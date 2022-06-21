package uniqueebattle.enchantments.upgrades;

import java.util.EnumSet;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import uniquebase.api.EnchantedUpgrade;
import uniqueebattle.UEBattle;

public class LunaticUpgrade extends EnchantedUpgrade
{
	public LunaticUpgrade()
	{
		super("uniquebattle", "lunatic_despair", "upgrade.uniquebattle.use_time", () -> UEBattle.LUNATIC_DESPAIR);
		setEquimentSlots(EnumSet.allOf(EquipmentSlotType.class));
	}
	
	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentType.ARMOR.canEnchant(stack.getItem());
	}
}
