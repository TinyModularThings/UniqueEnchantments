package uniquebase;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.enchantment.Enchantment;
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
import uniquebase.api.IKeyBind;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.handler.BaseHandler;
import uniquebase.handler.ClientProxy;
import uniquebase.handler.EnchantmentHandler;
import uniquebase.handler.Proxy;
import uniquebase.handler.flavor.Flavor;
import uniquebase.handler.flavor.FlavorTarget;
import uniquebase.handler.flavor.ItemType;
import uniquebase.networking.PacketHandler;

@Mod("uniquebase")
public class UEBase
{
	public static Logger LOGGER = LogManager.getLogger("UE");
	public static final PacketHandler NETWORKING = new PacketHandler();
	public static final Proxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> Proxy::new);
	public static IKeyBind ENCHANTMENT_GUI = IKeyBind.empty();
	private static final List<Runnable> CONFIG_RELOAD_TASKS = new ObjectArrayList<>();

	public static final Object2IntMap<Enchantment> COLOR_MAP = new Object2IntOpenHashMap<>();
	public static ConfigValue<List<? extends String>> COLOR_CONFIG;
	
	public static IntValue VIEW_COOLDOWN;
	public static BooleanValue ICONS;
	public static IntValue ICON_ROWS;
	public static IntValue ICON_ROW_ELEMENTS;
	public static IntValue ICON_CYCLE_TIME;
	public static BooleanValue ENCHANTED_GLINT;
	
	public static ForgeConfigSpec CONFIG;

	public static BooleanValue IS_OBFUSCATED;
	
	public static DoubleValue ENTITY_NAME_CHANCE;
	public static ConfigValue<List<? extends String>> PEOPLE_LIST;
	public static DoubleValue RARITY_NAME_CHANCE;
	public static ConfigValue<List<? extends String>> ADJECTIVES_LIST;
	public static ConfigValue<List<? extends String>> NAMES_LIST;
	public static DoubleValue LOCATION_NAME_CHANCE;
	public static ConfigValue<List<? extends String>> SUFFIX_LIST;
	
	
	
	public UEBase()
	{
		ENCHANTMENT_GUI = PROXY.registerKey("Enchantment Gui", 342);
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("obfuscator");
		IS_OBFUSCATED = builder.define("obfuscate", true);
		VIEW_COOLDOWN = builder.defineInRange("enchantment_view_cooldown", 40, 1, 1200);
		ENCHANTED_GLINT = builder.define("enchanted_glint", false);
		builder.pop();
		//TODO sort this properly
		builder.push("Enchantment Coloring");
		COLOR_CONFIG = builder.defineList("enchantmentColor", ObjectArrayList.wrap(new String[]{"minecraft:sharpness;#32f094"}), T -> true);
		builder.pop();
		builder.push("Enchantment Icons");
		ICONS = builder.define("enable", true);
		ICON_ROWS = builder.defineInRange("rows", 3, 1, 100);
		ICON_ROW_ELEMENTS = builder.defineInRange("row_elements", 18, 1, 100);
		ICON_CYCLE_TIME = builder.defineInRange("cycleTime", 40, 5, 10000);
		builder.pop();
		
		builder.push("Item Name Generator");
		builder.comment("Person/Entity|Rarity|Adjective|ItemName|Suffix/Location \nWhen they share a Position, the Chance based will have priority");
		ENTITY_NAME_CHANCE = builder.defineInRange("Entity Inheritor Chance", 0.1, 0, 1);
		PEOPLE_LIST = builder.defineList("Person Names", ObjectArrayList.wrap(new String[]{"Xaikii's;0;8;all", "Speiger's;0;8;sword", "Samiel's;6;8;all", "Robo's; 0,3,chest", "Waluigi's;0;8;all", "Cowardly Dog;2;6;all", "Dreams';7;8;all"}), T -> true);
		RARITY_NAME_CHANCE = builder.defineInRange("Rarity Chance", 0.25, 0.0, 1.0);
		ADJECTIVES_LIST = builder.defineList("Adjectives", ObjectArrayList.wrap(new String[]{"pathetic;0;4;all", "dominating;1;8;sword", "pixelated;2;6;all", "reading;2;5;all", "time manipulating;4;7;all", "cursed;0;8;all", "extra ordinary;2;8;all", "uncompensated;3;8;sword,tools", "fragile;0;8;all", "powerful;4;8;sword,trident,tools,bow,crossbow", "heretical;5;8;all", "human sized;0;8;all", "absolute;7;8;all"}), T -> true);
		NAMES_LIST = builder.defineList("Item Names", ObjectArrayList.wrap(new String[]{"Toothpick;0;8;sword,trident,tool", "Yamato;0;8;sword", "Glassmail;0;1;armor", "Hell Vest;5;8;chest", "Poncho;0;3;chest", "Drill;5;7;pickaxe", "Summer Cap;2;4;helmet", "Glasses;1;4;helmet", "Piece of Scrap;0;6;sword,shovel", "Broken Ankh;3;6;sword", "Sharp Stick;1;3;sword", "Bat;0;7;sword", "Rack;3;7;sword,tools", "Mallet;0;8;sword,axe", "Peacemaker;7;8;sword,trident,bow,crossbow,tools"}), T -> true);
		LOCATION_NAME_CHANCE = builder.defineInRange("Location Chance", 0.1, 0, 1);
		SUFFIX_LIST = builder.defineList("Suffix", ObjectArrayList.wrap(new String[]{"of Destruction;6;8;sword", "the mild incovenience;0;8;all", "of neverworking;0;8;all", "of strong worded complaints;2;8;all", "of blunt force trauma;4;8;sword,tools", "of visual acuity;0;3;helmet", "of Shenanigans;3;8;all", "of eternal Gratitude;4;8;all"}), T -> true);
		builder.pop();
			
		CONFIG = builder.build();
		ModLoadingContext.get().registerConfig(Type.COMMON, CONFIG, "UEBase.toml");
		MinecraftForge.EVENT_BUS.register(BaseHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(EnchantmentHandler.INSTANCE);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		Flavor.fillMap(PEOPLE_LIST, FlavorTarget.PERSON);
		Flavor.fillMap(ADJECTIVES_LIST, FlavorTarget.ADJECTIVE);
		Flavor.fillMap(NAMES_LIST, FlavorTarget.NAME);
		Flavor.fillMap(SUFFIX_LIST, FlavorTarget.SUFFIX);
		
		Flavor.printer();
		
		
		bus.addListener(this::onLoad);
		bus.addListener(this::onFileChange);
		
		bus.register(this);
		
		COLOR_MAP.defaultReturnValue(-1);
	}
	
    @SubscribeEvent
	public void postInit(FMLCommonSetupEvent setup) 
	{
		CropHarvestRegistry.INSTANCE.init();
		ItemType.init();
		EnchantmentHandler.INSTANCE.init();
		PROXY.init();
	}
    
	protected void reloadConfig()
    {
    	CONFIG_RELOAD_TASKS.forEach(Runnable::run);
    	COLOR_MAP.clear();
		List<? extends String> list = COLOR_CONFIG.get();
		for (int i = 0; i < list.size(); i++) {
			String[] split = list.get(i).split(";");
			if(split.length == 2) {
				COLOR_MAP.put(ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(split[0])), Integer.decode(split[1]).intValue());
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
