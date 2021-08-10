package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class SwiftBlade extends UniqueEnchantment
{
	public static DoubleStat SCALAR = new DoubleStat(1.2D, "scalar");

	public SwiftBlade()
	{
		super(new DefaultData("swiftblade", Rarity.VERY_RARE, 1, false, 26, 0, 30), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.BERSERKER);
	}

	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
	}
}
