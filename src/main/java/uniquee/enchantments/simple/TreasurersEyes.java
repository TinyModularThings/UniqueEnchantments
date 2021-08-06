package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquee.UniqueEnchantments;

public class TreasurersEyes extends UniqueEnchantment
{
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("range", 5D, 5.5D);
	
	public TreasurersEyes()
	{
		super(new DefaultData("treasurers_eyes", Rarity.RARE, 3, true, 22, 6, 75), EnumEnchantmentType.ARMOR_HEAD, EntityEquipmentSlot.HEAD);
		addStats(RANGE);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.ENDER_EYES);
	}	
}
