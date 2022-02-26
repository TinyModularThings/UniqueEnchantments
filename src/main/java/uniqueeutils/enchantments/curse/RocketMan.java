package uniqueeutils.enchantments.curse;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;

public class RocketMan extends UniqueEnchantment
{
	public static final DoubleLevelStats FLIGHT_TIME = new DoubleLevelStats("duration", 0.05D, 0.15D);
			
	public RocketMan()
	{
		super(new DefaultData("rocketman", Rarity.VERY_RARE, 3, true, 25, 4, 20), EnchantmentType.ARMOR_CHEST, EquipmentSlotType.CHEST);
		setCategory("utils");
		addStats(FLIGHT_TIME);
		setCurse();
		setDisableDefaultItems();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ElytraItem;
	}
}