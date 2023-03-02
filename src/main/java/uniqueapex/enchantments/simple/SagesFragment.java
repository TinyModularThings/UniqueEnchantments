package uniqueapex.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniqueapex.enchantments.ApexEnchantment;
import uniquebase.utils.DoubleStat;

public class SagesFragment extends ApexEnchantment
{
	public static final DoubleStat SCALE = new DoubleStat(1.0, "scale");
	
	public SagesFragment()
	{
		super("sages_fragment", EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND);
		addStats(SCALE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return EnchantmentCategory.WEAPON.canEnchant(stack.getItem()) || EnchantmentCategory.TRIDENT.canEnchant(stack.getItem());
	}
}
