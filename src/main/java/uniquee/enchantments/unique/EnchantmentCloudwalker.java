package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentCloudwalker extends UniqueEnchantment
{
	public static int TICKS = 50;

	public EnchantmentCloudwalker()
	{
		super("cloudwalker", Rarity.RARE, EnumEnchantmentType.ARMOR_FEET, new EntityEquipmentSlot[]{EntityEquipmentSlot.FEET});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 4;
	}
	
	@Override
	public int getMinEnchantability(int enchantmentLevel)
	{
		return 13 + (enchantmentLevel * 3);
	}
	
	@Override
	public int getMaxEnchantability(int enchantmentLevel)
	{
		return getMinEnchantability(enchantmentLevel) + 16;
	}
	
	@Override
	public boolean isTreasureEnchantment()
	{
		return true;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		TICKS = config.get(getConfigName(), "ticks", 50).getInt();
	}
}
