package uniquebase;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.ColorConfig;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.IdStat;
import uniquebase.utils.VisibilityMode;

public class BaseConfig
{
	public static final UEIcons ICONS = new UEIcons();
	public static final UETweaks TWEAKS = new UETweaks();
	public static final UETooltips TOOLTIPS = new UETooltips();
	public static final UEBooks BOOKS = new UEBooks();
	
	public static class UEIcons
	{
		public BooleanValue isEnabled;
		public BooleanValue isVisible;
		public EnumValue<VisibilityMode> visiblityMode;
		public IntValue iconLimit;
		public IntValue visibleRows;
		public IntValue visibleColumn;
		public IntValue cycleTime;
		public IdStat<Item> iconOverride = new IdStat<>("overrideIcons", "override that decides which items are used to decide to show in the tooltip display. If Empty all items are used", ForgeRegistries.ITEMS);

		public void load(ForgeConfigSpec.Builder configs, boolean addonsLoaded)
		{
			configs.push("icons");
			configs.comment("If Applicable tools should be displayed below the Enchantment");
			isEnabled = configs.define("Enable Applicable Tool-Icons", true);
			configs.comment("If Icons should be displayed. Can be toggled ingame with a key");
			isVisible = configs.define("Show Enchantment Icons", true);
			configs.comment("How many Icons should be visible underneath the Enchantment",
					"Everything: Shows every item that could have said Enchantment applied",
					"Normal: Applies a basic filter to remove duplicates",
					"Limited: Applies a aggressive filter to remove duplicates");
			visiblityMode = configs.defineEnum("Icon Filter Mode", VisibilityMode.LIMITED);
			configs.comment("Defines how many icons are visible at once in limited filter mode");
			iconLimit = configs.defineInRange("Limited Icon Limit", 4, 0, 25);
			configs.comment("Defines how many rows for the Enchantment Icons exist");
			visibleRows = configs.defineInRange("Enchantment Icon Rows", 2, 1, 100);;
			configs.comment("Defines how many columns for the Enchantment Icons exist");
			visibleColumn = configs.defineInRange("Enchantment Icon Columns", 9, 1, 100);
			configs.comment("Defines how many ticks should pass to cycle icons, if there is to many icons to display at once");
			cycleTime = configs.defineInRange("Cycle Time", 40, 5, 10000);
			iconOverride.handleConfig(configs);
			configs.pop();
		}
		
		public void onConfigsChanged()
		{
			iconOverride.onConfigChanged();
		}
	}
	
	public static class UETooltips
	{
		public IntValue viewCooldowns;
		public BooleanValue hideEnchantments;
		public BooleanValue sortEnchantments;
		public BooleanValue hideCurses;
		public BooleanValue showDescription;
		public BooleanValue showOnTools;
		public IntValue flags;

		
		Object2IntMap<ResourceLocation> priorities = new Object2IntOpenHashMap<>();
		ConfigValue<List<? extends String>> priorityConfigs;

		public void load(ForgeConfigSpec.Builder configs, boolean addonsLoaded)
		{
			configs.push("tooltips");
			configs.comment("Defines how long the progressbar in ticks is for opening the Enchantment View");
			viewCooldowns = configs.defineInRange("View Enchantments Cooldown", 40, 1, 1200);
			configs.comment("Hides Enchantments on Non Enchanted Books until Shift is held");
			//For anyone screaming WHAT THE FUCK IS THIS... Basically UEBase alone shouldn't be so game changing so this is a compromise.
			hideEnchantments = configs.define("hide_enchantments", addonsLoaded);
			configs.comment("Enables if Enchantment Tooltips are sorted by a Priority. This is Client only and might confuse with enchantment removing/extracting mods");
			sortEnchantments = configs.define("sort_enchantment_tooltips", addonsLoaded);
			configs.comment("Allows to sort Enchantment Entries by a desired order. Format: EnchantmentRegistryName;priority (example: minecraft:fortune;25)");
			priorityConfigs = configs.defineList("Enchantment Tooltip Order", ObjectLists.emptyList(), T -> true);
			configs.comment("Defines if curses are hidden until shift is pressed");
			hideCurses = configs.define("Hide Curses", false);
			configs.comment("Defines a small Description shown underneath the Enchantment");
			showDescription = configs.define("Show Description", true);
			configs.comment("Defines if Enchantment Descriptions should be also shown on Tools/Weapons/Armor/etc");
			showOnTools = configs.define("Show Icons on Items", false);
			configs.comment("Allows to control which Keybind Tooltips are displayed, 1 => Description, 2 => Icons, 4 => View, they can be added together if wanted.", "This won't disable functionality just hide the keybinding tooltip itself");
			flags = configs.defineInRange("Visible Tooltips", 7, 0, 7);
			configs.pop();
		}
		
