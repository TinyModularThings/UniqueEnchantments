package uniquee.enchantments.curse;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class PestilencesOdium extends UniqueEnchantment
{
	public static final DoubleStat RADIUS = new DoubleStat(7, "radius");
	public static final IntStat DELAY = new IntStat(300, "delay");
	public static final DoubleStat DAMAGE_PER_TICK = new DoubleStat(0.25D, "damage_per_tick");
	
	public PestilencesOdium()
	{
		super(new DefaultData("pestilences_odium", Rarity.RARE, 2, false, 10, 4, 40), EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
		addStats(RADIUS, DELAY, DAMAGE_PER_TICK);
	}
		
	@Override
	public boolean isCurse()
	{
		return true;
	}	
}