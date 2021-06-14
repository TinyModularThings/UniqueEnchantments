package uniqueeutils.enchantments;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.common.ForgeConfigSpec;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;

public class MountingAegisEnchantment extends UniqueEnchantment
{
	public MountingAegisEnchantment()
	{
		super(new DefaultData("mounting_aegis", Rarity.RARE, 1, true, 24, 4, 20), UniqueEnchantments.ALL_TYPES, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof ShieldItem);
	}
	
	@Override
	public void loadData(ForgeConfigSpec.Builder config)
	{	
	}
}