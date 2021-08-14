package uniquee.enchantments.curse;

import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.BaseUEMod;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class PestilencesOdium extends UniqueEnchantment
{
	public static final DoubleStat RADIUS = new DoubleStat(7, "radius");
	public static final IntStat DELAY = new IntStat(300, "delay");
	public static final DoubleStat DAMAGE_PER_TICK = new DoubleStat(0.25F, "damage_per_tick");
	
	public PestilencesOdium()
	{
		super(new DefaultData("pestilences_odium", Rarity.RARE, 2, false, true, 10, 4, 40), BaseUEMod.ALL_TYPES, EquipmentSlotType.values());
		addStats(RADIUS, DELAY, DAMAGE_PER_TICK);
		setCurse();
	}	
}
