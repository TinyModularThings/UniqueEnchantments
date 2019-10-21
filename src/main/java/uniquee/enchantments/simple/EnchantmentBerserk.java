package uniquee.enchantments.simple;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.complex.EnchantmentSpartanWeapon;
import uniquee.enchantments.complex.EnchantmentSwiftBlade;

public class EnchantmentBerserk extends UniqueEnchantment
{
	public static double SCALAR = 0.125D;
	
	public EnchantmentBerserk()
	{
		super("berserk", Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 1;
	}
	
	@Override
	public boolean canApply(ItemStack stack)
	{
        return stack.getItem() instanceof ItemAxe ? true : super.canApply(stack);
	}
	
	@Override
	public int getMinEnchantability(int enchantmentLevel)
	{
		return 18;
	}
	
	@Override
	public int getMaxEnchantability(int enchantmentLevel)
	{
		return 40;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentSwiftBlade || ench instanceof EnchantmentSpartanWeapon ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 0.125D).getDouble();
	}
}
