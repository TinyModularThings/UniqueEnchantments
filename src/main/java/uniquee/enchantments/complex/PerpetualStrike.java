package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class PerpetualStrike extends UniqueEnchantment
{
	public static final DoubleStat PER_HIT = new DoubleStat(0.1D, "bonus_per_hit");
	public static final DoubleStat PER_HIT_LEVEL = new DoubleStat(1D, "bonus_per_level");
	public static final DoubleStat MULTIPLIER = new DoubleStat(1.83D, "damage_multiplier");
	public static final String HIT_COUNT = "strikes";
	public static final String HIT_ID = "hit_id";
	
	public PerpetualStrike()
	{
		super(new DefaultData("perpetualstrike", Rarity.RARE, 3, false, 16, 6, 4), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND);
		addStats(PER_HIT, MULTIPLIER, PER_HIT_LEVEL);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.SPARTAN_WEAPON);
	}
}
