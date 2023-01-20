package uniqueeutils.enchantments.complex;

import java.util.UUID;

import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class SleipnirsGrace extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("16aea480-f9a0-4409-8a14-416ba3382d31");
	public static final String HORSE_NBT = "timetracker";
	public static final DoubleStat CAP = new DoubleStat(100D, "cap");
	public static final DoubleStat GAIN = new DoubleStat(1D, "speed_gain");
	public static final DoubleStat MAX = new DoubleStat(100D, "max");
	public static final DoubleStat PATH_BONUS = new DoubleStat(5D, "path_bonus");
	
	public SleipnirsGrace()
	{
		super(new DefaultData("sleipnirs_grace", Rarity.RARE, 10, true, true, 20, 14, 75), EnchantmentCategory.ARMOR_CHEST);
		setCategory("utils");
		addStats(CAP, GAIN, MAX, PATH_BONUS);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof HorseArmorItem;
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return EnchantmentCategory.ARMOR_CHEST.canEnchant(stack.getItem());
	}
}
