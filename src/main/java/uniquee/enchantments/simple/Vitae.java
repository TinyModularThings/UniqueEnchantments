package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;
import uniquebase.utils.SlotUUID;

public class Vitae extends UniqueEnchantment
{
	public static final SlotUUID HEALTH_MOD = new SlotUUID(
			"cacfc3c0-bb1d-402e-9509-e961af0776c5", "1ae23c8c-1b59-4bcc-85c3-e5aa5ce75fe1", "aa0db628-1082-42dc-8f97-bb935ed325c2",
			"72a87567-c59a-42a8-90e7-1ce787d42ad7", "11908392-e901-4578-9913-af800f6c4b8d", "1140ac9d-2b67-41b2-a1bc-1d9b4f3a6fb8");
	public static final IntStat BASE_BOOST = new IntStat(5, "base_boost");
	public static final DoubleStat SCALE_BOOST = new DoubleStat(1D, "scale_boost");
	
	public Vitae()
	{
		super(new DefaultData("vitae", Rarity.RARE, 5, false, 18, 8, 5), EnumEnchantmentType.ARMOR, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET);
		addStats(BASE_BOOST, SCALE_BOOST);
	}
}