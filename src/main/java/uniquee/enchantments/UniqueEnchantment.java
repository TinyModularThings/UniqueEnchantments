package uniquee.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public abstract class UniqueEnchantment extends Enchantment implements IToggleEnchantment
{
	public static final Rarity[] RARITIES = Rarity.values();
	DefaultData values;
	protected BooleanValue enabled;
	String configName;
	String categoryName = "base";

	protected UniqueEnchantment(DefaultData data, EnchantmentType typeIn, EquipmentSlotType[] slots)
	{
		super(data.getRarity(), typeIn, slots);
		setRegistryName("uniquee", data.getName());
		configName = data.getName();
		values = data;
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
	public boolean isTreasureEnchantment()
	{
		return values.isTreasure();
	}
	
	@Override
	public int getMinEnchantability(int enchantmentLevel)
	{
		return values.getLevelCost(enchantmentLevel);
	}
	
	@Override
	public int getMaxEnchantability(int enchantmentLevel)
	{
		return getMinEnchantability(enchantmentLevel) + values.getRangeCost();
	}
	
	@Override
	public Rarity getRarity()
	{
		return values.getRarity();
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled.get() ? (super.canApplyAtEnchantingTable(stack) || canApplyToItem(stack)) && !canNotApplyToItems(stack) : false;
	}
	
	@Override
	public boolean isAllowedOnBooks()
	{
		return enabled.get();
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
		int count = getConfigName().split(".").length + 2;
		config.push(getConfigName());
		config.translation(getName());
		enabled = config.define("enabled", true);
		values.loadConfig(config);
		loadData(config);
		config.pop(count);
	}
	
	public abstract void loadData(ForgeConfigSpec.Builder config);
	
	public static class DefaultData
	{
		int minLevel;
		int maxLevel;
		String name;
		Rarity rare;
		boolean isTreasure;
		int baseCost;
		int levelCost;
		int rangeCost;
		
		EnumValue<Rarity> rare_Config;
		BooleanValue isTreasure_Config;
		IntValue baseCost_Config;
		IntValue levelCost_Config;
		IntValue rangeCost_Config;
		IntValue minLevel_Config;
		IntValue maxLevel_Config;
		
		public DefaultData(String name, Rarity rare, int maxLevel, boolean isTreasure, int baseCost, int levelCost, int rangeCost)
		{
			this(name, rare, 1, maxLevel, isTreasure, baseCost, levelCost, rangeCost);
		}
		
		public DefaultData(String name, Rarity rare, int minLevel, int maxLevel, boolean isTreasure, int baseCost, int levelCost, int rangeCost)
		{
			this.name = name;
			this.rare = rare;
			this.isTreasure = isTreasure;
			this.baseCost = baseCost;
			this.levelCost = levelCost;
			this.rangeCost = rangeCost;
		}
		
		public void loadConfig(ForgeConfigSpec.Builder config)
		{
			minLevel_Config = config.defineInRange("min_level", minLevel, 0, Integer.MAX_VALUE);
			maxLevel_Config = config.defineInRange("max_level", maxLevel, 0, Integer.MAX_VALUE);
			rare_Config = config.defineEnum("rarity", rare);
			isTreasure_Config = config.define("treasure", isTreasure);
			baseCost_Config = config.defineInRange("base_cost", baseCost, 0, Integer.MAX_VALUE);
			levelCost_Config = config.defineInRange("per_level_cost", levelCost, 0, Integer.MAX_VALUE);
			rangeCost_Config = config.defineInRange("cost_limit", rangeCost, 0, Integer.MAX_VALUE);
		}
		
		public int getLevelCost(int minLevel)
		{
			int level = getLevelCost();
			return (getBaseCost() - level) + (minLevel * level);
		}
		
		public String getName()
		{
			return name;
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
	}
}
