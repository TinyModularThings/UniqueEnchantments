package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class EnchantmentSwiftBlade extends UniqueEnchantment
{
	public static final DoubleStat SCALAR = new DoubleStat(1.2D, "scalar");

	public EnchantmentSwiftBlade()
	{
		super(new DefaultData("swiftblade", Rarity.VERY_RARE, 1, false, 26, 0, 30), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.BERSERKER);
	}
	
	@Override
	public void loadData(Configuration entry)
	{
		SCALAR.handleConfig(entry, getConfigName());
	}
}
