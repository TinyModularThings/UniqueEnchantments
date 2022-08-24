package uniqueapex.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniqueapex.enchantments.ApexEnchantment;
import uniquebase.utils.DoubleStat;

public class SecondLife extends ApexEnchantment
{
	public static final DoubleStat RESTORE = new DoubleStat(1D, "restore");
	
	public SecondLife()
	{
		super("second_life", EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
		setCategory("apex");
		addStats(RESTORE);
	}
}
