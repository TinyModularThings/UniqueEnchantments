package uniqueeutils.enchantments.curse;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class PhanesRegret extends UniqueEnchantment
{
	public static final DoubleStat REDUCTION = new DoubleStat(0.125D, "reduction");

	public PhanesRegret()
	{
		super(new DefaultData("phanes_regret", Rarity.UNCOMMON, 2, true, 10, 2, 75), EnchantmentType.ALL, EquipmentSlotType.values());
		setCategory("utils");
		addStats(REDUCTION);
		setCurse();
	}
}