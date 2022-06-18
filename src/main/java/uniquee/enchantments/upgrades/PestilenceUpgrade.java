package uniquee.enchantments.upgrades;

import java.util.EnumSet;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import uniquebase.api.EnchantedUpgrade;
import uniquee.UE;

public class PestilenceUpgrade extends EnchantedUpgrade
{
	public PestilenceUpgrade()
	{
		super("uniquee", "pestilence", () -> UE.PESTILENCES_ODIUM);
		setEquimentSlots(EnumSet.allOf(EquipmentSlotType.class));
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentType.WEAPON.canEnchant(stack.getItem()) || EnchantmentType.ARMOR.canEnchant(stack.getItem()) || EnchantmentType.DIGGER.canEnchant(stack.getItem());
	}
	
}
