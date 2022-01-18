package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.DoubleStat;

public class StreakersWill extends UniqueEnchantment
{
	public static final DoubleStat LOSS_PER_LEVEL = new DoubleStat(11.25D, "loss");
	public static final DoubleLevelStats CHANCE = new DoubleLevelStats("chance", 0.1D, 0.15D);
	
	public StreakersWill()
	{
		super(new DefaultData("streakers_will", Rarity.UNCOMMON, 4, false, 12, 4, 15), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		addStats(LOSS_PER_LEVEL, CHANCE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemBow;
	}
}
