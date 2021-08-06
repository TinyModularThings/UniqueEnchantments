package uniqueeutils.enchantments.curse;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;

public class RocketMan extends UniqueEnchantment
{
	public static final DoubleLevelStats FLIGHT_TIME = new DoubleLevelStats("duration", 0.05D, 0.15D);
			
	public RocketMan()
	{
		super(new DefaultData("rocketman", Rarity.VERY_RARE, 3, true, 25, 4, 20), EnumEnchantmentType.ARMOR_CHEST, EntityEquipmentSlot.CHEST);
		setCategory("utils");
		addStats(FLIGHT_TIME);
		setCurse();
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled && stack.getItem() instanceof ItemElytra;
	}
}