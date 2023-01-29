package uniqueeutils.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class Dreams extends UniqueEnchantment 
{

	public static final DoubleStat DURABILITY_FACTOR = new DoubleStat(1.0D, "durability_factor");
	
	public Dreams()
	{
		super(new DefaultData("dreams", Rarity.VERY_RARE, 3, false, false, 20, 5, 25), EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
		setCategory("utils");
		addStats(DURABILITY_FACTOR);
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack) {
		return !stack.isDamageableItem();
	}
}
