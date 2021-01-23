package uniquee.enchantments.unique;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.InfinityEnchantment;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.complex.EnderMendingEnchantment;
import uniquee.utils.DoubleStat;

public class EnderMarksmenEnchantment extends UniqueEnchantment
{
	public static DoubleStat SCALAR = new DoubleStat(2D, "scalar");
	
	public EnderMarksmenEnchantment()
	{
		super(new DefaultData("endermarksmen", Rarity.VERY_RARE, 1, true, 28, 2, 16), EnchantmentType.BOW, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
			
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof MendingEnchantment || ench instanceof EnderMendingEnchantment || ench instanceof InfinityEnchantment || ench instanceof EcologicalEnchantment ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
	}
}
