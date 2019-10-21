
package uniquee.enchantments.complex;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.simple.EnchantmentBerserk;

public class EnchantmentSpartanWeapon extends UniqueEnchantment
{
	public static double SCALAR = 0.1D;

	public EnchantmentSpartanWeapon()
	{
		super("spartanweapon", Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	public boolean canApply(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe ? true : super.canApply(stack);
	}
	
	@Override
	public int getMaxLevel()
	{
		return 5;
	}
	
	@Override
	public boolean isTreasureEnchantment()
	{
		return true;
	}
	
	@Override
	public int getMinEnchantability(int enchantmentLevel)
	{
		return 8 + (enchantmentLevel * 3);
	}
	
	@Override
	public int getMaxEnchantability(int enchantmentLevel)
	{
		return getMinEnchantability(enchantmentLevel) + 10;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentBerserk || ench instanceof EnchantmentPerpetualStrike ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Configuration entry)
	{
		SCALAR = entry.get(getConfigName(), "scalar", 0.1D).getDouble();
	}
}
