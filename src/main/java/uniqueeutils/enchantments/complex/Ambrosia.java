package uniqueeutils.enchantments.complex;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SplashPotionItem;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.IntStat;

public class Ambrosia extends UniqueEnchantment
{
	public static final IntStat BASE_DURATION = new IntStat(600, "base_duration");
	public static final IntStat DURATION_MULTIPLIER = new IntStat(40, "duration_multiplier");
	
	public Ambrosia()
	{
		super(new DefaultData("ambrosia", Rarity.RARE, 4, true, true, 8, 12, 10), EnchantmentType.BREAKABLE, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		setCategory("utils");
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem().isEdible() || stack.getItem() instanceof PotionItem || stack.getItem() instanceof SplashPotionItem || stack.getItem() instanceof LingeringPotionItem;
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !canApplyToItem(stack);
	}
	
}