package uniquee.enchantments.unique;

import java.util.List;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.ICustomItemEnchantment;
import uniquebase.api.UniqueEnchantment;

public class EnderLibrarian extends UniqueEnchantment implements ICustomItemEnchantment
{
	public EnderLibrarian()
	{
		super(new DefaultData("ender_librarian", Rarity.VERY_RARE, 10, true, true, 20, 3, 50), EnchantmentCategory.FISHING_ROD, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		setDisableDefaultItems();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof MapItem;
	}

	@Override
	public void addCustomItems(List<ItemStack> list)
	{
		list.add(new ItemStack(Items.FILLED_MAP));
	}
}
