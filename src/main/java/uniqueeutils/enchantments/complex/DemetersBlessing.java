package uniqueeutils.enchantments.complex;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniqueeutils.UEUtils;

public class DemetersBlessing extends UniqueEnchantment
{
	public DemetersBlessing()
	{
		super(new DefaultData("demeters_blessing", Rarity.VERY_RARE, 2, false, true, 12, 8, 75), EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		setCategory("utils");
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof HoeItem) || !stack.isDamageableItem();
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UEUtils.DEMETERS_SOUL);
	}
	
}