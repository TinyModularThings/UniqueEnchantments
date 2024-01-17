package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.DoubleStat;

public class Range extends UniqueEnchantment
{
	public static final UUID RANGE_MOD = UUID.fromString("3b35b821-d4d7-4aa3-8c64-e9849f43516a");
	public static final UUID COMBAT_MOD = UUID.fromString("0d805b4a-95c0-11ed-a1eb-0242ac120002");
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("mining_fatique", 0.8D, 0.3D);
	public static final DoubleStat COMBAT = new DoubleStat(0.25D, "mining_fatique");
	public static final DoubleStat REDUCTION = new DoubleStat(0.05D, "reduction");
	
	public Range()
	{
		super(new DefaultData("range", Rarity.UNCOMMON, 4, false, false, 10, 10, 75), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);
		addStats(RANGE, REDUCTION, COMBAT);
	}
		
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return EnchantmentCategory.DIGGER.canEnchant(stack.getItem()) || stack.getItem() instanceof TridentItem;
	}
}
