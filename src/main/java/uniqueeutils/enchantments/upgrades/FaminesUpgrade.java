package uniqueeutils.enchantments.upgrades;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import uniquebase.api.EnchantedUpgrade;
import uniqueeutils.UEUtils;

public class FaminesUpgrade extends EnchantedUpgrade
{
	public FaminesUpgrade()
	{
		super("uniqueutil", "famines_odium", () -> UEUtils.FAMINES_ODIUM);
	}
	
	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentType.WEAPON.canEnchant(stack.getItem());
	}
}