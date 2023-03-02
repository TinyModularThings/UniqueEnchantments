package uniqueebattle.enchantments.upgrades;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.handler.MathCache;
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
	
	@Override
	protected double getFormular(int inputPoints)
	{
		return 1+(MathCache.LOG.get(1+inputPoints)*0.01D);
	}
}
