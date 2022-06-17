package uniqueebattle.enchantments.upgrades;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import uniquebase.api.EnchantedUpgrade;
import uniqueebattle.UEBattle;

public class AresUpgrade extends EnchantedUpgrade
{
	public AresUpgrade()
	{
		super("ue_battle", "ares_fragment", () -> UEBattle.ARES_FRAGMENT);
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentType.WEAPON.canEnchant(stack.getItem());
	}
	
}
