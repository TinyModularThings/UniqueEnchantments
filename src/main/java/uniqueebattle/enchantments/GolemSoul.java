package uniqueebattle.enchantments;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class GolemSoul extends UniqueEnchantment
{
	public static final UUID KNOCKBACK_MOD = UUID.fromString("195f8aca-b731-4c7c-a853-381011b1a6ec");
	public static final DoubleStat KNOCKBACK = new DoubleStat(0.25D, "knockback");
	public static final UUID SPEED_MOD = UUID.fromString("135847c6-f6ca-11eb-9a03-0242ac130003");
	public static final DoubleStat SPEED = new DoubleStat(0.05D, "speed");
	
	public GolemSoul()
	{
		super(new DefaultData("golem_soul", Rarity.COMMON, 4, false, 16, 3, 50), EnchantmentType.ARMOR_CHEST, EquipmentSlotType.CHEST);
		setCategory("battle");
		addStats(KNOCKBACK, SPEED);
	}
}