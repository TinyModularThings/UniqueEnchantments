package uniqueeutils.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import uniquebase.api.UniqueEnchantment;

public class MountingAegis extends UniqueEnchantment
{
	public MountingAegis()
	{
		super(new DefaultData("mounting_aegis", Rarity.RARE, 1, true, 24, 4, 20), EnchantmentType.ALL, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		setCategory("utils");
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof ShieldItem);
	}
}