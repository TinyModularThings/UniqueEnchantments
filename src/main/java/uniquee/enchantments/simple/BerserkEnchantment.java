package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class BerserkEnchantment extends UniqueEnchantment
{
	public static DoubleStat SCALAR = new DoubleStat(0.125D, "scalar");
	
	public BerserkEnchantment()
	{
		super(new DefaultData("berserk", Rarity.RARE, 1, false, 20, 2, 22), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
        return stack.getItem() instanceof AxeItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.SWIFT_BLADE, UniqueEnchantments.SPARTAN_WEAPON);
	}

	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
	}
}
