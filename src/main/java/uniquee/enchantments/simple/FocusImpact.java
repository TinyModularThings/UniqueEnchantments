package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class FocusImpact extends UniqueEnchantment
{
	public static final DoubleStat BASE_SPEED = new DoubleStat(1.05D, "attack_speed_comparison");
	
	public FocusImpact()
	{
		super(new DefaultData("focus_impact", Rarity.RARE, 3, false, 2, 8, 17), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(BASE_SPEED);
	}
		
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.SWIFT_BLADE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem;
	}
}
