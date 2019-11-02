package uniquee.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public abstract class UniqueEnchantment extends Enchantment implements IToggleEnchantment
{
	public static final Rarity[] RARITIES = Rarity.values();
	DefaultData defaults;
	DefaultData actualData;
	boolean enabled = false;
	String configName;
	
	protected UniqueEnchantment(DefaultData data, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots)
	{
		super(data.getRarity(), typeIn, slots);
		setName("uniquee."+data.getName());
		setRegistryName(data.getName());
		this.configName = data.getName();
		defaults = data;
		actualData = data;
	}
	
	@Override
	public boolean isTreasureEnchantment()
	{
		return actualData.isTreasure();
	}
	
	@Override
	public int getMinEnchantability(int enchantmentLevel)
	{
		return actualData.getLevelCost(enchantmentLevel);
	}
	
	@Override
	public int getMaxEnchantability(int enchantmentLevel)
	{
		return getMinEnchantability(enchantmentLevel) + actualData.getRangeCost();
	}
	
	@Override
	public Rarity getRarity()
	{
		return actualData.getRarity();
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled ? super.canApplyAtEnchantingTable(stack) : false;
	}

	@Override
	public String getConfigName()
	{
		return configName;
	}
	
	@Override
	public final void loadFromConfig(Configuration config)
	{
		enabled = config.get(getConfigName(), "enabled", true).getBoolean();
		actualData = new DefaultData(defaults, config, getConfigName());
		loadData(config);
	}
	
	public abstract void loadData(Configuration config);
	
	public static class DefaultData
	{
		String name;
		Rarity rare;
		boolean isTreasure;
		int baseCost;
		int levelCost;
		int rangeCost;
		
		public DefaultData(DefaultData defaultValues, Configuration config, String configName)
		{
			name = defaultValues.getName();
			rare = RARITIES[config.get(configName, "rarity", defaultValues.getRarity().ordinal()).getInt()];
			isTreasure = config.get(configName, "treasure", defaultValues.isTreasure()).getBoolean();
			baseCost = config.get(configName, "base_cost", defaultValues.getBaseCost()).getInt();
			levelCost = config.get(configName, "per_level_cost", defaultValues.getLevelCost()).getInt();
			rangeCost = config.get(configName, "cost_limit", defaultValues.getRangeCost()).getInt();
		}
		
		public DefaultData(String name, Rarity rare, boolean isTreasure, int baseCost, int levelCost, int rangeCost)
		{
			this.name = name;
			this.rare = rare;
			this.isTreasure = isTreasure;
			this.baseCost = baseCost;
			this.levelCost = levelCost;
			this.rangeCost = rangeCost;
		}
		
		public int getLevelCost(int minLevel)
		{
			return (baseCost - levelCost) + (minLevel * levelCost);
		}
		
		public String getName()
		{
			return name;
		}

		public Rarity getRarity()
		{
			return rare;
		}

		public boolean isTreasure()
		{
			return isTreasure;
		}

		public int getBaseCost()
		{
			return baseCost;
		}

		public int getLevelCost()
		{
			return levelCost;
		}

		public int getRangeCost()
		{
			return rangeCost;
		}
	}
}
