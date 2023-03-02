package uniqueeutils.enchantments.upgrades;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.handler.MathCache;
import uniqueeutils.UEUtils;

public class FaminesUpgrade extends EnchantedUpgrade
{
	public FaminesUpgrade()
	{
		super("uniqueutil", "famines_odium", "upgrade.uniqueutil.give_effect", () -> UEUtils.FAMINES_ODIUM);
	}
	
	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentCategory.WEAPON.canEnchant(stack.getItem());
	}
	
	@Override
	protected double getFormular(int inputPoints)
	{
		return 20*MathCache.LOG.get(1+inputPoints);
	}
}