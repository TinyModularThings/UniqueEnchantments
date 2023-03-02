package uniquee.enchantments.upgrades;

import java.util.EnumSet;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.EnchantedUpgrade;
import uniquee.UE;

public class PestilenceUpgrade extends EnchantedUpgrade
{
	public PestilenceUpgrade()
	{
		super("uniquee", "pestilence", "upgrade.uniquee.potion_reduction", () -> UE.PESTILENCES_ODIUM);
		setEquimentSlots(EnumSet.allOf(EquipmentSlot.class));
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentCategory.WEAPON.canEnchant(stack.getItem()) || EnchantmentCategory.ARMOR.canEnchant(stack.getItem()) || EnchantmentCategory.DIGGER.canEnchant(stack.getItem());
	}
	
	@Override
	protected double getFormular(int inputPoints)
	{
		return 100/(100+Math.pow(inputPoints, 0.25));
	}
}
