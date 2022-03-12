package uniqueebattle.enchantments;

import java.util.UUID;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.DoubleStat;

public class WarsOdium extends UniqueEnchantment
{
	public static final String HIT_COUNTER = "war_counter";
	public static final UUID HEALTH_MOD = UUID.fromString("4c1a8dd9-8468-4285-bb79-11112996a3c2");
	public static final DoubleLevelStats SPAWN_CHANCE = new DoubleLevelStats("hit", 0.05D, 0.0125D);
	public static final DoubleLevelStats HEALTH_BUFF = new DoubleLevelStats("health_buff", 0D, 1D);
	public static final DoubleStat MULTIPLIER = new DoubleStat(1D, "multiplier");
	
	public WarsOdium()
	{
		super(new DefaultData("wars_odium", Rarity.VERY_RARE, 2, false, 10, 10, 40), EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.values());
		addStats(SPAWN_CHANCE, HEALTH_BUFF, MULTIPLIER);
		setCurse();
	}
	
}
