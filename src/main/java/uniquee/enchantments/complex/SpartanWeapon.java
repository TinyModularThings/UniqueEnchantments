
package uniquee.enchantments.complex;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class SpartanWeapon extends UniqueEnchantment
{
	public static final DoubleStat EXTRA_DAMAGE = new DoubleStat(0.075D, "scalar");

	public SpartanWeapon()
	{
		super(new DefaultData("spartan_weapon", Rarity.UNCOMMON, 5, true, false, 25, 3, 50), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
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
		addIncompats(UE.BERSERKER, UE.PERPETUAL_STRIKE);
	}
}
