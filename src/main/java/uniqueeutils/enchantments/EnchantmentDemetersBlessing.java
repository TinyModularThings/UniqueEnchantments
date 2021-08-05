package uniqueeutils.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniqueeutils.UniqueEnchantmentsUtils;

public class EnchantmentDemetersBlessing extends UniqueEnchantment
{
	public EnchantmentDemetersBlessing()
	{
		super(new DefaultData("demeters_blessing", Rarity.VERY_RARE, 2, false, 12, 8, 75), EnumEnchantmentType.BREAKABLE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
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
	
	@Override
	public void loadData(Configuration config)
	{	
	}
}