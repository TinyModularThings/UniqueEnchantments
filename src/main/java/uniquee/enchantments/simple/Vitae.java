package uniquee.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.SlotUUID;

public class Vitae extends UniqueEnchantment
{
	public static final SlotUUID HEALTH_MOD = new SlotUUID(
			"cacfc3c0-bb1d-402e-9509-e961af0776c5", "1ae23c8c-1b59-4bcc-85c3-e5aa5ce75fe1", "aa0db628-1082-42dc-8f97-bb935ed325c2",
			"72a87567-c59a-42a8-90e7-1ce787d42ad7", "11908392-e901-4578-9913-af800f6c4b8d", "1140ac9d-2b67-41b2-a1bc-1d9b4f3a6fb8");
	public static final DoubleStat BASE_BOOST = new DoubleStat(29.482631D, "base_boost");
	public static final DoubleStat SCALE_BOOST = new DoubleStat(1D, "scale_boost");
	
	public Vitae()
	{
		super(new DefaultData("vitae", Rarity.RARE, 5, false, false, 5, 5, 5), EnchantmentCategory.ARMOR, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
		addStats(BASE_BOOST, SCALE_BOOST);
	}
	
}