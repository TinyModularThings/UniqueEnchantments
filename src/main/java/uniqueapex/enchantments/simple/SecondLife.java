package uniqueapex.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import uniqueapex.enchantments.ApexEnchantment;
import uniquebase.utils.DoubleStat;

public class SecondLife extends ApexEnchantment
{
	public static final DoubleStat RESTORE = new DoubleStat(1D, "restore");
	
	public SecondLife()
	{
		super("second_life", EnchantmentType.BREAKABLE, EquipmentSlotType.values());
		setCategory("apex");
		addStats(RESTORE);
	}
}
