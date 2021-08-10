package uniqueeutils.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemLingeringPotion;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.IntStat;

public class Ambrosia extends UniqueEnchantment
{
	public static final IntStat BASE_DURATION = new IntStat(600, "base_duration");
	public static final IntStat DURATION_MULTIPLIER = new IntStat(40, "duration_multiplier");
	
	public Ambrosia()
	{
		super(new DefaultData("ambrosia", Rarity.RARE, 4, true, 8, 12, 10), EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		setCategory("utils");
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemFood || stack.getItem() instanceof ItemPotion || stack.getItem() instanceof ItemSplashPotion || stack.getItem() instanceof ItemLingeringPotion;
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !canApplyToItem(stack);
	}
	
}