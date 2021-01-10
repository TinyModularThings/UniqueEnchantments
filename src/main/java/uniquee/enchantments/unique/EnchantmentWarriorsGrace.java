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
import uniquee.enchantments.type.IGraceEnchantment;

public class EnchantmentWarriorsGrace extends UniqueEnchantment implements IGraceEnchantment
{
	public static double DURABILITY_GAIN = 1.1D;

	public EnchantmentWarriorsGrace()
	{
		super(new DefaultData("warriorsgrace", Rarity.VERY_RARE, 1, true, 22, 2, 30), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
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
		DURABILITY_GAIN = config.get(getConfigName(), "durability_gain", 1.1D).getDouble();
	}
}
