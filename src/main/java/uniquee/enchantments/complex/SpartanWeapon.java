
package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class SpartanWeapon extends UniqueEnchantment
{
	public static final DoubleStat EXTRA_DAMAGE = new DoubleStat(0.05D, "scalar");

	public SpartanWeapon()
	{
		super(new DefaultData("spartanweapon", Rarity.UNCOMMON, 5, true, 25, 3, 50), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
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
	
	@Override
	public void loadData(Configuration entry)
	{
		EXTRA_DAMAGE.handleConfig(entry, getConfigName());
	}
}
