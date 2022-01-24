package uniquee.enchantments.unique;

import java.util.List;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import uniquebase.api.ICustomItemEnchantment;
import uniquebase.api.UniqueEnchantment;

public class EnderLibrarian extends UniqueEnchantment implements ICustomItemEnchantment
{	
	public EnderLibrarian()
	{
		super(new DefaultData("ender_librarian", Rarity.VERY_RARE, 1, true, 24, 3, 10), EnumEnchantmentType.FISHING_ROD, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		setDisableDefaultItems();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemMap;
	}
	
	@Override
	public void addCustomItems(List<ItemStack> list)
	{
		list.add(new ItemStack(Items.FILLED_MAP));
	}
}
