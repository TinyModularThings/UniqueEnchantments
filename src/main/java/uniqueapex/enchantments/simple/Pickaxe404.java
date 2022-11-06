package uniqueapex.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import uniqueapex.enchantments.ApexEnchantment;

public class Pickaxe404 extends ApexEnchantment
{
	public Pickaxe404()
	{
		super("pickaxe", EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND);
		setCategory("apex");
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof PickaxeItem);
	}
}
