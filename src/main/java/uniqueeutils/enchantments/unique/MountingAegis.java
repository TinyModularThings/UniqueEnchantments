package uniqueeutils.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;

public class MountingAegis extends UniqueEnchantment
{
	public MountingAegis()
	{
		super(new DefaultData("mounting_aegis", Rarity.RARE, 1, true, 24, 4, 20), EnumEnchantmentType.ALL, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		setCategory("utils");
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof ItemShield);
	}
}