package uniqueeutils.enchantments.upgrades;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
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
		return EnchantmentCategory.ARMOR.canEnchant(stack.getItem()) || EnchantmentCategory.DIGGER.canEnchant(stack.getItem()) || EnchantmentCategory.WEAPON.canEnchant(stack.getItem());
	}
	
}
