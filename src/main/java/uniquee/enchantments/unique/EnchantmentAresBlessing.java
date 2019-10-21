package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentAresBlessing extends UniqueEnchantment
{
	public static double SCALAR = 2D;
	public EnchantmentAresBlessing()
	{
		super("aresblessing", Rarity.VERY_RARE, EnumEnchantmentType.ARMOR_CHEST, new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 1;
	}
	
	@Override
	public int getMinEnchantability(int enchantmentLevel)
	{
		return 26 + (enchantmentLevel * 2);
	}
	
	@Override
	public int getMaxEnchantability(int enchantmentLevel)
	{
		return getMinEnchantability(enchantmentLevel) + 32;
	}
	
	@Override
	public boolean isTreasureEnchantment()
	{
		return true;
	}

	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 2D).getDouble();
	}
}
