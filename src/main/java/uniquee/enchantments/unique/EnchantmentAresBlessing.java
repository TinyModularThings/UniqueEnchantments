package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;

public class EnchantmentAresBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	public static double SCALAR = 2D;
	public EnchantmentAresBlessing()
	{
		super(new DefaultData("aresblessing", Rarity.VERY_RARE, 1, true, 28, 2, 32), EnumEnchantmentType.ARMOR_CHEST, new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 2D).getDouble();
	}
}
