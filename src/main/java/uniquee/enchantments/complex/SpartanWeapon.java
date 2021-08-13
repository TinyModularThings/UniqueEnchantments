
package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class SpartanWeapon extends UniqueEnchantment
{
	public static final DoubleStat EXTRA_DAMAGE = new DoubleStat(0.075D, "scalar");

	public SpartanWeapon()
	{
		super(new DefaultData("spartanweapon", Rarity.UNCOMMON, 5, true, 25, 3, 50), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(EXTRA_DAMAGE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.BERSERKER, UniqueEnchantments.PERPETUAL_STRIKE);
	}
}
