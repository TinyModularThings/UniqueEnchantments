package uniqueebattle.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.DoubleStat;

public class StreakersWill extends UniqueEnchantment
{
	public static final DoubleStat LOSS_PER_LEVEL = new DoubleStat(4.25D, "loss");
	public static final DoubleLevelStats CHANCE = new DoubleLevelStats("chance", 0.1D, 0.15D);
	
	public StreakersWill()
	{
		super(new DefaultData("streakers_will", Rarity.UNCOMMON, 4, false, false, 12, 4, 15), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(LOSS_PER_LEVEL, CHANCE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof BowItem;
	}
}
