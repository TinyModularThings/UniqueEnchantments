package uniquee.enchantments.unique;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntLevelStats;

public class Cloudwalker extends UniqueEnchantment
{
	public static final String TIMER = "cloud";
	public static final String TRIGGER = "cloud_trigger";
	public static final String ENABLED = "cloud_enabled";
	public static final String HEIGHT = "cloud_height";
	public static final IntLevelStats TICKS = new IntLevelStats("duration", 8, 28);
	public static final DoubleStat TRANSCENDED_EXPONENT = new DoubleStat(1.5, "transcended_frequency_exponent");
	
	public Cloudwalker()
	{
		super(new DefaultData("cloud_walker", Rarity.RARE, 4, true, false, 12, 4, 16).setTrancendenceLevel(500), EnchantmentCategory.ARMOR_FEET, EquipmentSlot.FEET);
		addStats(TICKS, TRANSCENDED_EXPONENT);
	}
}
