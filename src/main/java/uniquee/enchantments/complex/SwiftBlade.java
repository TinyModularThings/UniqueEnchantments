package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class SwiftBlade extends UniqueEnchantment
{
	public static final DoubleStat BASE_SPEED = new DoubleStat(1.2D, "base_speed");

	public SwiftBlade()
	{
		super(new DefaultData("swiftblade", Rarity.VERY_RARE, 2, false, false, 30, 85, 5), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(BASE_SPEED);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.BERSERKER);
	}
}
