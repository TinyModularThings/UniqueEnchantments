package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.api.filters.IBlessingEnchantment;
import uniquebase.utils.DoubleStat;

public class AresBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	public static final DoubleStat BASE_DAMAGE = new DoubleStat(6D, "scalar");
	
	public AresBlessing()
	{
		super(new DefaultData("aresblessing", Rarity.VERY_RARE, 3, true, 28, 2, 45), EnchantmentType.ARMOR_CHEST, new EquipmentSlotType[]{EquipmentSlotType.CHEST});
		addStats(BASE_DAMAGE);
	}
}