package uniquee.enchantments.unique;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentNaturesGrace extends UniqueEnchantment
{
	public static double SCALAR = 0.5D;

	public EnchantmentNaturesGrace()
	{
		super(new DefaultData("naturesgrace", Rarity.VERY_RARE, true, 22, 4, 22), EnumEnchantmentType.ARMOR_CHEST, new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 1;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentAresBlessing || ench instanceof EnchantmentAlchemistsGrace || ench instanceof EnchantmentWarriorsGrace ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 0.5D).getDouble();
	}
}
