package uniquee.enchantments.curse;

import net.minecraft.world.entity.EquipmentSlot;
import uniquebase.api.BaseUEMod;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class PestilencesOdium extends UniqueEnchantment
{
	public static final DoubleStat RADIUS = new DoubleStat(7, "radius");
	public static final IntStat DELAY = new IntStat(300, "delay");
	public static final DoubleStat DAMAGE_PER_TICK = new DoubleStat(1.0F, "damage_per_tick");
	public static final String PESTILENCE_ARMOR_MOD = "pestilence_armor_mod";
	public static final String PESTILENCE_TOUGHNESS_MOD = "pestilence_toughness_mod";
	
	public PestilencesOdium()
	{
		super(new DefaultData("pestilences_odium", Rarity.RARE, 2, false, true, 10, 4, 40), BaseUEMod.ALL_TYPES, EquipmentSlot.values());
		addStats(RADIUS, DELAY, DAMAGE_PER_TICK);
		setCurse();
	}	
}
