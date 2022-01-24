package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class NobleImpact extends UniqueEnchantment
{
	public static final DoubleStat SCALE = new DoubleStat(1D, "scale");
	
	public NobleImpact()
	{
		super(new DefaultData("noble_impact", Rarity.COMMON, 2, false, 12, 6, 60), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND);
		addStats(SCALE);
	}
}