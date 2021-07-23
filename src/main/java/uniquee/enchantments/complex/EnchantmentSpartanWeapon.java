
package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class EnchantmentSpartanWeapon extends UniqueEnchantment
{
	public static final DoubleStat SCALAR = new DoubleStat(0.1D, "scalar");

	public EnchantmentSpartanWeapon()
	{
		super(new DefaultData("spartanweapon", Rarity.RARE, 5, true, 11, 3, 10), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.BERSERKER, UniqueEnchantments.PERPETUAL_STRIKE);
	}
	
	@Override
	public void loadData(Configuration entry)
	{
		SCALAR.handleConfig(entry, getConfigName());
	}
}
