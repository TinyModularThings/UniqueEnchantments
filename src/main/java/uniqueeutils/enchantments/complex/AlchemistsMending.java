package uniqueeutils.enchantments.complex;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;

public class AlchemistsMending extends UniqueEnchantment {

	public AlchemistsMending() {
		super(new DefaultData("alchemists_mending", Rarity.VERY_RARE, 3, true, false, 40, 30, 75), EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
		setCategory("utils");
	}

	@Override
	protected boolean canNotApplyToItems(ItemStack stack) {
		return !stack.isDamageableItem();
	}
}
