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

public class EnchantmentAdvancedDamage extends EnchantmentDamage implements IToggleEnchantment
{
    private static final String[] DAMAGE_NAMES = new String[] {"all", "undead", "arthropods"};
    private static final int[] BASECOST = new int[]{18, 14, 14};
    private static final float[] DEFAULTS = new float[]{1F, 3F, 3F};
    public float scalar;
    boolean isEnabled = true;
    
	public EnchantmentAdvancedDamage(int damageTypeIn)
	{
		super(damageTypeIn == 0 ? Rarity.VERY_RARE : Rarity.RARE, damageTypeIn, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
		setRegistryName(DAMAGE_NAMES[damageTypeIn]);
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
    	return "enchantment.uniquee.damage."+DAMAGE_NAMES[damageType];
    }
    
    @Override
    public int getMaxLevel()
    {
    	return 5;
    }
    
    @Override
    public boolean isTreasureEnchantment()
    {
    	return true;
    }
    
    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
    	return BASECOST[damageType] + (2 * enchantmentLevel);
    }
    
    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
    	return getMinEnchantability(enchantmentLevel) + BASECOST[damageType];
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
			case 0: return "adv_sharpness";
			case 1: return "adv_smite";
			case 2: return "adv_arthropods";
			default: return "I_AM_ERROR";
		}
	}

	@Override
	public void loadFromConfig(Configuration config)
	{
		isEnabled = config.get(getConfigName(), "enabled", true).getBoolean();
		scalar = (float)config.get(getConfigName(), "scalar", DEFAULTS[damageType]).getDouble();
	}
}
