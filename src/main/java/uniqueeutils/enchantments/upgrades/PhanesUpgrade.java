package uniqueeutils.enchantments.upgrades;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import uniquebase.api.EnchantedUpgrade;
import uniqueeutils.UEUtils;

public class PhanesUpgrade extends EnchantedUpgrade
{
	public static final String SHIELD_STORAGE = "ue_utils_shield";
	
	public PhanesUpgrade()
	{
		super("uniqueutil", "phanes_regret", "upgrade.uniqueutil.shield", () -> UEUtils.PHANES_REGRET);
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentType.ARMOR.canEnchant(stack.getItem()) || EnchantmentType.DIGGER.canEnchant(stack.getItem()) || EnchantmentType.WEAPON.canEnchant(stack.getItem());
	}
	
}
