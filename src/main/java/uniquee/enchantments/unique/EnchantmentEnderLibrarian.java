package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentEnderLibrarian extends UniqueEnchantment
{
	public EnchantmentEnderLibrarian()
	{
		super(new DefaultData("ender_librarian", Rarity.VERY_RARE, true, 24, 3, 10), EnchantmentType.FISHING_ROD, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 1;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled.get() && stack.getItem() instanceof FilledMapItem;
	}
	
	@Override
	public void loadData(Builder config){}
}
