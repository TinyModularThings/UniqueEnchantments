package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;

public class TreasurersEyes extends UniqueEnchantment
{
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("range", 5D, 5.5D);
	
	public TreasurersEyes()
	{
		super(new DefaultData("treasurers_eyes", Rarity.RARE, 3, true, 22, 6, 75), EnchantmentType.ARMOR_HEAD, EquipmentSlotType.HEAD);
		addStats(RANGE);
	}
}