package uniqueapex.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniqueapex.enchantments.ApexEnchantment;
import uniquebase.utils.DoubleStat;

public class AbsoluteProtection extends ApexEnchantment
{
	public static final DoubleStat SCALE = new DoubleStat(0.004D, "scale");
	public static final DoubleStat PROT_MULT = new DoubleStat(0.01D, "protection_multiplier");
	
	public AbsoluteProtection()
	{
		super("absolute_protection", EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
		setCategory("apex");
		addStats(SCALE, PROT_MULT);
	}
}
