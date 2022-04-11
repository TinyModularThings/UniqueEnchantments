package uniquebase;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.ColorConfig;
import uniquebase.api.IKeyBind;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.handler.BaseHandler;
import uniquebase.handler.ClientProxy;
import uniquebase.handler.EnchantmentHandler;
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
	
	public static final Object2ObjectMap<Enchantment, ColorConfig> COLOR_SETTINGS = new Object2ObjectLinkedOpenHashMap<>();
	public static ConfigValue<List<? extends String>> COLOR_CONFIGS;
	public static Object2IntMap<Item> ENCHANTMENT_LIMITS = new Object2IntOpenHashMap<>();
	public static ConfigValue<List<? extends String>> ENCHANTMENT_LIMITS_CONFIGS;
	public static IntValue VIEW_COOLDOWN;
	public static BooleanValue ENCHANTED_GLINT;
	public static BooleanValue HIDE_CURSES;
	public static BooleanValue SHOW_DESCRIPTION;
	public static BooleanValue SHOW_NON_BOOKS;

	
	public static BooleanValue ICONS;
	public static IntValue ICON_ROWS;
	public static IntValue ICON_ROW_ELEMENTS;
	public static IntValue ICON_CYCLE_TIME;
	
	public static BooleanValue XP_OVERRIDE_ENCHANT;
	public static DoubleValue XP_MULTIPLIER_ENCHANT;
	public static BooleanValue XP_OVERRIDE_ANVIL;
	public static DoubleValue XP_MULTIPLIER_ANVIL;
	
	public static ForgeConfigSpec CONFIG;	
	
	public UEBase()
	{
		ENCHANTMENT_GUI = PROXY.registerKey("Enchantment Gui", 342);
		ENCHANTMENT_ICONS = PROXY.registerKey("Enchantment Icons", 342);
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("general");
		builder.comment("Allows to set how long it takes to view the enchantments on");
		VIEW_COOLDOWN = builder.defineInRange("View Enchantments Cooldown", 40, 1, 1200);
		builder.comment("Allows to control if enchanted books have the glint. This is needed for the new texture looking better");
		ENCHANTED_GLINT = builder.define("Enchanted Glint", false);
		builder.comment("Enables the feature that Enchantment Tables take Levels worth of XP instead of XPLevels, this affects only consumtion not checks");
		XP_OVERRIDE_ENCHANT = builder.define("Enchanting Table XP override", false);
		builder.comment("Allows modify the conversion rate from Level to XP points. This can result in consuming more then the player actually has");
		XP_MULTIPLIER_ENCHANT = builder.defineInRange("Enchanting Table XP multiplier", 1D, 0.1D, 1000D);
		builder.comment("Enables the feature that Anvils take Levels worth of XP instead of XPLevels, this affects only consumtion not checks");
		XP_OVERRIDE_ANVIL = builder.define("Anvil XP override", false);
		builder.comment("Allows modify the conversion rate from Level to XP points. This can result in consuming more then the player actually has");
		XP_MULTIPLIER_ANVIL = builder.defineInRange("Anvil XP multiplier", 1D, 0.1D, 1000D);
		builder.comment("Allows to limit how many Enchantments can be put on to a Item. Excess gets deleted", 
				"Format: ItemRegistryName;MaxEnchantment (example: minecraft:diamond;2");
		ENCHANTMENT_LIMITS_CONFIGS = builder.defineList("Item Enchantment Limits", ObjectLists.emptyList(), T -> true);
		builder.pop();
		builder.push("tooltips");
		builder.comment("Hides curses from items until shift is pressed");
		HIDE_CURSES = builder.define("Hide Curses", false);
		builder.comment("Shows Enchantment descriptions on items. Automatically disabled if said mod is detected");
		SHOW_DESCRIPTION = builder.define("Show Description", true);
		builder.comment("If Enchantment Descriptions should not be exclusive to Books");
		SHOW_NON_BOOKS = builder.define("Show on Items", false);
		builder.comment("If valid Target Icons should be shown");
		ICONS = builder.define("Enchantment Icons", true);
		builder.comment("How many Icon Rows should exists");
		ICON_ROWS = builder.defineInRange("Enchantment Icon Rows", 3, 1, 100);
		builder.comment("How many Icons per Row should be Shown");
		ICON_ROW_ELEMENTS = builder.defineInRange("Enchantment Icon Row Elements", 18, 1, 100);
		builder.comment("How Long it should take to cycle Icons if there is to many, in Ticks");
		ICON_CYCLE_TIME = builder.defineInRange("Cycle Time", 40, 5, 10000);
		builder.pop();
		builder.comment("Useful tool can be found here https://hugabor.github.io/color-picker/ for help with colors. Name uses RGB hex, Tooltip uses RGBA hex");
		builder.push("Coloring");
		builder.comment("Allows to override colors of Enchantment Text, Tooltip Border/Background of each individual Enchantment", 
				"Format: EnchantmentRegistryId;TextColor;BackgroundColor;BorderColorTop;BorderColorBottom",
				"Supports RGBA and expects a # or 0x at the beginning of the color string");
		COLOR_CONFIGS = builder.defineList("enchantmentColors", ColorConfig.createColorConfig(), T -> true);
		builder.pop();
		
		CONFIG = builder.build();
		ModLoadingContext.get().registerConfig(Type.COMMON, CONFIG, "UEBase.toml");
		MinecraftForge.EVENT_BUS.register(BaseHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(EnchantmentHandler.INSTANCE);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		
		bus.addListener(this::onLoad);
		bus.addListener(this::onFileChange);
		
		bus.register(this);
		
		COLOR_SETTINGS.defaultReturnValue(new ColorConfig());
		ENCHANTMENT_LIMITS.defaultReturnValue(Integer.MAX_VALUE);
	}
	
    @SubscribeEvent
	public void postInit(FMLCommonSetupEvent setup) 
	{
		CropHarvestRegistry.INSTANCE.init();
		PROXY.init();
	}
    
	protected void reloadConfig()
    {
		COLOR_SETTINGS.clear();
		List<? extends String> list = COLOR_CONFIGS.get();
		for (int i = 0; i < list.size(); i++) {
			String[] split = list.get(i).split(";");
			if(split.length < 2) continue;
			ColorConfig color = ColorConfig.fromText(split);
			if(color != null) {
				Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(split[0]));
				if(ench != null) COLOR_SETTINGS.put(ench, color);
			}
		}
		ENCHANTMENT_LIMITS.clear();
		list = ENCHANTMENT_LIMITS_CONFIGS.get();
		for(int i = 0; i < list.size(); i++) {
			String[] split = list.get(i).split(";");
			if(split.length != 2) continue;
			Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(split[0]));
			if(item != null) {
				try { ENCHANTMENT_LIMITS.put(item, Integer.parseInt(split[1])); }
				catch(Exception e) { UEBase.LOGGER.info("Failed To load: "+list.get(i)+", Error: "+e); }
			}
		}
    }
    
    public void onLoad(ModConfig.Loading configEvent) 
    {
    	reloadConfig();
    }

    public void onFileChange(ModConfig.Reloading configEvent) 
    {
    	reloadConfig();
    }
}
