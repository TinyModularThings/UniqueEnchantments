package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class FocusImpact extends UniqueEnchantment
{
	public static final DoubleStat BASE_SPEED = new DoubleStat(1.2D, "attack_speed_comparison");
	
	public FocusImpact()
	{
		super(new DefaultData("focus_impact", Rarity.RARE, 3, false, 2, 8, 17), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND);
		addStats(BASE_SPEED);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.SWIFT_BLADE);
	}	
}
