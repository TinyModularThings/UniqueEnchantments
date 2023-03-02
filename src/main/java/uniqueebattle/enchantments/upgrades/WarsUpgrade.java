package uniqueebattle.enchantments.upgrades;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.handler.MathCache;
import uniqueebattle.UEBattle;

public class WarsUpgrade extends EnchantedUpgrade
{
	public WarsUpgrade()
	{
		super("uniquebattle", "wars_odium", "upgrade.uniquebattle.lifesteal", () -> UEBattle.WARS_ODIUM);
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentCategory.WEAPON.canEnchant(stack.getItem());
	}
	
	@Override
	protected double getFormular(int inputPoints)
	{
		return MathCache.LOG10.get(inputPoints+1);
	}
}
