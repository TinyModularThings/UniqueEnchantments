package uniquee.enchantments.complex;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLootBonus;
import net.minecraft.enchantment.EnchantmentUntouching;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;
import uniquee.enchantments.unique.EnchantmentMidasBlessing;

public class EnchantmentJokersBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	public static int SCALAR = 8;
	
	public EnchantmentJokersBlessing()
	{
		super(new DefaultData("jokers_blessing", Rarity.VERY_RARE, true, 10, 5, 30), EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}

	@Override
	public int getMaxLevel()
	{
		return 4;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentLootBonus || ench instanceof EnchantmentUntouching || ench instanceof EnchantmentMidasBlessing ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 8).getInt();
	}
	
}
