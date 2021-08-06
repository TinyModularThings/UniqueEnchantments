package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.api.filters.IGraceEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class WarriorsGrace extends UniqueEnchantment implements IGraceEnchantment
{
	public static final DoubleStat DURABILITY_GAIN = new DoubleStat(1.1D, "durability_gain");

	public WarriorsGrace()
	{
		super(new DefaultData("warriorsgrace", Rarity.RARE, 1, true, 22, 2, 5), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND);
		addStats(DURABILITY_GAIN);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.ECOLOGICAL, UniqueEnchantments.ALCHEMISTS_GRACE, UniqueEnchantments.NATURES_GRACE, Enchantments.MENDING, Enchantments.UNBREAKING);
	}
}