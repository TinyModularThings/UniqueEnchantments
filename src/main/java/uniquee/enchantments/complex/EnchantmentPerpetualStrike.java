package uniquee.enchantments.complex;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentPerpetualStrike extends UniqueEnchantment
{
	public static double SCALAR = 0.025D;

	public EnchantmentPerpetualStrike()
	{
		super("perpetualstrike", Rarity.VERY_RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 3;
	}
	
	@Override
	public int getMinEnchantability(int enchantmentLevel)
	{
		return 24 * (enchantmentLevel * 2);
	}
	
	@Override
	public int getMaxEnchantability(int enchantmentLevel)
	{
		return getMinEnchantability(enchantmentLevel) + 30;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentSpartanWeapon ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Configuration entry)
	{
		SCALAR = entry.get(getConfigName(), "scalar", 0.025D).getDouble();
	}
}
