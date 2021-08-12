package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.IntLevelStats;

public class Cloudwalker extends UniqueEnchantment
{
	public static final String TIMER = "cloud";
	public static final String TRIGGER = "cloud_trigger";
	public static final String ENABLED = "cloud_enabled";
	public static final IntLevelStats TICKS = new IntLevelStats("duration", 8, 28);
	
	public Cloudwalker()
	{
		super(new DefaultData("cloudwalker", Rarity.RARE, 4, true, 12, 4, 16), EnchantmentType.ARMOR_FEET, EquipmentSlotType.FEET);
		addStats(TICKS);
	}
}
