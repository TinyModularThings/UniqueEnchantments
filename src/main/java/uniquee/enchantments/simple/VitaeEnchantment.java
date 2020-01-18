package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class VitaeEnchantment extends UniqueEnchantment
{
	public static final UUID HEALTH_MOD_GENERAL = UUID.fromString("cacfc3c0-bb1d-402e-9509-e961af0776c5");
	public static final UUID HEALTH_MOD_HEAD = UUID.fromString("11908392-e901-4578-9913-af800f6c4b8d");
	public static final UUID HEALTH_MOD_CHEST = UUID.fromString("72a87567-c59a-42a8-90e7-1ce787d42ad7");
	public static final UUID HEALTH_MOD_LEGS = UUID.fromString("aa0db628-1082-42dc-8f97-bb935ed325c2");
	public static final UUID HEALTH_MOD_FEET = UUID.fromString("1ae23c8c-1b59-4bcc-85c3-e5aa5ce75fe1");
	public static final DoubleStat HEALTH_BOOST = new DoubleStat(1D, "health_boost");
	
	public VitaeEnchantment()
	{
		super(new DefaultData("vitae", Rarity.VERY_RARE, false, 10, 2, 7), EnchantmentType.ARMOR, new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 5;
	}
	
	public static UUID getForSlot(EquipmentSlotType slot)
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
	
	@Override
	public void loadData(Builder config)
	{
		HEALTH_BOOST.handleConfig(config);
	}
	
}
