package uniquee.enchantments.unique;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLootBonus;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentFastFood extends UniqueEnchantment
{
	public static int SCALAR = 1;

	public EnchantmentFastFood()
	{
		super("fastfood", Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 2;
	}
	
	@Override
	public int getMinEnchantability(int enchantmentLevel)
	{
		return 8 + (enchantmentLevel * 6);
	}
	
	@Override
	public int getMaxEnchantability(int enchantmentLevel)
	{
		return getMinEnchantability(enchantmentLevel) + 10;
	}
	
	@Override
	public boolean isTreasureEnchantment()
	{
		return true;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentLootBonus ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 1).getInt();
	}
}
