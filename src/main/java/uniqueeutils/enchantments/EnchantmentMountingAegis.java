package uniqueeutils.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentMountingAegis extends UniqueEnchantment
{
	public EnchantmentMountingAegis()
	{
		super(new DefaultData("mounting_aegis", Rarity.RARE, 1, true, 24, 4, 20), EnumEnchantmentType.ALL, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof ItemShield);
	}
	
	@Override
	public void loadData(Configuration config)
	{	
	}
}