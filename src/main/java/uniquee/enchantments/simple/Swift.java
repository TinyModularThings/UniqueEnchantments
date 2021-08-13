package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;

public class Swift extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("df795085-a576-4035-abaf-02f6ecd4a2c7");
	public static final DoubleLevelStats SPEED_BONUS = new DoubleLevelStats("speed_bonus", 0.07D, 0.04D);
	
	public Swift()
	{
		super(new DefaultData("swift", Rarity.UNCOMMON, 2, false, 14, 12, 10), EnchantmentType.ARMOR_LEGS, EquipmentSlotType.LEGS);
		addStats(SPEED_BONUS);
	}
}
