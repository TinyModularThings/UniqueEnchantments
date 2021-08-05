package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;
import uniquee.utils.IntLevelStats;

public class AmelioratedBaneOfArthropod extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(1D, "bonus_damage");
	public static final IntLevelStats SLOW_DURATION = new IntLevelStats("slow_duration", 40, 15);
	
	public AmelioratedBaneOfArthropod()
	{
		super(new DefaultData("arthropods", Rarity.RARE, 5, true, 6, 4, 30), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		BONUS_DAMAGE.handleConfig(config, getConfigName());
		SLOW_DURATION.handleConfig(config, getConfigName());
	}
	
    @Override
	public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType)
    {
    	return creatureType == EnumCreatureAttribute.ARTHROPOD ? BONUS_DAMAGE.getFloat(level) : 0F;
    }
    
    @Override
	public void onEntityDamaged(EntityLivingBase user, Entity target, int level)
    {
        if (target instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)target;
            if (entitylivingbase.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD)
            {
                entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, SLOW_DURATION.get(level), 3));
            }
        }
    }
}
