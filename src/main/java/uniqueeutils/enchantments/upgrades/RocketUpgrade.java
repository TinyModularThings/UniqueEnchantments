package uniqueeutils.enchantments.upgrades;

import java.util.EnumSet;
import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import uniquebase.api.EnchantedUpgrade;
import uniqueeutils.UEUtils;

public class RocketUpgrade extends EnchantedUpgrade
{
	public static final UUID SPEED_MOD = UUID.fromString("ff50c176-52d2-4711-8665-e7c782a21616");
	public RocketUpgrade()
	{
		super("uniqueutil", "rocket_man", () -> UEUtils.ROCKET_MAN);
		setEquimentSlots(EnumSet.allOf(EquipmentSlotType.class));
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentType.ARMOR.canEnchant(stack.getItem());
	}
}