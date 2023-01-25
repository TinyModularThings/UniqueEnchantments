package uniqueeutils.enchantments.complex;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.BooleanStat;
import uniquebase.utils.IntStat;

public class Ambrosia extends UniqueEnchantment
{
	public static final IntStat BASE_DURATION = new IntStat(600, "base_duration");
	public static final IntStat DURATION_MULTIPLIER = new IntStat(40, "duration_multiplier");
	public static final BooleanStat HEALING = new BooleanStat(true, "healing_enabled");
	
	public Ambrosia()
	{
		super(new DefaultData("ambrosia", Rarity.RARE, 4, true, true, 8, 12, 10), EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(BASE_DURATION, DURATION_MULTIPLIER, HEALING);
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