package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;
import uniquee.utils.DoubleStat;

public class AresBlessingEnchantment extends UniqueEnchantment implements IBlessingEnchantment
{
	public static DoubleStat SCALAR = new DoubleStat(2D, "scalar");
	public AresBlessingEnchantment()
	{
		super(new DefaultData("aresblessing", Rarity.VERY_RARE, true, 28, 2, 32), EnchantmentType.ARMOR_CHEST, new EquipmentSlotType[]{EquipmentSlotType.CHEST});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 1;
	}
	
	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
	}
}
