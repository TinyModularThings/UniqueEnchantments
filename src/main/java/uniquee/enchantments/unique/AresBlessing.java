package uniquee.enchantments.unique;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class AresBlessing extends UniqueEnchantment
{
	public static final DoubleStat BASE_DAMAGE = new DoubleStat(6D, "scalar");
	
	public AresBlessing()
	{
		super(new DefaultData("ares_blessing", Rarity.VERY_RARE, 3, true, false, 28, 2, 45), EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
		addStats(BASE_DAMAGE);
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack) {
		return !stack.isDamageableItem();
	}
}