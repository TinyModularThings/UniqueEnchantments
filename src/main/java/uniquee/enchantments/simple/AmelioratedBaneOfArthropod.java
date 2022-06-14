package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntLevelStats;
import uniquee.UE;

public class AmelioratedBaneOfArthropod extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(3D, "bonus_damage");
	public static final IntLevelStats SLOW_DURATION = new IntLevelStats("slow_duration", 40, 15);
	public static final DoubleStat TRANSCENDED_DAMAGE_EXPONENT = new DoubleStat(0.25D, "transcended_damage_exponent");
	
	public AmelioratedBaneOfArthropod()
	{
		super(new DefaultData("ameliorated_bane_of_arthropod", Rarity.RARE, 5, true, false, 6, 4, 30).setTrancendenceLevel(200), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(BONUS_DAMAGE, SLOW_DURATION, TRANSCENDED_DAMAGE_EXPONENT);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.SHARPNESS, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS);
		addIncompats(UE.ADV_SHARPNESS, UE.ADV_SMITE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem;
	}
	
	@Override
	public float getDamageBonus(int level, CreatureAttribute creatureType)
	{
    	return creatureType == CreatureAttribute.ARTHROPOD ? BONUS_DAMAGE.getFloat(level) : 0F;
	}
    
	@Override
	public void doPostAttack(LivingEntity user, Entity target, int level)
	{
        if (target instanceof LivingEntity)
        {
        	LivingEntity entitylivingbase = (LivingEntity)target;
            if (entitylivingbase.getMobType() == CreatureAttribute.ARTHROPOD)
            {
                entitylivingbase.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, SLOW_DURATION.get(level), 3));
            }
        }
	}
}