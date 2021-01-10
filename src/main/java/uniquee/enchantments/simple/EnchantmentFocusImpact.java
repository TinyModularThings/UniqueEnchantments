package uniquee.enchantments.simple;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.complex.EnchantmentSwiftBlade;

public class EnchantmentFocusImpact extends UniqueEnchantment
{
	public static double SCALAR = 1.05D;
	
	public EnchantmentFocusImpact()
	{
		super(new DefaultData("focus_impact", Rarity.RARE, 3, false, 20, 2, 17), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentSwiftBlade ? false : super.canApplyTogether(ench);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 1.05D, "Important Info: Turning the Scalar to 0.639 or below will end up Healing the enemy with the Bonus Damage instead of Damaging on LvL 3 for a 1.6 Attack Speed(Default Attack Speed). so keep it above").getDouble();
	}
	
}
