package uniqueeutils.enchantments.upgrades;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import uniquebase.api.EnchantedUpgrade;
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
		return EnchantmentType.DIGGER.canEnchant(stack.getItem());
	}	
}