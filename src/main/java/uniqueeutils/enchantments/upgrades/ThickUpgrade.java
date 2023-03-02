package uniqueeutils.enchantments.upgrades;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.handler.MathCache;
import uniqueeutils.UEUtils;

public class ThickUpgrade extends EnchantedUpgrade
{
	public ThickUpgrade()
	{
		super("uniqueutil", "thick_pick", "upgrade.uniqueutil.mining_speed", () -> UEUtils.THICK_PICK);
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentCategory.DIGGER.canEnchant(stack.getItem());
	}	
	
	@Override
	protected double getFormular(int inputPoints)
	{
		return MathCache.LOG.get(inputPoints+1);
	}
}