package uniqueeutils.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class Adept extends UniqueEnchantment
{
	public static final DoubleStat SPEED_SCALE = new DoubleStat(1.0D, "speed_scale");
	
	public Adept()
	{
		super(new DefaultData("adept", Rarity.RARE, 3, false, false, 20, 10, 10), EnchantmentCategory.ARMOR_HEAD, EquipmentSlot.HEAD);
		setCategory("utils");
		addStats(SPEED_SCALE);
	}
}