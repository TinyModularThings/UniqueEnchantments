package uniquee.enchantments.upgrades;

import java.util.EnumSet;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.handler.MathCache;

public class ProtectionUpgrade extends EnchantedUpgrade 
{
	public ProtectionUpgrade() {
		super("uniqueapex", "protection", "upgrade.uniqueapex.protection", () -> Enchantments.ALL_DAMAGE_PROTECTION);
		setEquimentSlots(EnumSet.allOf(EquipmentSlot.class));
	}

	@Override
	public boolean isValid(ItemStack stack) {
		return EnchantmentCategory.ARMOR.canEnchant(stack.getItem());
	}

	@Override
	protected double getFormular(int inputPoints)
	{
		return 1-(MathCache.LOG10.get(inputPoints+1)*0.01D);
	}
}
