package uniquee.enchantments.upgrades;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.handler.MathCache;
import uniquee.UE;

public class DeathsUpgrade extends EnchantedUpgrade
{
	public DeathsUpgrade()
	{
		super("uniquee", "deaths", "upgrade.uniquee.percent_damage", () -> UE.DEATHS_ODIUM);
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentCategory.WEAPON.canEnchant(stack.getItem());
	}

	@Override
	protected double getFormular(int inputPoints)
	{
		return MathCache.LOG10.get(inputPoints+1)*0.01D;
	}
	
}
