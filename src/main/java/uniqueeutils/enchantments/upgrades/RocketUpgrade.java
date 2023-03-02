package uniqueeutils.enchantments.upgrades;

import java.util.EnumSet;
import java.util.UUID;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.handler.MathCache;
import uniqueeutils.UEUtils;

public class RocketUpgrade extends EnchantedUpgrade
{
	public static final UUID SPEED_MOD = UUID.fromString("ff50c176-52d2-4711-8665-e7c782a21616");
	public RocketUpgrade()
	{
		super("uniqueutil", "rocket_man", "upgrade.uniqueutil.speed", () -> UEUtils.ROCKET_MAN);
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
		return MathCache.LOG.get(1+inputPoints)*0.01D;
	}
}