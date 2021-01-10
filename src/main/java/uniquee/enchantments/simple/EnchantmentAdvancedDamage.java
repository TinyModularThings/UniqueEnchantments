package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.IToggleEnchantment;
import uniquee.enchantments.UniqueEnchantment.DefaultData;

public class EnchantmentAdvancedDamage extends EnchantmentDamage implements IToggleEnchantment
{
	public static final DefaultData[] DATA = new DefaultData[]{
		new DefaultData("all", Rarity.VERY_RARE, 5, true, 20, 2, 18),
		new DefaultData("undead", Rarity.RARE, 5, true, 16, 2, 14),
		new DefaultData("arthropods", Rarity.RARE, 5, true, 16, 2, 14),
	};
    private static final float[] DEFAULTS = new float[]{1F, 3F, 3F};
	DefaultData defaults;
	DefaultData actualData;
    public float scalar;
    boolean isEnabled = true;
    
	public EnchantmentAdvancedDamage(int damageTypeIn)
	{
		super(DATA[damageTypeIn].getRarity(), damageTypeIn, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
		setRegistryName(DATA[damageTypeIn].getName());
		defaults = DATA[damageTypeIn];
		actualData = defaults;
		scalar = DEFAULTS[damageTypeIn];
	}
	
    @Override
	public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType)
    {
    	switch(damageType)
    	{
    		case 0: return scalar * level;
    		case 1: return creatureType == EnumCreatureAttribute.UNDEAD ? scalar * level : 0F;
    		case 2: return creatureType == EnumCreatureAttribute.ARTHROPOD ? scalar * level : 0F;
    		default: return 0F;
    	}
    }
    
    @Override
    public String getName()
    {
    	return "enchantment.uniquee."+defaults.getName();
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
	public void onEntityDamaged(EntityLivingBase user, Entity target, int level)
    {
        if (target instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)target;

            if (this.damageType == 2 && entitylivingbase.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD)
            {
                int i = 40 + user.getRNG().nextInt(15 * level);
                entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, i, 3));
            }
        }
    }
    
	@Override
	public String getConfigName()
	{
		switch(damageType)
		{
			case 0: return "base.ameliorated_sharpness";
			case 1: return "base.ameliorated_smite";
			case 2: return "base.ameliorated_arthropods";
			default: return "I_AM_ERROR";
		}
	}
	
	@Override
	public void loadFromConfig(Configuration config)
	{
		isEnabled = config.get(getConfigName(), "enabled", true).getBoolean();
		actualData = new DefaultData(defaults, config, getConfigName());
		scalar = (float)config.get(getConfigName(), "scalar", DEFAULTS[damageType]).getDouble();
		config.getCategory(getConfigName()).setLanguageKey(getName());
	}
}
