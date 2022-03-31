package uniquebase;

import java.util.ArrayList;
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
import uniquebase.handler.Proxy;
import uniquebase.networking.PacketHandler;
import uniquebase.utils.NameEnum;
import uniquebase.utils.NameString;

@Mod("uniquebase")
public class UEBase
{
	public static Logger LOGGER = LogManager.getLogger("UE");
	public static final PacketHandler NETWORKING = new PacketHandler();
	public static final Proxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> Proxy::new);
	public static IKeyBind ENCHANTMENT_GUI = IKeyBind.empty();
	
	public static ForgeConfigSpec config;

	public static BooleanValue obfuscate;
	public static ConfigValue<List<? extends String>> color;
	
	public static DoubleValue entityChance;
	public static ConfigValue<List<? extends String>> persons;
	public static DoubleValue rarityChance;
	public static ConfigValue<List<? extends String>> adjectives;
	public static ConfigValue<List<? extends String>> names;
	public static DoubleValue locationChance;
	public static ConfigValue<List<? extends String>> suffix;
	
	
	public static List<String> defVal = new ArrayList<String>();
	
	public static Object2IntMap<Enchantment> colorMap = new Object2IntOpenHashMap<>();
	
	
	
	
	public UEBase()
	{
		
		ENCHANTMENT_GUI = PROXY.registerKey("Enchantment Gui", 342);
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("obfuscator");
		obfuscate = builder.define("obfuscate", true);
		builder.pop();
		
		builder.push("Enchantment Coloring");
		color = builder.defineList("enchantmentColor", ObjectArrayList.wrap(new String[]{"minecraft:sharpness;#32f094"}), T -> true);
		builder.pop();
		
		builder.push("Item Name Generator");
		builder.comment("Person/Entity|Rarity|Adjective|ItemName|Suffix/Location \nWhen they share a Position, the Chance based will have priority");
		entityChance = builder.defineInRange("Entity Inheritor Chance", 0.1, 0, 1);
		persons = builder.defineList("Person Names", ObjectArrayList.wrap(new String[]{"Xaikii's;0;8;all", "Speiger's;0;8;sword", "Samiel's;6;8;all", "Robo's; 0,3,chest", "Waluigi's;0;8;all", "Cowardly Dog;2;6;all", "Dreams';7;8;all"}), T -> true);
		rarityChance = builder.defineInRange("Rarity Chance", 0.25, 0.0, 1.0);
		adjectives = builder.defineList("Adjectives", ObjectArrayList.wrap(new String[]{"pathetic;0;4;all", "dominating;1;8;sword", "pixelated;2;6;all", "reading;2;5;all", "time manipulating;4;7;all", "cursed;0;8;all", "extra ordinary;2;8;all", "uncompensated;3;8;sword,tools", "fragile;0;8;all", "powerful;4;8;sword,trident,tools,bow,crossbow", "heretical;5;8;all", "human sized;0;8;all", "absolute;7;8;all"}), T -> true);
		names = builder.defineList("Item Names", ObjectArrayList.wrap(new String[]{"Toothpick;0;8;sword,trident,tool", "Yamato;0;8;sword", "Glassmail;0;1;armor", "Hell Vest;5;8;chest", "Poncho;0;3;chest", "Drill;5;7;pickaxe", "Summer Cap;2;4;helmet", "Glasses;1;4;helmet", "Piece of Scrap;0;6;sword,shovel", "Broken Ankh;3;6;sword", "Sharp Stick;1;3;sword", "Bat;0;7;sword", "Rack;3;7;sword,tools", "Mallet;0;8;sword,axe", "Peacemaker;7;8;sword,trident,bow,crossbow,tools"}), T -> true);
		locationChance = builder.defineInRange("Location Chance", 0.1, 0, 1);
		suffix = builder.defineList("Suffix", ObjectArrayList.wrap(new String[]{"of Destruction;6;8;sword", "the mild incovenience;0;8;all", "of neverworking;0;8;all", "of strong worded complaints;2;8;all", "of blunt force trauma;4;8;sword,tools", "of visual acuity;0;3;helmet", "of Shenanigans;3;8;all", "of eternal Gratitude;4;8;all"}), T -> true);
		builder.pop();
			
		config = builder.build();
		ModLoadingContext.get().registerConfig(Type.COMMON, config, "UEBase.toml");
		MinecraftForge.EVENT_BUS.register(BaseHandler.INSTANCE);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		NameString.fillMap(persons, NameEnum.PERSON);
		NameString.fillMap(adjectives, NameEnum.ADJECTIVE);
		NameString.fillMap(names, NameEnum.NAME);
		NameString.fillMap(suffix, NameEnum.SUFFIX);
		
		NameString.printer();
		
		
		bus.addListener(this::onLoad);
		bus.addListener(this::onFileChange);
		
		bus.register(this);
		
		colorMap.defaultReturnValue(-1);
	}
	
    @SubscribeEvent
	public void postInit(FMLCommonSetupEvent setup) 
	{
		CropHarvestRegistry.INSTANCE.init();
	}
    
    @SuppressWarnings("deprecation")
	protected void reloadConfig()
    {
    	colorMap.clear();
		List<? extends String> list = color.get();
		for (int i = 0; i < list.size(); i++) {
			String[] split = list.get(i).split(";");
			if(split.length == 2) {
				colorMap.put(ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(split[0])), Integer.decode(split[1]));
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
