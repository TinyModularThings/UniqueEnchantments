package uniquee.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;

public class TreasurersEyes extends UniqueEnchantment
{
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("range", 5D, 5.5D);
	
	public TreasurersEyes()
	{
		super(new DefaultData("treasurers_eyes", Rarity.RARE, 3, true, true, 22, 6, 75), EnchantmentCategory.ARMOR_HEAD, EquipmentSlot.HEAD);
		addStats(RANGE);
	}
}