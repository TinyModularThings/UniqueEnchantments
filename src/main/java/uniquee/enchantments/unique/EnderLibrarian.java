package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;

public class EnderLibrarian extends UniqueEnchantment
{
	public EnderLibrarian()
	{
		super(new DefaultData("ender_librarian", Rarity.VERY_RARE, 1, true, true, 24, 3, 10), EnchantmentType.FISHING_ROD, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled.get() && stack.getItem() instanceof FilledMapItem;
	}
}
