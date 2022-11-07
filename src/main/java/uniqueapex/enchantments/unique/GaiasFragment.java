package uniqueapex.enchantments.unique;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniqueapex.enchantments.ApexEnchantment;

public class GaiasFragment extends ApexEnchantment
{
	public GaiasFragment()
	{
		super("gaias_fragment", EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND);
		setCategory("apex");
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof TieredItem);
	}
}