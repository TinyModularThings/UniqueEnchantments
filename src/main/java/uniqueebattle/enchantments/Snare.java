package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.IntLevelStats;

public class Snare extends UniqueEnchantment
{
	public static final IntLevelStats DURATION = new IntLevelStats("duration", 30, 50);
	
	public Snare()
	{
		super(new DefaultData("snare", Rarity.UNCOMMON, 3, false, true, 20, 5, 75), EnchantmentType.TRIDENT, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(DURATION);
		setCategory("battle");
	}
}
