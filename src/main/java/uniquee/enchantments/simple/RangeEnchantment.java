package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;

public class RangeEnchantment extends UniqueEnchantment
{
	public static final UUID RANGE_MOD = UUID.fromString("3b35b821-d4d7-4aa3-8c64-e9849f43516a");
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("range", 1D, 0.25D);
	public static final DoubleLevelStats REDUCTION = new DoubleLevelStats("reduction", 0.2D, 0.4D);
	
	public RangeEnchantment()
	{
		super(new DefaultData("ranged", Rarity.COMMON, 4, false, 12, 4, 75), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
		
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return EnchantmentType.DIGGER.canEnchantItem(stack.getItem());
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(Enchantments.SWEEPING);
	}
	
	@Override
	public void loadData(Builder config)
	{
		RANGE.handleConfig(config);
		REDUCTION.handleConfig(config);
	}
}
