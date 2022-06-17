package uniqueebattle.enchantments.curse;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IdStat;

public class WarsOdium extends UniqueEnchantment
{
	public static final String HIT_COUNTER = "war_counter";
	public static final UUID HEALTH_MOD = UUID.fromString("4c1a8dd9-8468-4285-bb79-11112996a3c2");
	public static final DoubleLevelStats SPAWN_CHANCE = new DoubleLevelStats("hit", 0.05D, 0.02D);
	public static final DoubleLevelStats HEALTH_BUFF = new DoubleLevelStats("health_buff", 0D, 1D);
	public static final DoubleStat MULTIPLIER = new DoubleStat(1D, "multiplier");
	public static final IdStat BLACKLIST = new IdStat("blacklist", ForgeRegistries.ENTITIES);
	
	public WarsOdium()
	{
		super(new DefaultData("wars_odium", Rarity.VERY_RARE, 2, false, true, 10, 10, 40), EnchantmentType.BREAKABLE, EquipmentSlotType.values());
		addStats(SPAWN_CHANCE, HEALTH_BUFF, MULTIPLIER, BLACKLIST);
		setCurse();
	}
	
}
