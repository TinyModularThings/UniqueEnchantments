package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;
import uniquee.utils.DoubleStat;

public class AresBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	public static DoubleStat SCALAR = new DoubleStat(6D, "scalar");
	
	public AresBlessing()
	{
		super(new DefaultData("aresblessing", Rarity.VERY_RARE, 1, true, 28, 2, 32), EnchantmentType.ARMOR_CHEST, new EquipmentSlotType[]{EquipmentSlotType.CHEST});
	}
	
	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
	}
}
