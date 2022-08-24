package uniquee.enchantments.simple;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
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
		super(new DefaultData("ameliorated_bane_of_arthropods", Rarity.RARE, 5, true, false, 6, 4, 30).setTrancendenceLevel(200), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);
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
	public float getDamageBonus(int level, MobType creatureType)
	{
    	return creatureType == MobType.ARTHROPOD ? BONUS_DAMAGE.getFloat(level) : 0F;
	}
    
	@Override
	public void doPostAttack(LivingEntity user, Entity target, int level)
	{
        if (target instanceof LivingEntity)
        {
        	LivingEntity entitylivingbase = (LivingEntity)target;
            if (entitylivingbase.getMobType() == MobType.ARTHROPOD)
            {
                entitylivingbase.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_DURATION.get(level), 3));
            }
        }
	}
}