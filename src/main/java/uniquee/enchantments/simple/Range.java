package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;

public class Range extends UniqueEnchantment
{
	public static final UUID RANGE_MOD = UUID.fromString("3b35b821-d4d7-4aa3-8c64-e9849f43516a");
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("mining_fatique", 0.8D, 0.3D);
	public static final DoubleLevelStats REDUCTION = new DoubleLevelStats("reduction", 1D, 2D);
	
	public Range()
	{
		super(new DefaultData("ranged", Rarity.COMMON, 4, false, false, 12, 8, 75), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(RANGE, REDUCTION);
	}
		
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return EnchantmentType.DIGGER.canEnchantItem(stack.getItem());
	}
}
