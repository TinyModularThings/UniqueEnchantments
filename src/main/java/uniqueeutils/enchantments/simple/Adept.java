package uniqueeutils.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class Adept extends UniqueEnchantment
{
	public static final DoubleStat SPEED_SCALE = new DoubleStat(1.0D, "speed_scale");
	
	public Adept()
	{
		super(new DefaultData("adept", Rarity.RARE, 3, false, 20, 10, 10), EnchantmentType.ARMOR_HEAD, EquipmentSlotType.HEAD);
		setCategory("utils");
		addStats(SPEED_SCALE);
	}
}