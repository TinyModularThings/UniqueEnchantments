package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;
import uniquee.utils.DoubleStat;

public class AresBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	public static final DoubleStat SCALAR = new DoubleStat(6D, "scalar");
	
	public AresBlessing()
	{
		super(new DefaultData("aresblessing", Rarity.VERY_RARE, 3, true, 28, 2, 45), EnumEnchantmentType.ARMOR_CHEST, new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR.handleConfig(config, getConfigName());
	}
}
