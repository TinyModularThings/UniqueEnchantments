package uniqueeutils.enchantments.complex;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniqueeutils.UniqueEnchantmentsUtils;

public class DemetersBlessing extends UniqueEnchantment
{
	public DemetersBlessing()
	{
		super(new DefaultData("demeters_blessing", Rarity.VERY_RARE, 2, false, 12, 8, 75), EnchantmentType.BREAKABLE, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		setCategory("utils");
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof HoeItem);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantmentsUtils.DEMETERS_SOUL);
	}
	
}