package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentSweepingEdge;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentRange extends UniqueEnchantment
{
	public static final UUID RANGE_MOD = UUID.fromString("3b35b821-d4d7-4aa3-8c64-e9849f43516a");
	public static double RANGE_VALUE = 1D;
	public static double RANGE_LEVEL = 0.25D;
	public static double REDUCTION_VALUE = 0.2D;
	public static double REDUCTION_LEVEL = 0.4D;
	
	public EnchantmentRange()
	{
		super(new DefaultData("ranged", Rarity.COMMON, false, 12, 4, 75), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 4;
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return EnumEnchantmentType.DIGGER.canEnchantItem(stack.getItem());
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentSweepingEdge ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		RANGE_VALUE = config.get(getConfigName(), "range_value", 1D).getDouble();
		RANGE_LEVEL = config.get(getConfigName(), "range_level", 0.25D).getDouble();
		REDUCTION_VALUE = config.get(getConfigName(), "reduction_value", 0.2D).getDouble();
		REDUCTION_LEVEL = config.get(getConfigName(), "reduction_level", 0.4D).getDouble();
	}
	
}
