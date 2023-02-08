package uniqueebattle.enchantments.curse;

import java.util.UUID;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IdStat;

public class WarsOdium extends UniqueEnchantment
{
	public static final String HIT_COUNTER = "war_counter";
	public static final UUID HEALTH_MOD = UUID.fromString("4c1a8dd9-8468-4285-bb79-11112996a3c2");
	public static final DoubleStat SPAWN_CHANCE = new DoubleStat(0.95D, "hit");
	public static final DoubleStat BOSS_CHANCE = new DoubleStat(0.93, "boss_hit");
	public static final DoubleLevelStats HEALTH_BUFF = new DoubleLevelStats("health_buff", 0D, 1D);
	public static final DoubleStat MULTIPLIER = new DoubleStat(1D, "multiplier");
	public static final IdStat<EntityType<?>> BLACKLIST = new IdStat<>("blacklist", ForgeRegistries.ENTITY_TYPES);
	public static final IdStat<EntityType<?>> BOSS_LIST = new IdStat<>("bossList", ForgeRegistries.ENTITY_TYPES, EntityType.WITHER, EntityType.ENDER_DRAGON);
	
	public WarsOdium()
	{
		super(new DefaultData("wars_odium", Rarity.VERY_RARE, 2, false, true, 10, 10, 40), EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
		addStats(SPAWN_CHANCE, BOSS_CHANCE, HEALTH_BUFF, MULTIPLIER, BLACKLIST, BOSS_LIST);
		setCurse();
	}
	
}
