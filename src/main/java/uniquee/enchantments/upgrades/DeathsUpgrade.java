package uniquee.enchantments.upgrades;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import uniquebase.api.EnchantedUpgrade;
import uniquee.UE;

public class DeathsUpgrade extends EnchantedUpgrade
{
	public DeathsUpgrade()
	{
		super("uniquee", "deaths", () -> UE.DEATHS_ODIUM);
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentType.WEAPON.canEnchant(stack.getItem());
	}
	
}
