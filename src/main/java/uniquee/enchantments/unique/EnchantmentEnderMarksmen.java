package uniquee.enchantments.unique;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentArrowInfinite;
import net.minecraft.enchantment.EnchantmentMending;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentEnderMarksmen extends UniqueEnchantment
{
	public static double SCALAR = 3D;
	
	public EnchantmentEnderMarksmen()
	{
		super("endermarksmen", Rarity.VERY_RARE, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 1;
	}
	
	@Override
	public int getMinEnchantability(int enchantmentLevel)
	{
		return 26;
	}
	
	@Override
	public int getMaxEnchantability(int enchantmentLevel)
	{
		return 42;
	}
	
	@Override
	public boolean isTreasureEnchantment()
	{
		return true;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentMending || ench instanceof EnchantmentArrowInfinite ? false : super.canApplyTogether(ench);
	}

	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 3D).getDouble();
	}
}
