package uniquebase.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import uniquebase.api.jei.EnchantmentTarget;

public abstract class BaseUEMod
{
	private static final ThreadLocal<Boolean> CHECKING = ThreadLocal.withInitial(() -> false);
	private static final Object SYNC_LOCK = new Object();
	public static final EnchantmentCategory ALL_TYPES = EnchantmentCategory.create("ANY", BaseUEMod::canEnchant);
	static final List<BaseUEMod> ALL_MODS = ObjectLists.synchronize(new ObjectArrayList<>());
	List<IToggleEnchantment> enchantments = new ObjectArrayList<>();
	ObjectList<EnchantedUpgrade> upgrades = new ObjectArrayList<>();
	Map<ResourceLocation, Tuple<BannerPattern, Item.Properties>> banners = new Object2ObjectLinkedOpenHashMap<>();
	List<EnchantmentTarget> targets = new ObjectArrayList<>();
	ILootModifier lootManager;
	
	public ForgeConfigSpec config;

	public BaseUEMod()
	{
		ALL_MODS.add(this);
	}
	
	public static boolean containsMod(BaseUEMod mod)
	{
		return ALL_MODS.contains(mod);
	}
	
	public static List<BaseUEMod> getAllMods()
	{
		return new ObjectArrayList<>(ALL_MODS);
	}
	
	public static void validateConfigFolder() {
		Path path = FMLPaths.CONFIGDIR.get().resolve("ue");
		try {
			if(Files.notExists(path)) {
				Files.createDirectories(path);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void init(IEventBus bus, String name)
	{
		bus.addListener(this::registerInternal);
		bus.addListener(this::onLoad);
		bus.addListener(this::onFileChange);
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("general");
		addConfig(builder);
		loadEnchantments();
		loadUpgrades();
		loadBanners();
		for(int i = 0,m=upgrades.size();i<m;i++)
		{
			upgrades.get(i).register();
		}
		synchronized(SYNC_LOCK)
		{
			for(IToggleEnchantment ench : enchantments)
			{
				ForgeRegistries.ENCHANTMENTS.register(ench.getId(), (Enchantment)ench);
			}
		}
		for(int i = 0,m=enchantments.size();i<m;i++)
		{
			enchantments.get(i).loadIncompats();
			enchantments.get(i).loadFromConfig(builder);
		}
		builder.pop();
		config = builder.build();
		validateConfigFolder();
		ModLoadingContext.get().registerConfig(Type.COMMON, config, "ue/"+name);
	}
	
	protected abstract void loadEnchantments();
	protected void loadUpgrades() {};
	protected void loadBanners() {};
	
	protected void addConfig(ForgeConfigSpec.Builder builder) {}
	
	protected void setLootManager(ILootModifier lootManager)
	{
		this.lootManager = lootManager;
	}
	
	public ILootModifier getLootManager()
	{
		return lootManager;
	}
	
	protected void addTarget(EnchantmentTarget target)
	{
		this.targets.add(target);
	}
	
	protected void registerUpgrade(EnchantedUpgrade upgrades)
	{
		this.upgrades.add(upgrades);
	}
	
	protected void registerPattern(String id, String name, String hash)
	{
		registerPattern(id, name, hash, new Item.Properties());
	}
	
	protected void registerPattern(String id, String name, String hash, Rarity rarity)
	{
		registerPattern(id, name, hash, new Item.Properties().rarity(rarity).tab(CreativeModeTab.TAB_MISC));
	}
	
	protected void registerPattern(String id, String name, String hash, Item.Properties props)
	{
		banners.put(new ResourceLocation(id, name), new Tuple<>(new BannerPattern(hash), props));
	}
	
	public void getBannerItems(Consumer<ItemStack> listener)
	{
		for(ResourceLocation id : banners.keySet())
		{
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id.getNamespace(), id.getPath()+"_banner_pattern"));
			if(item == null || item == Items.AIR) continue;
			listener.accept(new ItemStack(item));
		}
	}
	
	public List<EnchantmentTarget> getTargets()
	{
		return Collections.unmodifiableList(targets);
	}
	
	protected Enchantment register(Enchantment ench)
	{
		if(ench instanceof IToggleEnchantment)
		{
			enchantments.add((IToggleEnchantment)ench);
		}
		return ench;
	}
	
    protected void reloadConfig()
    {
    	for(int i = 0,m=enchantments.size();i<m;i++)
    	{
    		enchantments.get(i).onConfigChanged();
    	}
    }
    
    public void onLoad(ModConfigEvent.Loading configEvent) 
    {
    	reloadConfig();
    }

    public void onFileChange(ModConfigEvent.Reloading configEvent) 
    {
    	reloadConfig();
    }
    
	private void registerInternal(RegisterEvent event)
	{
		if(event.getRegistryKey().equals(Registry.BANNER_PATTERN_REGISTRY))
		{
			for(Map.Entry<ResourceLocation, Tuple<BannerPattern, Item.Properties>> entry : banners.entrySet())
			{
				Registry.register(Registry.BANNER_PATTERN, ResourceKey.create(Registry.BANNER_PATTERN_REGISTRY, entry.getKey()), entry.getValue().getA());
			}
		}
		else if(event.getRegistryKey().equals(Registry.ITEM_REGISTRY))
		{
			for(Map.Entry<ResourceLocation, Tuple<BannerPattern, Item.Properties>> entry : banners.entrySet())
			{
				ResourceLocation id = entry.getKey(); 
				Tuple<BannerPattern, Item.Properties> props = entry.getValue();
				TagKey<BannerPattern> tag = TagKey.create(Registry.BANNER_PATTERN_REGISTRY, new ResourceLocation(id.getNamespace(), "pattern_item/"+id.getPath()));
				event.getForgeRegistry().register(new ResourceLocation(id.getNamespace(), id.getPath()+"_banner_pattern"), new BannerPatternItem(tag, props.getB()));
			}
		}
	}
	
	private static boolean canEnchant(Item item)
	{
		if(CHECKING.get()) return false;
		CHECKING.set(true);
		for(EnchantmentCategory type : EnchantmentCategory.values())
		{
			if(type != ALL_TYPES && type.canEnchant(item))
			{
				CHECKING.set(false);
				return true;
			}
		}
		CHECKING.set(false);
		return false;
	}
}
