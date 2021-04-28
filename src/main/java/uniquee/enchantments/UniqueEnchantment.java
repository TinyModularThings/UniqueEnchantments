package uniquee.enchantments;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;

public abstract class UniqueEnchantment extends Enchantment implements IToggleEnchantment
{
	public static final Rarity[] RARITIES = Rarity.values();
	DefaultData defaults;
	DefaultData actualData;
	protected boolean enabled = false;
	String configName;
	String categoryName = "base";
	
	protected UniqueEnchantment(DefaultData data, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots)
	{
		super(data.getRarity(), typeIn, slots);
		setName(Loader.instance().activeModContainer().getModId()+"."+data.getName());
		setRegistryName(data.getName());
		this.configName = data.getName();
		defaults = data;
		actualData = data;
	}
	
	@Override
	public int getMinLevel()
	{
		return actualData.getMinLevel();
	}
	
	@Override
	public int getMaxLevel()
	{
		return actualData.getMaxLevel();
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
	protected boolean canApplyTogether(Enchantment ench)
	{
		return super.canApplyTogether(ench) && !actualData.incompats.contains(ench.getRegistryName());
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled ? (super.canApplyAtEnchantingTable(stack) || canApplyToItem(stack)) && !canNotApplyToItems(stack) : false;
	}
	
	@Override
	public boolean isAllowedOnBooks()
	{
		return enabled;
	}
	
	@Override
	public boolean isEnabled()
	{
		return enabled;
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
	
	protected void addIncomats(Enchantment...enchantments)
	{
		defaults.addIncompats(enchantments);
	}
	
	@Override
	public void loadIncompats()
	{
		
	}
	
	@Override
	public final void loadFromConfig(Configuration config)
	{
		enabled = config.get(getConfigName(), "enabled", true).getBoolean();
		actualData = new DefaultData(defaults, config, getConfigName());
		loadData(config);
		config.getCategory(getConfigName()).setLanguageKey(getName());
	}
	
	public abstract void loadData(Configuration config);
	
	public static class DefaultData
	{
		String name;
		Rarity rare;
		int minLevel;
		int maxLevel;
		boolean isTreasure;
		int baseCost;
		int levelCost;
		int rangeCost;
		Set<ResourceLocation> incompats = new ObjectOpenHashSet<>();
		
		public DefaultData(DefaultData defaultValues, Configuration config, String configName)
		{
			name = defaultValues.getName();
			minLevel = config.get(configName, "min_level", defaultValues.getMinLevel()).getInt();
			maxLevel = config.get(configName, "max_level", defaultValues.getMaxLevel()).getInt();
			rare = RARITIES[config.get(configName, "rarity", defaultValues.getRarity().ordinal()).getInt()];
			isTreasure = config.get(configName, "treasure", defaultValues.isTreasure()).getBoolean();
			baseCost = config.get(configName, "base_cost", defaultValues.getBaseCost()).getInt();
			levelCost = config.get(configName, "per_level_cost", defaultValues.getLevelCost()).getInt();
			rangeCost = config.get(configName, "cost_limit", defaultValues.getRangeCost()).getInt();
			String[] result = config.get(configName, "incompats", defaultValues.getInCompats()).getStringList();
			for(int i = 0,m=result.length;i<m;i++)
			{
				try
				{
					incompats.add(new ResourceLocation(result[i]));
				}
				catch(Exception e)
				{
					FMLLog.log.error("Adding Incompat ["+result[i]+"] has caused a crash", e);
				}
			}
		}
		
		public DefaultData(String name, Rarity rare, int maxLevel, boolean isTreasure, int baseCost, int levelCost, int rangeCost)
		{
			this(name, rare, 1, maxLevel, isTreasure, baseCost, levelCost, rangeCost);
		}
		
		public DefaultData(String name, Rarity rare, int minLevel, int maxLevel, boolean isTreasure, int baseCost, int levelCost, int rangeCost)
		{
			this.name = name;
			this.rare = rare;
			this.minLevel = minLevel;
			this.maxLevel = maxLevel;
			this.isTreasure = isTreasure;
			this.baseCost = baseCost;
			this.levelCost = levelCost;
			this.rangeCost = rangeCost;
		}
		
		public void addIncompats(Enchantment...enchantments)
		{
			for(int i = 0,m=enchantments.length;i<m;incompats.add(enchantments[i++].getRegistryName()));
		}
		
		private String[] getInCompats() 
		{
			String[] incompatString = new String[incompats.size()];
			int index = 0;
			for(ResourceLocation loc : incompats) incompatString[index++] = loc.toString();
			return incompatString;
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
		
		public int getMinLevel()
		{
			return minLevel;
		}
		
		public int getMaxLevel()
		{
			return maxLevel;
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
