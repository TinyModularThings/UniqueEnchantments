package uniquee.enchantments.complex;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.simple.EnchantmentBerserk;

public class EnchantmentSwiftBlade extends UniqueEnchantment
{
	public static double SCALAR = 1.1D;

	public EnchantmentSwiftBlade()
	{
		super("swiftblade", Rarity.VERY_RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
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
		return 56;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentBerserk ? false : super.canApplyTogether(ench);
	}

	@Override
	public void loadData(Configuration entry)
	{
		SCALAR = entry.get(getConfigName(), "scalar", 1.1D).getDouble();
	}
}
