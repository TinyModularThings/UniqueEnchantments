package uniquee.enchantments.upgrades;

import java.util.EnumSet;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import uniquebase.api.EnchantedUpgrade;
import uniquee.UE;

public class PhoenixUpgrade extends EnchantedUpgrade
{
	public PhoenixUpgrade()
	{
		super("uniquee", "phoenix_blessing", "upgrade.uniquee.regen", () -> UE.PHOENIX_BLESSING);
		setEquimentSlots(EnumSet.allOf(EquipmentSlotType.class));
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentType.ARMOR.canEnchant(stack.getItem());
	}
}