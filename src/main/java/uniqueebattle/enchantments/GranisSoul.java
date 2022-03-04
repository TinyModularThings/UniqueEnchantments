package uniqueebattle.enchantments;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntLevelStats;
import uniquebase.utils.IntStat;

public class GranisSoul extends UniqueEnchantment
{
	public static final UUID DASH_ID = UUID.fromString("092a2a89-bdac-4323-be57-4062ad7b0fd2");
	public static final String NEXT_DASH = "granis_next_dash";
	public static final String DASH_TIME = "granis_dash";
	public static final IntLevelStats BLEED_DURATION = new IntLevelStats("bleed_duration", 200, 20);
	public static final IntLevelStats BLEED_RANGE = new IntLevelStats("bleed_range", 3, 1);
	public static final IntStat DASH_DURATION = new IntStat(50, "dash_duration");
	public static final DoubleStat DASH_SPEED = new DoubleStat(9D, "dash_speed"); 
	
	public GranisSoul()
	{
		super(new DefaultData("granis_soul", Rarity.RARE, 5, true, false, 40, 10, 20), EnchantmentType.ARMOR_CHEST);
		addStats(BLEED_DURATION, BLEED_RANGE, DASH_DURATION, DASH_SPEED);
		setDisableDefaultItems();
		setCategory("battle");
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof HorseArmorItem;
	}
}
