package uniqueebattle.enchantments.upgrades;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.EnchantedUpgrade;
import uniqueebattle.UEBattle;

public class AresUpgrade extends EnchantedUpgrade
{
	public AresUpgrade()
	{
		super("uniquebattle", "ares_fragment", "upgrade.uniquebattle.crit", () -> UEBattle.ARES_FRAGMENT);
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentCategory.WEAPON.canEnchant(stack.getItem());
	}
	
}
