package uniquee.enchantments.unique;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class WarriorsGrace extends UniqueEnchantment
{
	public static final DoubleStat DURABILITY_GAIN = new DoubleStat(1.1D, "durability_gain");

	public WarriorsGrace()
	{
		super(new DefaultData("warriors_grace", Rarity.VERY_RARE, 1, true, false, 22, 2, 5), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);
		addStats(DURABILITY_GAIN);
	}
		
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof TridentItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.ECOLOGICAL, UE.ALCHEMISTS_GRACE, UE.NATURES_GRACE, Enchantments.MENDING, Enchantments.UNBREAKING);
	}	
}
