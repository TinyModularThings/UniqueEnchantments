package uniquebase;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.BaseUEMod;
import uniquebase.api.IKeyBind;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.handler.BaseHandler;
import uniquebase.handler.ClientProxy;
import uniquebase.handler.EnchantmentHandler;
import uniquebase.handler.PackHandler;
import uniquebase.handler.Proxy;
import uniquebase.networking.PacketHandler;

@Mod("uniquebase")
public class UEBase
{
	public static Logger LOGGER = LogManager.getLogger("UE");
	public static final PacketHandler NETWORKING = new PacketHandler();
	public static final Proxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> Proxy::new);
	public static IKeyBind ENCHANTMENT_GUI = IKeyBind.empty();
	public static IKeyBind ENCHANTMENT_ICONS = IKeyBind.empty();
	
//	public static final Object2ObjectMap<ResourceLocation, ColorConfig> COLOR_SETTINGS = new Object2ObjectLinkedOpenHashMap<>();
//	public static ConfigValue<List<? extends String>> COLOR_CONFIGS;
//	public static BooleanValue ITEM_COLORING_ENABLED;
//	public static Object2IntMap<ResourceLocation> ENCHANTMENT_LIMITS = new Object2IntOpenHashMap<>();
//	public static IdStat<Enchantment> ENCHANTMENT_LIMIT_BLACKLIST = new IdStat<>("enchantment_limit_blacklist", "Allows to Exclude the Enchantments from the Enchantment limit. This has a Performance hit", ForgeRegistries.ENCHANTMENTS);
//	public static IntValue ENCHANTMENT_LIMIT_DEFAULT;
//	public static ConfigValue<List<? extends String>> ENCHANTMENT_LIMITS_CONFIGS;
//	public static Object2IntMap<ResourceLocation> ENCHANTMENT_PRIORITY = new Object2IntOpenHashMap<>();
//	public static ConfigValue<List<? extends String>> ENCHANTMENT_PRIORITY_CONFIGS;
//	public static BooleanValue SORT_ENCHANTMENT_TOOLTIP;
//	
//	public static IntValue VIEW_COOLDOWN;
//	public static BooleanValue HIDE_ENCHANTMENTS;
//	public static BooleanValue ENCHANTED_GLINT;
//	public static BooleanValue HIDE_CURSES;
//	public static BooleanValue SHOW_DESCRIPTION;
//	public static BooleanValue SHOW_NON_BOOKS;
//	public static IntValue TOOLTIPS_FLAGS;
//	public static BooleanValue LOG_BROKEN_MODS;
//	public static final IdStat<Item> ATTRIBUTES = new IdStat<>("attribute_activators", ForgeRegistries.ITEMS, Items.BELL);
//	
//	public static BooleanValue ICONS;
//	public static BooleanValue ICONS_VISIBLE;
//	public static EnumValue<VisibilityMode> ICON_MODE;
//	public static IntValue LIMIT_AMOUNT;
//	public static IntValue ICON_ROWS;
//	public static IntValue ICON_ROW_ELEMENTS;
//	public static IntValue ICON_CYCLE_TIME;
//	
//	public static BooleanValue XP_OVERRIDE_ENCHANT;
//	public static DoubleValue XP_MULTIPLIER_ENCHANT;
//	public static BooleanValue XP_OVERRIDE_ANVIL;
//	public static DoubleValue XP_MULTIPLIER_ANVIL;
//	public static DoubleValue PROTECTION_MULTIPLIER;
//	
//	public static final IdStat<Item> APPLICABLE_ICON_OVERRIDE = new IdStat<>("overrideIcons", "override that decides which items are used to decide to show in the tooltip display. If Empty all items are used", ForgeRegistries.ITEMS);
	
	public static BooleanValue DISABLE_JEI;
	public static ForgeConfigSpec CONFIG;
	
	public static boolean SHOULD_LOAD_RESOURCEPACK = false;
	
	public static final SoundEvent ANVIL_STACK = new SoundEvent(new ResourceLocation("uniquebase", "anvil_stacks"));
	
	public UEBase()
	{
		boolean addonsLoaded = ModList.get().isLoaded("uniquee") || ModList.get().isLoaded("uniqueapex") || ModList.get().isLoaded("uniquebattle") || ModList.get().isLoaded("uniqueutil");
		ENCHANTMENT_GUI = PROXY.registerKey("Enchantment Gui", 342);
		ENCHANTMENT_ICONS = PROXY.registerKey("Enchantment Icons", 342);
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		BaseConfig.TWEAKS.load(builder, addonsLoaded);
		BaseConfig.BOOKS.load(builder, addonsLoaded);
		BaseConfig.ICONS.load(builder, addonsLoaded);
		BaseConfig.TOOLTIPS.load(builder, addonsLoaded);
		builder.push("jei");
		builder.comment("If the JEI plugin should be disabled");
		DISABLE_JEI = builder.define("Disable JEI Plugin", false);		
		builder.pop();
		CONFIG = builder.build();
		BaseUEMod.validateConfigFolder();
		ModLoadingContext.get().registerConfig(Type.COMMON, CONFIG, "ue/UEBase.toml");
		MinecraftForge.EVENT_BUS.register(BaseHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(EnchantmentHandler.INSTANCE);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		PROXY.preInit(bus);
		bus.addListener(this::onLoad);
		bus.addListener(this::onFileChange);
		bus.register(this);
		
		ForgeRegistries.SOUND_EVENTS.register("anvil_stacks", ANVIL_STACK);
		Path path = FMLPaths.CONFIGDIR.get().resolve("ue").resolve("resourcePack.txt");
		if(Files.notExists(path))
		{
			try { Files.createDirectories(path.getParent()); }
			catch(IOException e1) {}
			SHOULD_LOAD_RESOURCEPACK = true;
			try(BufferedWriter writer = Files.newBufferedWriter(path))
			{
				writer.write("This File only exists so the new Unique Enchantments Enchanted Book texture pack is loaded by default but still can be turned off. Since neither Vanilla or forge are capable of having defaulty enabled ResourcePacks and forges config sucks so bad that you can't have configs that load before texture packs load so we have to do this shitty solution to make it work!");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		PackHandler.loaded();
	}
	
    @SubscribeEvent
	public void postInit(FMLCommonSetupEvent setup) 
	{
		CropHarvestRegistry.INSTANCE.init();
		PROXY.init();
	}
    
	protected void reloadConfig()
    {
		EnchantmentHandler.INSTANCE.cleanCache();
    }
    
    public void onLoad(ModConfigEvent.Loading configEvent) 
    {
    	reloadConfig();
    }

    public void onFileChange(ModConfigEvent.Reloading configEvent) 
    {
    	reloadConfig();
    }
}
