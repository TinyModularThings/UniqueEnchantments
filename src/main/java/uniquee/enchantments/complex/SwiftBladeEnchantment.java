package uniquee.enchantments.complex;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.simple.BerserkEnchantment;
import uniquee.utils.DoubleStat;

public class SwiftBladeEnchantment extends UniqueEnchantment
{
	public static DoubleStat SCALAR = new DoubleStat(1.2D, "scalar");

	public SwiftBladeEnchantment()
	{
		super(new DefaultData("swiftblade", Rarity.VERY_RARE, 1, false, 26, 0, 30), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof BerserkEnchantment ? false : super.canApplyTogether(ench);
	}

	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
	}
}
