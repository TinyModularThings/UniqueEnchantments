package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class AresBlessing extends UniqueEnchantment
{
	public static final DoubleStat BASE_DAMAGE = new DoubleStat(6D, "base_armor_damage");
	
	public AresBlessing()
	{
		super(new DefaultData("aresblessing", Rarity.VERY_RARE, 3, true, 28, 2, 45), EnumEnchantmentType.ARMOR_CHEST, EntityEquipmentSlot.CHEST);
		addStats(BASE_DAMAGE);
	}
}