		public void onConfigsChanged()
		{
			priorities.clear();
			priorities.defaultReturnValue(1);
			List<? extends String> list = priorityConfigs.get();
			for(int i = 0; i < list.size(); i++) {
				String[] split = list.get(i).split(";");
				if(split.length != 2) continue;
				ResourceLocation item = ResourceLocation.tryParse(split[0]);
				if(item != null) {
					try { priorities.put(item, Integer.parseInt(split[1])); }
					catch(Exception e) { UEBase.LOGGER.info("Failed To load: "+list.get(i)+", Error: "+e); }
				}
			}
		}
		
		public int getPriority(String s)
		{
			return priorities.getInt(ResourceLocation.tryParse(s));
		}
	}
	
	public static class UEBooks
	{
		public BooleanValue enableItemColoring;
		public BooleanValue enableEnchantmentGlint;
		ConfigValue<List<? extends String>> colorConfigs;
		Object2ObjectMap<ResourceLocation, ColorConfig> colors = new Object2ObjectLinkedOpenHashMap<>();
		
		public void load(ForgeConfigSpec.Builder configs, boolean addonsLoaded)
		{
			configs.comment("Useful tool can be found here https://hugabor.github.io/color-picker/ for help with colors. Name uses RGB hex, Tooltip uses RGBA hex");
			configs.push("Book Textures");
			configs.comment("Toggle for Item Overlay Coloring of Enchanted Books so if the texture is disabled you can turn this optioanlly of too so we don't ruin your texture");
			enableItemColoring = configs.define("Enable Item Coloring", true);
			configs.comment("Allows to override colors of Enchantment Text, Tooltip Border/Background of each individual Enchantment", 
					"Format: EnchantmentRegistryId;TextColor;BackgroundColor;BorderColorTop;BorderColorBottom",
					"Supports RGBA and expects a # or 0x at the beginning of the color string");
			colorConfigs = configs.defineList("enchantmentColors", ColorConfig::createColorConfig, T -> true);
			configs.comment("Allows to control if enchanted books have the glint. This is needed for the new texture looking better");
			enableEnchantmentGlint = configs.define("Enchanted Glint", false);
			configs.pop();
			colors.defaultReturnValue(new ColorConfig());
		}
		
		public void onConfigsChanged()
		{
			colors.clear();
			List<? extends String> list = colorConfigs.get();
			for (int i = 0; i < list.size(); i++) {
				String[] split = list.get(i).split(";");
				if(split.length < 2) continue;
				ColorConfig color = ColorConfig.fromText(split);
				if(color != null) {
					ResourceLocation ench = ResourceLocation.tryParse(split[0]);
					if(ench != null) colors.put(ench, color);
				}
			}
		}
		
	    public ColorConfig getEnchantmentColor(Enchantment ench) {
	    	return colors.get(ench == null ? null : ForgeRegistries.ENCHANTMENTS.getKey(ench));
	    }
	}
	
	public static class UETweaks
	{
		public BooleanValue tableOverride;
		public DoubleValue tableMultiplier;
		public BooleanValue anvilOverride;
		public DoubleValue anvilMultiplier;
		public DoubleValue protectionMultiplier;
		public BooleanValue enableLimits;


		Object2ObjectMap<ResourceLocation, DoubleLevelStats> itemComplexity = new Object2ObjectOpenHashMap<>();
		Object2ObjectMap<ResourceLocation, DoubleLevelStats> enchantmentComplexity = new Object2ObjectOpenHashMap<>();
		ConfigValue<List<? extends String>> itemComplexityConfig;
		ConfigValue<List<? extends String>> enchantmentComplexityConfig;
		DoubleLevelStats defaultItemCompelxity = new DoubleLevelStats("item complexity default", 10, 0.2D, "Defines the Default Value for Item limits if they are not defined");
		public DoubleValue defaultEnchantmentComplexity;
		public IdStat<Item> attribute = new IdStat<>("attribute_activators", ForgeRegistries.ITEMS, Items.BELL);
		
