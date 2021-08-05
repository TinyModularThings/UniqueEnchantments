package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnderLibrarian extends UniqueEnchantment
{
	public EnderLibrarian()
	{
		super(new DefaultData("ender_librarian", Rarity.VERY_RARE, 1, true, 24, 3, 10), EnumEnchantmentType.FISHING_ROD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
		
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled && stack.getItem() instanceof ItemMap;
	}
	
	@Override
	public void loadData(Configuration config){}
}
