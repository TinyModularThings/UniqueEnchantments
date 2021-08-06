package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class Vitae extends UniqueEnchantment
{
	public static final UUID HEALTH_MOD_GENERAL = UUID.fromString("cacfc3c0-bb1d-402e-9509-e961af0776c5");
	public static final UUID HEALTH_MOD_HEAD = UUID.fromString("11908392-e901-4578-9913-af800f6c4b8d");
	public static final UUID HEALTH_MOD_CHEST = UUID.fromString("72a87567-c59a-42a8-90e7-1ce787d42ad7");
	public static final UUID HEALTH_MOD_LEGS = UUID.fromString("aa0db628-1082-42dc-8f97-bb935ed325c2");
	public static final UUID HEALTH_MOD_FEET = UUID.fromString("1ae23c8c-1b59-4bcc-85c3-e5aa5ce75fe1");
	public static final DoubleStat HEALTH_BOOST = new DoubleStat(1D, "health_boost");
	
	public Vitae()
	{
		super(new DefaultData("vitae", Rarity.RARE, 5, false, 18, 8, 5), EnumEnchantmentType.ARMOR, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET);
		addStats(HEALTH_BOOST);
	}
	
	public static UUID getForSlot(EntityEquipmentSlot slot)
	{
		switch(slot)
		{
			case HEAD: return HEALTH_MOD_HEAD;
			case CHEST: return HEALTH_MOD_CHEST;
			case LEGS: return HEALTH_MOD_LEGS;
			case FEET: return HEALTH_MOD_FEET;
			default: return HEALTH_MOD_GENERAL;
		}
	}
}