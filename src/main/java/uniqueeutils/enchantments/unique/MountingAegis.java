package uniqueeutils.enchantments.unique;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import uniquebase.api.BaseUEMod;
import uniquebase.api.UniqueEnchantment;

public class MountingAegis extends UniqueEnchantment
{
	public MountingAegis()
	{
		super(new DefaultData("mounting_aegis", Rarity.RARE, 1, true, true, 24, 4, 20), BaseUEMod.ALL_TYPES, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		setCategory("utils");
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof ShieldItem);
	}
}