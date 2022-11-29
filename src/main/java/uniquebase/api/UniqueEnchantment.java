package uniquebase.api;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import uniquebase.utils.IStat;
import uniquebase.utils.IdStat;

public abstract class UniqueEnchantment extends Enchantment implements IToggleEnchantment
{
	public static final Rarity[] RARITIES = Rarity.values();
	DefaultData values;
	protected BooleanValue enabled;
	protected BooleanValue activated;
	boolean isCurse = false;
	boolean disableDefaultItems = false;
	List<IStat> stats = new ObjectArrayList<>();
	String configName;
	String categoryName = "base";
	ResourceLocation id;
	
	protected UniqueEnchantment(DefaultData data, EnchantmentCategory typeIn, EquipmentSlot... slots)
	{
		super(data.getRarity(), typeIn, slots);
		id = GameData.checkPrefix(data.getName(), false);
		configName = data.getName();
		values = data;
	}
	
	public UniqueEnchantment addStats(IStat...stat)
	{
		stats.addAll(ObjectArrayList.wrap(stat));
		return this;
	}
	
	public UniqueEnchantment setCurse()
	{
		isCurse = true;
		return this;
	}
	
	public UniqueEnchantment setDisableDefaultItems()
	{
		disableDefaultItems = true;
		return this;
	}
	
	@Override
	public ResourceLocation getId()
	{
		return id;
	}
	
	@Override
	public boolean isCurse()
	{
		return isCurse;
	}
	
	@Override
	public int getMaxLevel()
	{
		return values.getMaxLevel();
	}
	
	@Override
	public int getMinLevel()
	{
		return values.getMinLevel();
	}
	
	@Override
	public boolean isTreasureOnly()
	{
		return values.isTreasure();
	}
	
	@Override
	public boolean isTradeable()
	{
		return values.isTradeable();
	}
	
	@Override
	public int getMinCost(int enchantmentLevel)
	{
		return values.getLevelCost(enchantmentLevel);
	}
	
	@Override
	public int getMaxCost(int enchantmentLevel)
	{
		return getMinCost(enchantmentLevel) + values.getRangeCost();
	}
	
	@Override
	public Rarity getRarity()
	{
		return values.getRarity();
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled.get() ? ((!disableDefaultItems && super.canApplyAtEnchantingTable(stack)) || canApplyToItem(stack) || values.isCompatible(stack)) && !(canNotApplyToItems(stack) || values.isIncompatible(stack)) : false;
	}
	
	@Override
	public boolean isAllowedOnBooks()
	{
		return activated.get();
	}
	
	@Override
	public int getHardCap()
	{
		return values.getHardCap();
	}
	
	@Override
	public int getTranscendedLevel()
	{
		return values.getTranscendedLevel();
	}
	
	@Override
	protected boolean checkCompatibility(Enchantment ench)
	{
		return super.checkCompatibility(ench) && !values.incompats.contains(ForgeRegistries.ENCHANTMENTS.getKey(ench));
	}
	
	@Override
	public boolean isEnabled()
	{
		return enabled.get();
	}
	
	protected void addIncompats(Enchantment... enchantments)
	{
		values.addIncompats(enchantments);
	}
	
	protected void addIncompats(ResourceLocation... enchantments)
	{
		values.addIncompats(enchantments);
	}
	
