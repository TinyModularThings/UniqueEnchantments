package uniqueapex.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniqueapex.enchantments.ApexEnchantment;

public class Pickaxe404 extends ApexEnchantment
{
	public Pickaxe404()
	{
		super("pickaxe404", EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND);
		setCategory("apex");
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof PickaxeItem);
	}
}
