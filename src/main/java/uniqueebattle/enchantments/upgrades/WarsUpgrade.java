package uniqueebattle.enchantments.upgrades;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import uniquebase.api.EnchantedUpgrade;
import uniqueebattle.UEBattle;

public class WarsUpgrade extends EnchantedUpgrade
{
	public WarsUpgrade()
	{
		super("uniquebattle", "wars_odium", () -> UEBattle.WARS_ODIUM);
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentType.WEAPON.canEnchant(stack.getItem());
	}
	
}
