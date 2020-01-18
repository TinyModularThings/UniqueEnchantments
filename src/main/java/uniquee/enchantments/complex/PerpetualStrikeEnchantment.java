package uniquee.enchantments.complex;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class PerpetualStrikeEnchantment extends UniqueEnchantment
{
	public static DoubleStat SCALAR = new DoubleStat(0.025D, "scalar");
	public static final String HIT_COUNT = "strikes";
	public static final String HIT_ID = "hit_id";
	
	public PerpetualStrikeEnchantment()
	{
		super(new DefaultData("perpetualstrike", Rarity.VERY_RARE, false, 26, 2, 30), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 3;
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof SpartanWeaponEnchantment ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
	}
}
