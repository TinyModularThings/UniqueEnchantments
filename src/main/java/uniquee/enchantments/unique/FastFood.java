package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntLevelStats;

public class FastFood extends UniqueEnchantment
{
	public static final IntLevelStats NURISHMENT = new IntLevelStats("nourishment", 1, 2);
	public static final DoubleStat SATURATION = new DoubleStat(2D, "saturation");
	public static final DoubleStat TRANSCENDED_STORAGE = new DoubleStat(900, "transcended_storage");
	public static final String FASTFOOD = "fast_food";

	public FastFood()
	{
		super(new DefaultData("fastfood", Rarity.RARE, 2, true, true, 14, 6, 10), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(NURISHMENT, SATURATION, TRANSCENDED_STORAGE);
	}
}