package uniquebase;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
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
import uniquebase.api.ColorConfig;
import uniquebase.api.IKeyBind;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.handler.BaseHandler;
import uniquebase.handler.ClientProxy;
import uniquebase.handler.EnchantmentHandler;
import uniquebase.handler.PackHandler;
import uniquebase.handler.Proxy;
import uniquebase.networking.PacketHandler;
import uniquebase.utils.IdStat;
import uniquebase.utils.VisibilityMode;

@Mod("uniquebase")
public class UEBase
{
	public static Logger LOGGER = LogManager.getLogger("UE");
	public static final PacketHandler NETWORKING = new PacketHandler();
	public static final Proxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> Proxy::new);
	public static IKeyBind ENCHANTMENT_GUI = IKeyBind.empty();
	public static IKeyBind ENCHANTMENT_ICONS = IKeyBind.empty();
	
	public static final Object2ObjectMap<ResourceLocation, ColorConfig> COLOR_SETTINGS = new Object2ObjectLinkedOpenHashMap<>();
	public static ConfigValue<List<? extends String>> COLOR_CONFIGS;
	public static BooleanValue ITEM_COLORING_ENABLED;
	public static Object2IntMap<ResourceLocation> ENCHANTMENT_LIMITS = new Object2IntOpenHashMap<>();
	public static IdStat<Enchantment> ENCHANTMENT_LIMIT_BLACKLIST = new IdStat<>("enchantment_limit_blacklist", "Allows to Exclude the Enchantments from the Enchantment limit. This has a Performance hit", ForgeRegistries.ENCHANTMENTS);
	public static IntValue ENCHANTMENT_LIMIT_DEFAULT;
	public static ConfigValue<List<? extends String>> ENCHANTMENT_LIMITS_CONFIGS;
	public static Object2IntMap<ResourceLocation> ENCHANTMENT_PRIORITY = new Object2IntOpenHashMap<>();
	public static ConfigValue<List<? extends String>> ENCHANTMENT_PRIORITY_CONFIGS;
	public static BooleanValue SORT_ENCHANTMENT_TOOLTIP;
	
	public static IntValue VIEW_COOLDOWN;
	public static BooleanValue HIDE_ENCHANTMENTS;
	public static BooleanValue ENCHANTED_GLINT;
	public static BooleanValue HIDE_CURSES;
	public static BooleanValue SHOW_DESCRIPTION;
	public static BooleanValue SHOW_NON_BOOKS;
	public static IntValue TOOLTIPS_FLAGS;
	public static BooleanValue LOG_BROKEN_MODS;
	public static final IdStat<Item> ATTRIBUTES = new IdStat<>("attribute_activators", ForgeRegistries.ITEMS, Items.BELL);
	
	public static BooleanValue ICONS;
	public static BooleanValue ICONS_VISIBLE;
	public static EnumValue<VisibilityMode> ICON_MODE;
	public static IntValue LIMIT_AMOUNT;
	public static IntValue ICON_ROWS;
	public static IntValue ICON_ROW_ELEMENTS;
	public static IntValue ICON_CYCLE_TIME;
	
	public static BooleanValue XP_OVERRIDE_ENCHANT;
	public static DoubleValue XP_MULTIPLIER_ENCHANT;
	public static BooleanValue XP_OVERRIDE_ANVIL;
	public static DoubleValue XP_MULTIPLIER_ANVIL;
	public static DoubleValue PROTECTION_MULTIPLIER;
	
	public static BooleanValue DISABLE_JEI;
	public static final IdStat<Item> APPLICABLE_ICON_OVERRIDE = new IdStat<>("overrideIcons", "override that decides which items are used to decide to show in the tooltip display. If Empty all items are used", ForgeRegistries.ITEMS);
	
	public static ForgeConfigSpec CONFIG;
	
	public static boolean SHOULD_LOAD_RESOURCEPACK = false;
	
	public static final SoundEvent ANVIL_STACK = new SoundEvent(new ResourceLocation("uniquebase", "anvil_stacks"));
	
	public UEBase()
	{
		boolean addonsLoaded = ModList.get().isLoaded("uniquee") || ModList.get().isLoaded("uniqueapex") || ModList.get().isLoaded("uniquebattle") || ModList.get().isLoaded("uniqueutil");
		ENCHANTMENT_GUI = PROXY.registerKey("Enchantment Gui", 342);
		ENCHANTMENT_ICONS = PROXY.registerKey("Enchantment Icons", 342);
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("general");
		builder.comment("Allows to set how long it takes to view the enchantments on");
		VIEW_COOLDOWN = builder.defineInRange("View Enchantments Cooldown", 40, 1, 1200);
		builder.comment("Allows to control if enchanted books have the glint. This is needed for the new texture looking better");
		ENCHANTED_GLINT = builder.define("Enchanted Glint", false);
		builder.comment("The multiplier for the Protection Tweaks we did, the higher this value the stronger they get");
		PROTECTION_MULTIPLIER = builder.defineInRange("protection_multiplier", 0.003875d, 0.0d, Double.MAX_VALUE);
		builder.comment("Enables the feature that Enchantment Tables take Levels worth of XP instead of XPLevels, this affects only consumtion not checks");
		XP_OVERRIDE_ENCHANT = builder.define("Enchanting Table XP override", false);
		builder.comment("Allows modify the conversion rate from Level to XP points. This can result in consuming more then the player actually has");
		XP_MULTIPLIER_ENCHANT = builder.defineInRange("Enchanting Table XP multiplier", 1D, 0.1D, 1000D);
		builder.comment("Enables the feature that Anvils take Levels worth of XP instead of XPLevels, this affects only consumtion not checks");
		XP_OVERRIDE_ANVIL = builder.define("Anvil XP override", false);
		builder.comment("Allows modify the conversion rate from Level to XP points. This can result in consuming more then the player actually has");
		XP_MULTIPLIER_ANVIL = builder.defineInRange("Anvil XP multiplier", 1D, 0.1D, 1000D);
		builder.comment("The default limit for each Item, if not further specified in the List");
		ENCHANTMENT_LIMIT_DEFAULT = builder.defineInRange("Item Enchantment Limit Default", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
		builder.comment("Allows to limit how many Enchantments can be put on to a Item. Excess gets deleted", 
				"Format: ItemRegistryName;MaxEnchantment (example: minecraft:diamond;2)");
		ENCHANTMENT_LIMITS_CONFIGS = builder.defineList("Item Enchantment Limits", ObjectLists.emptyList(), T -> true);
		ENCHANTMENT_LIMIT_BLACKLIST.handleConfig(builder);
		builder.comment("Enable the logging of Mods that create Invalid ItemStacks that will crash the game");
		LOG_BROKEN_MODS = builder.define("Log Invalid ItemStacks", true);
		ATTRIBUTES.handleConfig(builder);
		builder.pop();
		builder.push("tooltips");
		APPLICABLE_ICON_OVERRIDE.handleConfig(builder);
		builder.comment("Hides Enchantments on Non Enchanted Books until Shift is held");
		//For anyone screaming WHAT THE FUCK IS THIS... Basically UEBase alone shouldn't be so game changing so this is a compromise.
		HIDE_ENCHANTMENTS = builder.define("hide_enchantments", addonsLoaded);
		
		builder.comment("Enables if Enchantment Tooltips are sorted by a Priority. This is Client only and might confuse with enchantment removing/extracting mods");
		SORT_ENCHANTMENT_TOOLTIP = builder.define("sort_enchantment_tooltips", addonsLoaded);
		builder.comment("Allows to sort Enchantment Entries by a desired order. Format: EnchantmentRegistryName;priority (example: minecraft:fortune;25)");
		ENCHANTMENT_PRIORITY_CONFIGS = builder.defineList("Enchantment Tooltip Order", ObjectLists.emptyList(), T -> true);
		builder.comment("Allows to control which Keybind Tooltips are displayed, 1 => Description, 2 => Icons, 4 => View, they can be added together if wanted.", "This won't disable functionality just hide the keybinding tooltip itself");
		TOOLTIPS_FLAGS = builder.defineInRange("Visible Tooltips", 7, 0, 7);
		builder.comment("Hides curses from items until shift is pressed");
		HIDE_CURSES = builder.define("Hide Curses", false);
		builder.comment("Shows Enchantment descriptions on items. Automatically disabled if said mod is detected");
		SHOW_DESCRIPTION = builder.define("Show Description", true);
		builder.comment("If Enchantment Descriptions should not be exclusive to Books");
		SHOW_NON_BOOKS = builder.define("Show on Items", false);
		builder.comment("If valid Target Icons should be shown");
		ICONS = builder.define("Enchantment Icons", true);
		builder.comment("If Icons should be displayed. Can be toggled ingame with a key");
		ICONS_VISIBLE = builder.define("Enchantment Icons Visible", false);
		builder.comment("Decide how agressive the icon filter is", "Limited => tries to limit the amount of icons visible drastically", "Normal => is a reduced amount but everything important is still shown", "Everything => is for the crazy people out there and shows everything");
		ICON_MODE = builder.defineEnum("Enchantment Icon Mode", VisibilityMode.LIMITED);
		builder.comment("Icon Limit defines how many icons of each extra category are shown at once");
		LIMIT_AMOUNT = builder.defineInRange("Icon Limit", 4, 0, 25);
		builder.comment("How many Icon Rows should exists");
		ICON_ROWS = builder.defineInRange("Enchantment Icon Rows", 2, 1, 100);
		builder.comment("How many Icons per Row should be Shown");
		ICON_ROW_ELEMENTS = builder.defineInRange("Enchantment Icon Row Elements", 9, 1, 100);
		builder.comment("How Long it should take to cycle Icons if there is to many, in Ticks");
		ICON_CYCLE_TIME = builder.defineInRange("Cycle Time", 40, 5, 10000);
		builder.pop();
		builder.comment("Useful tool can be found here https://hugabor.github.io/color-picker/ for help with colors. Name uses RGB hex, Tooltip uses RGBA hex");
		builder.push("Coloring");
		builder.comment("Allows to override colors of Enchantment Text, Tooltip Border/Background of each individual Enchantment", 
				"Format: EnchantmentRegistryId;TextColor;BackgroundColor;BorderColorTop;BorderColorBottom",
				"Supports RGBA and expects a # or 0x at the beginning of the color string");
		COLOR_CONFIGS = builder.defineList("enchantmentColors", ColorConfig::createColorConfig, T -> true);
		builder.comment("Toggle for Item Overlay Coloring of Enchanted Books so if the texture is disabled you can turn this optioanlly of too so we don't ruin your texture");
		ITEM_COLORING_ENABLED = builder.define("Enable Item Coloring", true);
		builder.pop();
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
		
		COLOR_SETTINGS.defaultReturnValue(new ColorConfig());
		ENCHANTMENT_LIMITS.defaultReturnValue(Integer.MAX_VALUE);
		
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
    
    public static ColorConfig getEnchantmentColor(Enchantment ench) {
    	return COLOR_SETTINGS.get(ench == null ? null : ForgeRegistries.ENCHANTMENTS.getKey(ench));
    }
    
	protected void reloadConfig()
    {
		EnchantmentHandler.INSTANCE.cleanCache();
		APPLICABLE_ICON_OVERRIDE.onConfigChanged();
		ENCHANTMENT_LIMIT_BLACKLIST.onConfigChanged();
		ATTRIBUTES.onConfigChanged();
		COLOR_SETTINGS.clear();
		List<? extends String> list = COLOR_CONFIGS.get();
		for (int i = 0; i < list.size(); i++) {
			String[] split = list.get(i).split(";");
			if(split.length < 2) continue;
			ColorConfig color = ColorConfig.fromText(split);
			if(color != null) {
				ResourceLocation ench = ResourceLocation.tryParse(split[0]);
				if(ench != null) COLOR_SETTINGS.put(ench, color);
			}
		}
		ENCHANTMENT_LIMITS.clear();
		ENCHANTMENT_LIMITS.defaultReturnValue(ENCHANTMENT_LIMIT_DEFAULT.get());
		list = ENCHANTMENT_LIMITS_CONFIGS.get();
		for(int i = 0; i < list.size(); i++) {
			String[] split = list.get(i).split(";");
			if(split.length != 2) continue;
			ResourceLocation item = ResourceLocation.tryParse(split[0]);
			if(item != null) {
				try { ENCHANTMENT_LIMITS.put(item, Integer.parseInt(split[1])); }
				catch(Exception e) { UEBase.LOGGER.info("Failed To load: "+list.get(i)+", Error: "+e); }
			}
		}
		ENCHANTMENT_PRIORITY.clear();
		ENCHANTMENT_PRIORITY.defaultReturnValue(1);
		list = ENCHANTMENT_PRIORITY_CONFIGS.get();
		for(int i = 0; i < list.size(); i++) {
			String[] split = list.get(i).split(";");
			if(split.length != 2) continue;
			ResourceLocation item = ResourceLocation.tryParse(split[0]);
			if(item != null) {
				try { ENCHANTMENT_PRIORITY.put(item, Integer.parseInt(split[1])); }
				catch(Exception e) { UEBase.LOGGER.info("Failed To load: "+list.get(i)+", Error: "+e); }
			}
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
}
