package uniqueapex.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import uniqueapex.enchantments.ApexEnchantment;

public class GaiasFragment extends ApexEnchantment
{
	public GaiasFragment()
	{
		super("gaias_fragment", EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND);
		setCategory("apex");
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof TieredItem);
	}
}