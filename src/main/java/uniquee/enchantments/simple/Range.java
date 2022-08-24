package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;

public class Range extends UniqueEnchantment
{
	public static final UUID RANGE_MOD = UUID.fromString("3b35b821-d4d7-4aa3-8c64-e9849f43516a");
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("mining_fatique", 0.8D, 0.3D);
	public static final DoubleLevelStats REDUCTION = new DoubleLevelStats("reduction", 1D, 2D);
	
	public Range()
	{
		super(new DefaultData("range", Rarity.UNCOMMON, 4, false, false, 10, 10, 75), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);
		addStats(RANGE, REDUCTION);
	}
		
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return EnchantmentCategory.DIGGER.canEnchant(stack.getItem());
	}
}
