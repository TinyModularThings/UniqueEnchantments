package uniquee.enchantments.unique;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentMending;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentWarriorsGrace extends UniqueEnchantment
{
	public static double SCALAR = 1D;

	public EnchantmentWarriorsGrace()
	{
		super(new DefaultData("warriorsgrace", Rarity.VERY_RARE, true, 22, 2, 30), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 1;
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentDurability || ench instanceof EnchantmentMending || ench instanceof EnchantmentAlchemistsGrace || ench instanceof EnchantmentNaturesGrace || ench instanceof EnchantmentEcological ? false : super.canApplyTogether(ench);
	}

	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 1D).getDouble();
	}
}