	protected boolean canApplyToItem(ItemStack stack)
	{
		return false;
	}
	
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return false;
	}

	@Override
	public String getConfigName()
	{
		return categoryName+"."+configName;
	}
	
	protected void setCategory(String name)
	{
		this.categoryName = name;
	}
	
	@Override
	public final void loadFromConfig(ForgeConfigSpec.Builder config)
	{
		int split = getConfigName().split(".").length+2;
		config.push(getConfigName());
		config.push("General Settings");
		config.comment("If the Enchantment is Obtainable");
		enabled = config.define("enabled", true);
		config.comment("If the Enchantment has any effect");
		activated = config.define("activated", true);
		values.loadConfig(config);
		config.pop();
		for(int i = 0,m=stats.size();i<m;i++)
		{
			stats.get(i).handleConfig(config);
		}
		loadData(config);
		config.pop(split);
	}
	
	@Override
	public void onConfigChanged()
	{
		values.onConfigChanged();
	}
	
	public void loadData(ForgeConfigSpec.Builder config) {}
	
	public static class DefaultData
	{
		int minLevel;
		int maxLevel;
		String name;
		Rarity rare;
		boolean isTreasure;
		boolean isTradeable;
		int baseCost;
		int levelCost;
		int rangeCost;
		int hardCap = 100;
		Integer trancendence = null;
		
		EnumValue<Rarity> rare_Config;
		BooleanValue isTreasure_Config;
		BooleanValue isTradeable_Config;
		IntValue baseCost_Config;
		IntValue levelCost_Config;
		IntValue rangeCost_Config;
		IntValue minLevel_Config;
		IntValue maxLevel_Config;
		IntValue hardCap_Config;
		IntValue trancendence_Config;
		IdStat<Enchantment> incompats = new IdStat<>("incompats", ForgeRegistries.ENCHANTMENTS);
		IdStat<Item> incompatibleItems = new IdStat<>("incompatible_items", "Allows to add custom incompatible Items", ForgeRegistries.ITEMS);
		IdStat<Item> compatibleItems = new IdStat<>("compatible_items", "Allows to add custom compatible Items", ForgeRegistries.ITEMS);
		
		public DefaultData(String name, Rarity rare, int maxLevel, boolean isTreasure, boolean isTradeable, int baseCost, int levelCost, int rangeCost)
		{
			this(name, rare, 1, maxLevel, isTreasure, isTradeable, baseCost, levelCost, rangeCost);
		}
		
		public DefaultData(String name, Rarity rare, int minLevel, int maxLevel, boolean isTreasure, boolean isTradeable, int baseCost, int levelCost, int rangeCost)
		{
			this.name = name;
			this.rare = rare;
			this.minLevel = minLevel;
			this.maxLevel = maxLevel;
			this.isTreasure = isTreasure;
			this.isTradeable = isTradeable;
			this.baseCost = baseCost;
			this.levelCost = levelCost;
			this.rangeCost = rangeCost;
		}
		
		public void loadConfig(ForgeConfigSpec.Builder config)
		{
			config.comment("Minimum Enchantment Level");
			minLevel_Config = config.defineInRange("min_level", minLevel, 0, Integer.MAX_VALUE);
			config.comment("Maximum Enchantment Level");
			maxLevel_Config = config.defineInRange("max_level", maxLevel, 0, Integer.MAX_VALUE);
			config.comment("Rarity of the Enchantment");
			rare_Config = config.defineEnum("rarity", rare);
			config.comment("If the Enchantment is a Treasure");
			isTreasure_Config = config.define("treasure", isTreasure);
			config.comment("If the Enchantment is Tradeable by Villagers");
			isTradeable_Config = config.define("tradeable", isTradeable);
			config.comment("Minimum Level for Enchanting");
			baseCost_Config = config.defineInRange("base_cost", baseCost, 0, Integer.MAX_VALUE);
			config.comment("Increase of levels per Enchantment Level");
			levelCost_Config = config.defineInRange("per_level_cost", levelCost, 0, Integer.MAX_VALUE);
			config.comment("The Additional Upper Range of Required Levels");
			rangeCost_Config = config.defineInRange("cost_limit", rangeCost, 0, Integer.MAX_VALUE);
			config.comment("Hard Limit of where the Enchantment will be capped even if the level is higher");
			hardCap_Config = config.defineInRange("hard_cap", hardCap, 0, Integer.MAX_VALUE);
			if(trancendence != null)
			{
				config.comment("Defines the Required XP level to trigger the Trancendence Effect");
				trancendence_Config = config.defineInRange("trancendence", trancendence, 1, Integer.MAX_VALUE);
			}
			config.comment("Enchantments that are not compatible with this Enchantment");
			incompats.handleConfig(config);
			incompatibleItems.handleConfig(config);
			compatibleItems.handleConfig(config);
		}
		
		public void addIncompats(Enchantment...enchantments)
		{
			incompats.addDefault(enchantments);
		}
		
		public void addIncompats(ResourceLocation...enchantments)
		{
			incompats.addDefault(enchantments);
		}
		
		public DefaultData setHardCap(int newCap)
		{
			hardCap = newCap;
			return this;
		}
		
		public DefaultData setTrancendenceLevel(int newLevel)
		{
			trancendence = newLevel;
			return this;
		}
		
		public void onConfigChanged()
		{
			incompats.onConfigChanged();
			incompatibleItems.onConfigChanged();
			compatibleItems.onConfigChanged();
		}
		
		public int getLevelCost(int minLevel)
		{
			int level = getLevelCost();
			return (getBaseCost() - level) + (minLevel * level);
		}
		
		public boolean isCompatible(ItemStack stack)
		{
			return compatibleItems.contains(stack.getItem());
		}
		
		public boolean isIncompatible(ItemStack stack)
		{
			return incompatibleItems.contains(stack.getItem());
		}
		
		public String getName()
		{
			return name;
		}
		
		public int getTranscendedLevel()
		{
			return trancendence == null || trancendence_Config == null ? 1000 : trancendence_Config.get();
		}
		
		public int getMinLevel()
		{
			return minLevel_Config != null ? minLevel_Config.get() : minLevel;
		}
		
		public int getMaxLevel()
		{
			return maxLevel_Config != null ? maxLevel_Config.get() : maxLevel;
		}

		public Rarity getRarity()
		{
			return rare_Config != null ? rare_Config.get() : rare;
		}

		public boolean isTreasure()
		{
			return isTreasure_Config != null ? isTreasure_Config.get() : isTreasure;
		}
		
		public boolean isTradeable()
		{
			return isTradeable_Config != null ? isTradeable_Config.get() : isTradeable;
		}

		public int getBaseCost()
		{
			return baseCost_Config != null ? baseCost_Config.get() : baseCost;
		}

		public int getLevelCost()
		{
			return levelCost_Config != null ? levelCost_Config.get() : levelCost;
		}

		public int getRangeCost()
		{
			return rangeCost_Config != null ? rangeCost_Config.get() : rangeCost;
		}
		
		public int getHardCap()
		{
			return hardCap_Config != null ? hardCap_Config.get() : hardCap;
		}
	}
}
