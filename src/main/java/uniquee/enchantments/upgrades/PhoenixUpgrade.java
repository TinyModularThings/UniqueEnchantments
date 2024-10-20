package uniquee.enchantments.upgrades;

import java.util.EnumSet;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.handler.MathCache;
import uniquee.UE;

public class PhoenixUpgrade extends EnchantedUpgrade
{
	public PhoenixUpgrade()
	{
		super("uniquee", "phoenix_blessing", "upgrade.uniquee.regen", () -> UE.PHOENIX_BLESSING);
		setEquimentSlots(EnumSet.allOf(EquipmentSlot.class));
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentCategory.ARMOR.canEnchant(stack.getItem());
	}
	
	@Override
	protected double getFormular(int inputPoints)
	{
		return MathCache.LOG.get(inputPoints+1)*0.1D;
	}
}