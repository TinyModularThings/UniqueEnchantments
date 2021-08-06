package uniqueeutils.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniqueeutils.UniqueEnchantmentsUtils;

public class DemetersBlessing extends UniqueEnchantment
{
	public DemetersBlessing()
	{
		super(new DefaultData("demeters_blessing", Rarity.VERY_RARE, 2, false, 12, 8, 75), EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		setCategory("utils");
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof ItemHoe);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantmentsUtils.DEMETERS_SOUL);
	}
}