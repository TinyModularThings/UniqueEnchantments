package uniquee.enchantments.complex;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class Momentum extends UniqueEnchantment
{
	public static final DoubleStat BASE_FACTOR = new DoubleStat(0.7D, "base_factor");
	public static final DoubleStat EFFECT_FACTOR = new DoubleStat(0.3, "effect_factor");
	
	public Momentum() 
	{
		super(new DefaultData("momentum", Rarity.RARE, 3, true, true, 10, 5, 75), EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND);
		addStats(BASE_FACTOR, EFFECT_FACTOR);
	}
	
	public static double calculateBoost(Entity entity, int level) {
		return BASE_FACTOR.get()+ EFFECT_FACTOR.get() * Math.sqrt(0.1D*entity.getDeltaMovement().horizontalDistance()*level);
	}
}
