package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
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

public class AmelioratedBaneOfArthropod extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(3D, "bonus_damage");
	public static final IntLevelStats SLOW_DURATION = new IntLevelStats("slow_duration", 40, 15);
	
	public AmelioratedBaneOfArthropod()
	{
		super(new DefaultData("arthropods", Rarity.RARE, 5, true, 6, 4, 30), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(BONUS_DAMAGE, SLOW_DURATION);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem;
	}
	
	@Override
	public float calcDamageByCreature(int level, CreatureAttribute creatureType)
	{
    	return creatureType == CreatureAttribute.ARTHROPOD ? BONUS_DAMAGE.getFloat(level) : 0F;
	}
    
	@Override
	public void onEntityDamaged(LivingEntity user, Entity target, int level)
	{
        if (target instanceof LivingEntity)
        {
        	LivingEntity entitylivingbase = (LivingEntity)target;
            if (entitylivingbase.getCreatureAttribute() == CreatureAttribute.ARTHROPOD)
            {
                entitylivingbase.addPotionEffect(new EffectInstance(Effects.SLOWNESS, SLOW_DURATION.get(level), 3));
            }
        }
	}
}