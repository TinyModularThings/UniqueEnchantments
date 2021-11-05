package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class WarriorsGrace extends UniqueEnchantment
{
	public static final DoubleStat DURABILITY_GAIN = new DoubleStat(1.1D, "durability_gain");

	public WarriorsGrace()
	{
		super(new DefaultData("warriorsgrace", Rarity.VERY_RARE, 1, true, false, 22, 2, 5), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(DURABILITY_GAIN);
	}
		
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.ECOLOGICAL, UniqueEnchantments.ALCHEMISTS_GRACE, UniqueEnchantments.NATURES_GRACE, Enchantments.MENDING, Enchantments.UNBREAKING);
	}	
}
