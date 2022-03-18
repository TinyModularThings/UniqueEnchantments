package uniqueapex.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import uniqueapex.enchantments.ApexEnchantment;
import uniquebase.utils.DoubleStat;

public class AbsoluteProtection extends ApexEnchantment
{
	public static final DoubleStat SCALE = new DoubleStat(1D, "scale");
	
	public AbsoluteProtection()
	{
		super("absolute_protection", EnchantmentType.BREAKABLE, EquipmentSlotType.values());
		setCategory("apex");
		addStats(SCALE);
	}
}
