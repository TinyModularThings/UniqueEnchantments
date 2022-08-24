package uniqueeutils.enchantments.curse;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;

public class RocketMan extends UniqueEnchantment
{
	public static final DoubleLevelStats FLIGHT_TIME = new DoubleLevelStats("duration", 0.05D, 0.15D);
			
	public RocketMan()
	{
		super(new DefaultData("rocket_man", Rarity.VERY_RARE, 3, true, true, 25, 4, 20), EnchantmentCategory.ARMOR_CHEST, EquipmentSlot.CHEST);
		setCategory("utils");
		addStats(FLIGHT_TIME);
		setCurse();
		setDisableDefaultItems();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)	{
		return stack.getItem() instanceof ElytraItem;
	}
}