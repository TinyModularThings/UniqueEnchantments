package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class SwiftBlade extends UniqueEnchantment
{
	public static final DoubleStat BASE_SPEED = new DoubleStat(1.2D, "base_speed");

	public SwiftBlade()
	{
		super(new DefaultData("swiftblade", Rarity.VERY_RARE, 2, false, 30, 85, 5), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND);
		addStats(BASE_SPEED);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.BERSERKER);
	}
}
