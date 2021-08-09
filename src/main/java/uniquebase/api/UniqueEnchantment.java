package uniquebase.api;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import uniquebase.utils.IStat;

public abstract class UniqueEnchantment extends Enchantment implements IToggleEnchantment
{
	public static final Rarity[] RARITIES = Rarity.values();
	DefaultData defaults;
	DefaultData actualData;
	List<IStat> stats = new ObjectArrayList<>();
	protected boolean enabled = false;
	protected boolean activated = false;
	protected boolean isCurse = false;
	String configName;
	String categoryName = "base";
	
	protected UniqueEnchantment(DefaultData data, EnumEnchantmentType typeIn, EntityEquipmentSlot... slots)
	{
		super(data.getRarity(), typeIn, slots);
		setName(Loader.instance().activeModContainer().getModId()+"."+data.getName());
		setRegistryName(data.getName());
		this.configName = data.getName();
		defaults = data;
		actualData = data;
	}
	
	public void addStats(IStat...stats)
	{
		this.stats.addAll(ObjectArrayList.wrap(stats));
	}
	
	public void setCurse()
	{
		isCurse = true;
	}
	
	@Override
	public boolean isCurse()
	{
		return isCurse;
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
		return activated;
	}
	
	@Override
	public int getHardCap()
	{
		return actualData.getHardCap();
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
	
	protected void addIncompats(Enchantment...enchantments)
	{
		defaults.addIncompats(enchantments);
	}
	
	public void addIncompats(ResourceLocation...locations)
	{
		defaults.addIncompats(locations);
	}
	
	@Override
	public void loadIncompats()
	{
		
	}
	
	@Override
	public final void loadFromConfig(Configuration config)
	{
		enabled = config.get(getConfigName(), "enabled", true, "If the Enchantment is Obtainable").getBoolean();
		activated = config.get(getConfigName(), "activated", true, "If the Enchantment has any effect").getBoolean();
		actualData = new DefaultData(defaults, config, getConfigName());
		for(int i = 0,m=stats.size();i<m;i++)
		{
			stats.get(i).handleConfig(config, getConfigName());
		}
		loadData(config);
		config.getCategory(getConfigName()).setLanguageKey(getName());
	}
	
	public void loadData(Configuration config) {}
	
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
		int hardCap;
		Set<ResourceLocation> incompats = new ObjectOpenHashSet<>();
		
		public DefaultData(DefaultData defaultValues, Configuration config, String configName)
		{
			name = defaultValues.getName();
			minLevel = config.get(configName, "min_level", defaultValues.getMinLevel(), "Minimum Enchantment Level").getInt();
			maxLevel = config.get(configName, "max_level", defaultValues.getMaxLevel(), "Maximum Enchantment Level").getInt();
			rare = RARITIES[config.get(configName, "rarity", defaultValues.getRarity().ordinal(), "Rarity of the Enchantment").getInt()];
			isTreasure = config.get(configName, "treasure", defaultValues.isTreasure(), "If the Enchantment is a Treasure").getBoolean();
			baseCost = config.get(configName, "base_cost", defaultValues.getBaseCost(), "Minimum Level for Enchanting").getInt();
			levelCost = config.get(configName, "per_level_cost", defaultValues.getLevelCost(), "Increase of levels per Enchantment Level").getInt();
			rangeCost = config.get(configName, "cost_limit", defaultValues.getRangeCost(), "The Additional Upper Range of Required Levels").getInt();
			hardCap = config.get(configName, "hard_cap", defaultValues.getHardCap(), "Hard Limit of where the Enchantment will be capped even if the level is higher").getInt();
			String[] result = config.get(configName, "incompats", defaultValues.getInCompats(), "Enchantments that are not compatible with this Enchantment").getStringList();
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
			this.hardCap = 100;
		}
		
		public DefaultData setHardCap(int max)
		{
			this.hardCap = max;
			return this;
		}
		
		public void addIncompats(Enchantment...enchantments)
		{
			for(int i = 0,m=enchantments.length;i<m;incompats.add(enchantments[i++].getRegistryName()));
		}
		
		public void addIncompats(ResourceLocation...locations)
		{
			incompats.addAll(ObjectArrayList.wrap(locations));
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
		
		public int getHardCap()
		{
			return hardCap;
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
