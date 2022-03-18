package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class PerpetualStrike extends UniqueEnchantment
{
	public static final DoubleStat PER_HIT = new DoubleStat(0.1D, "bonus_per_hit");
	public static final DoubleStat PER_HIT_LEVEL = new DoubleStat(1D, "bonus_per_level");
	public static final DoubleStat MULTIPLIER = new DoubleStat(1.83D, "damage_multiplier");
	public static final String HIT_COUNT = "strikes";
	public static final String HIT_ID = "hit_id";
	
	public PerpetualStrike()
	{
		super(new DefaultData("perpetualstrike", Rarity.RARE, 3, false, true, 16, 6, 4), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(PER_HIT, MULTIPLIER, PER_HIT_LEVEL);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.SPARTAN_WEAPON);
	}
}
