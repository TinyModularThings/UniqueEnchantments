package uniquee.enchantments.complex;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.BooleanStat;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;
import uniquee.UE;

public class PerpetualStrike extends UniqueEnchantment
{
	public static final DoubleStat PER_HIT_LEVEL = new DoubleStat(1D, "bonus_per_level");
	public static final DoubleStat MULTIPLIER = new DoubleStat(1D, "damage_multiplier");
	public static final IntStat TRANSCENDED_MERCY = new IntStat(1, "transcended_mercy_hits");
	public static final IntStat HIT_CAP = new IntStat(Integer.MAX_VALUE, "hit_cap");
	public static final String HIT_COUNT = "strikes";
	public static final String HIT_ID = "hit_id";
	public static final BooleanStat SCALING_STATE = new BooleanStat(false, "exponential_scaling", "changes damage formula to exponential instead of logarithmic");

	public PerpetualStrike()
	{
		super(new DefaultData("perpetual_strike", Rarity.RARE, 3, false, true, 16, 6, 4).setTrancendenceLevel(600), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);
		addStats(MULTIPLIER, PER_HIT_LEVEL, TRANSCENDED_MERCY, HIT_CAP, SCALING_STATE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof TridentItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.SPARTAN_WEAPON);
	}
}
