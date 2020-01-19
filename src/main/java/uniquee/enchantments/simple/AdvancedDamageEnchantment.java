package uniquee.enchantments.simple;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.IToggleEnchantment;
import uniquee.enchantments.UniqueEnchantment.DefaultData;
import uniquee.utils.DoubleStat;

public class AdvancedDamageEnchantment extends DamageEnchantment implements IToggleEnchantment
{
	public static final DefaultData[] DATA = new DefaultData[]{
		new DefaultData("all", Rarity.VERY_RARE, true, 20, 2, 18),
		new DefaultData("undead", Rarity.RARE, true, 16, 2, 14),
		new DefaultData("arthropods", Rarity.RARE, true, 16, 2, 14),
	};
    private static final DoubleStat[] DEFAULTS = new DoubleStat[]{new DoubleStat(1D, "scalar"), new DoubleStat(3D, "scalar"), new DoubleStat(3D, "scalar")};
	DefaultData values;
    public DoubleStat scalar;
    BooleanValue isEnabled;
    
	public AdvancedDamageEnchantment(int damageTypeIn)
	{
		super(DATA[damageTypeIn].getRarity(), damageTypeIn, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
		setRegistryName(DATA[damageTypeIn].getName());
		values = DATA[damageTypeIn];
		scalar = DEFAULTS[damageTypeIn];
	}
	
    @Override
	public float calcDamageByCreature(int level, CreatureAttribute creatureType)
    {
    	switch(damageType)
    	{
    		case 0: return (float)(scalar.get() * level);
    		case 1: return creatureType == CreatureAttribute.UNDEAD ? (float)(scalar.get() * level) : 0F;
    		case 2: return creatureType == CreatureAttribute.ARTHROPOD ? (float)(scalar.get() * level) : 0F;
    		default: return 0F;
    	}
    }
    
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack)
    {
    	return isEnabled.get() && super.canApplyAtEnchantingTable(stack);
    }
    
    @Override
    public boolean isAllowedOnBooks()
    {
    	return isEnabled.get();
    }
    
    @Override
    public String getName()
    {
    	return "enchantment.uniquee."+values.getName();
    }
    
    @Override
    public int getMaxLevel()
    {
    	return 5;
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
    public void onEntityDamaged(LivingEntity user, Entity target, int level)
    {
        if (target instanceof LivingEntity)
        {
        	LivingEntity entitylivingbase = (LivingEntity)target;

            if (damageType == 2 && entitylivingbase.getCreatureAttribute() == CreatureAttribute.ARTHROPOD)
            {
                int i = 40 + user.getRNG().nextInt(15 * level);
                entitylivingbase.addPotionEffect(new EffectInstance(Effects.SLOWNESS, i, 3));
            }
        }
    }
    
	@Override
	public String getConfigName()
	{
		switch(damageType)
		{
			case 0: return "ameliorated_sharpness";
			case 1: return "ameliorated_smite";
			case 2: return "ameliorated_arthropods";
			default: return "I_AM_ERROR";
		}
	}
	
	@Override
	public void loadFromConfig(Builder entry)
	{
		entry.push(getConfigName());
		isEnabled = entry.define("enabled", true);
		values.loadConfig(entry);
		scalar.handleConfig(entry);
		entry.pop();
	}
}
