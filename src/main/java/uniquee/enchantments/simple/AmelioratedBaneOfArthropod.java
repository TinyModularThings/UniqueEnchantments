package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntLevelStats;
import uniquee.UniqueEnchantments;

public class AmelioratedBaneOfArthropod extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(3D, "bonus_damage");
	public static final IntLevelStats SLOW_DURATION = new IntLevelStats("slow_duration_lvl", 40, 15);
	
	public AmelioratedBaneOfArthropod()
	{
		super(new DefaultData("arthropods", Rarity.RARE, 5, true, 6, 4, 30), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND);
		addStats(BONUS_DAMAGE, SLOW_DURATION);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.SHARPNESS, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS);
		addIncompats(UniqueEnchantments.ADV_SHARPNESS, UniqueEnchantments.ADV_SMITE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe;
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
