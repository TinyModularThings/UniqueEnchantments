
package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class SpartanWeapon extends UniqueEnchantment
{
	public static final DoubleStat EXTRA_DAMAGE = new DoubleStat(0.05D, "damage_multiplier");

	public SpartanWeapon()
	{
		super(new DefaultData("spartanweapon", Rarity.UNCOMMON, 5, true, 25, 3, 50), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		addStats(EXTRA_DAMAGE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.BERSERKER, UniqueEnchantments.PERPETUAL_STRIKE);
	}
}