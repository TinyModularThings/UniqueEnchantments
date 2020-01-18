
package uniquee.enchantments.complex;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.simple.BerserkEnchantment;
import uniquee.utils.DoubleStat;

public class SpartanWeaponEnchantment extends UniqueEnchantment
{
	public static DoubleStat SCALAR = new DoubleStat(0.1D, "scalar");

	public SpartanWeaponEnchantment()
	{
		super(new DefaultData("spartanweapon", Rarity.RARE, true, 11, 3, 10), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem;
	}
	
	@Override
	public int getMaxLevel()
	{
		return 5;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof BerserkEnchantment || ench instanceof PerpetualStrikeEnchantment ? false : super.canApplyTogether(ench);
	}

	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
	}
}
