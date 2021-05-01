package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IGraceEnchantment;

public class EnchantmentWarriorsGrace extends UniqueEnchantment implements IGraceEnchantment
{
	public static double DURABILITY_GAIN = 1.1D;

	public EnchantmentWarriorsGrace()
	{
		super(new DefaultData("warriorsgrace", Rarity.VERY_RARE, 1, true, 22, 2, 30), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.ECOLOGICAL, UniqueEnchantments.ALCHEMISTS_GRACE, UniqueEnchantments.NATURES_GRACE, Enchantments.MENDING, Enchantments.UNBREAKING);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		DURABILITY_GAIN = config.get(getConfigName(), "durability_gain", 1.1D).getDouble();
	}
}
