package uniqueebattle.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.IntLevelStats;

public class Snare extends UniqueEnchantment
{
	public static final IntLevelStats DURATION = new IntLevelStats("duration", 30, 50);
	
	public Snare()
	{
		super(new DefaultData("snare", Rarity.UNCOMMON, 3, false, true, 20, 5, 75), EnchantmentCategory.TRIDENT, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(DURATION);
		setCategory("battle");
	}
}
