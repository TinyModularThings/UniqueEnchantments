package uniquebase.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public abstract class BaseUEMod
{
	private static final ThreadLocal<Boolean> CHECKING = ThreadLocal.withInitial(() -> false);
	public static final EnchantmentCategory ALL_TYPES = EnchantmentCategory.create("ANY", BaseUEMod::canEnchant);
	static final List<BaseUEMod> ALL_MODS = ObjectLists.synchronize(new ObjectArrayList<>());
	List<IToggleEnchantment> enchantments = new ObjectArrayList<>();
	ObjectList<EnchantedUpgrade> upgrades = new ObjectArrayList<>();
	public ForgeConfigSpec config;

	public BaseUEMod()
	{
		ALL_MODS.add(this);
	}
	
	public static boolean containsMod(BaseUEMod mod)
	{
		return ALL_MODS.contains(mod);
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
		bus.addListener(this::registerEnchantments);
		bus.addListener(this::onLoad);
		bus.addListener(this::onFileChange);
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("general");
		addConfig(builder);
		loadEnchantments();
		loadUpgrades();
		for(int i = 0,m=upgrades.size();i<m;i++)
		{
			upgrades.get(i).register();
		}
		for(IToggleEnchantment ench : enchantments)
		{
			ForgeRegistries.ENCHANTMENTS.register(ench.getId(), (Enchantment)ench);
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
	
	
	protected void addConfig(ForgeConfigSpec.Builder builder) {}
	
	protected void registerUpgrade(EnchantedUpgrade upgrades)
	{
		this.upgrades.add(upgrades);
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
    
	public void registerEnchantments(RegisterEvent event)
	{
//		if(event.getRegistryKey().equals(ForgeRegistries.Keys.ENCHANTMENTS))
//		{
//			for(IToggleEnchantment ench : enchantments)
//			{
//				event.getForgeRegistry().register(ench.getId(), (Enchantment)ench);
//			}
//		}
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
