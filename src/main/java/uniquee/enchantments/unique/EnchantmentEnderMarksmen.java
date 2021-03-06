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
	public static double SCALAR = 2D;
	
	public EnchantmentEnderMarksmen()
	{
		super(new DefaultData("endermarksmen", Rarity.VERY_RARE, 1, true, 28, 2, 16), EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
			
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentMending || ench instanceof EnchantmentArrowInfinite || ench instanceof EnchantmentEcological ? false : super.canApplyTogether(ench);
	}

	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 2D).getDouble();
	}
}