		public void load(ForgeConfigSpec.Builder configs, boolean addonsLoaded)
		{
			configs.push("tweaks");
			configs.push("overrides");
			configs.comment("Enables the feature that Enchantment Tables take Levels worth of XP instead of XPLevels, this affects only consumtion not checks");
			tableOverride = configs.define("Enchanting Table XP override", true);
			configs.comment("Allows modify the conversion rate from Level to XP points. This can result in consuming more then the player actually has");
			tableMultiplier = configs.defineInRange("Enchanting Table XP multiplier", 1D, 0.1D, 1000D);
			configs.comment("Enables the feature that Anvils take Levels worth of XP instead of XPLevels, this affects only consumtion not checks");
			anvilOverride = configs.define("Anvil XP override", true);
			configs.comment("Allows modify the conversion rate from Level to XP points. This can result in consuming more then the player actually has");
			anvilMultiplier = configs.defineInRange("Anvil XP multiplier", 1D, 0.1D, 1000D);
			configs.comment("The multiplier for the Protection Tweaks we did, the higher this value the stronger they get");
			protectionMultiplier = configs.defineInRange("protection_multiplier", 0.003875D, 0D, Double.MAX_VALUE);
			configs.pop();
			configs.push("features");
			defaultItemCompelxity.handleConfig(configs);
			configs.comment("Enables the Enchantment Limit Feature, which allows you to customize how many enchantments and what tiers of enchantments can be put on items.",
					"Disabled by default since this is exteremly altering of the Default Experience");
			enableLimits = configs.define("Enchantment Limits", false);
			configs.comment("Defines the Complexity Limits of items. These Complexity Limits define how many enchantments can be on a Item.",
					"Format: ItemReqistryName;base;perEnchantability",
					"Example: minecraft:carrot;15;0.23",
					"Default: 10+0.2*enchantability");
			itemComplexityConfig = configs.defineList("Item Complexity Limits", ObjectLists.emptyList(), T -> true);
			configs.comment("Default value if a Enchantment isn't listed under enchantment Complexity");
			defaultEnchantmentComplexity = configs.defineInRange("Default Enchantment Complexity", 1D, 0.1D, Double.MAX_VALUE);
			configs.comment("Defines the Complexity of the Enchantment, read \"Item Complexity Limits\" to understand what that is.",
					"Format: EnchantmentRegistryName;base;perLevel",
					"Example: minecraft:protection;1;2.3",
					"Default: 1*level");
			enchantmentComplexityConfig = configs.defineList("Enchantment Complexity", ObjectLists.emptyList(), T -> true);
			attribute.handleConfig(configs);
			configs.pop(2);
		}
		
		public void onConfigChanged()
		{
			attribute.onConfigChanged();
			itemComplexity.clear();
			List<? extends String> list = itemComplexityConfig.get();
			for(int i = 0; i < list.size(); i++) {
				String[] split = list.get(i).split(";");
				if(split.length != 3) continue;
				ResourceLocation item = ResourceLocation.tryParse(split[0]);
				if(item != null) {
					try { itemComplexity.put(item, new DoubleLevelStats("unknown", Double.parseDouble(split[1]), Double.parseDouble(split[2]))); }
					catch(Exception e) { UEBase.LOGGER.info("Failed To load: "+list.get(i)+", Error: "+e); }
				}
			}
			enchantmentComplexity.clear();
			list = enchantmentComplexityConfig.get();
			for(int i = 0; i < list.size(); i++) {
				String[] split = list.get(i).split(";");
				if(split.length != 3) continue;
				ResourceLocation item = ResourceLocation.tryParse(split[0]);
				if(item != null) {
					try { enchantmentComplexity.put(item, new DoubleLevelStats("unknown", Double.parseDouble(split[1]), Double.parseDouble(split[2]))); }
					catch(Exception e) { UEBase.LOGGER.info("Failed To load: "+list.get(i)+", Error: "+e); }
				}
			}
		}
		
		public double getComplexityLimit(ItemStack item)
		{
			DoubleLevelStats stats = itemComplexity.get(ForgeRegistries.ITEMS.getKey(item.getItem()));
			if(stats == null) return 10D+(0.2D*item.getEnchantmentValue());
			return stats.getAsDouble(item.getEnchantmentValue());
		}
		
		public double getComplexity(ResourceLocation id, int level)
		{
			DoubleLevelStats stats = enchantmentComplexity.get(id);
			if(stats == null) return 1D*level;
			return stats.getAsDouble(level);
		}
	}
}