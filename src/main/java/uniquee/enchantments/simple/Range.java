package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;

public class Range extends UniqueEnchantment
{
	public static final UUID RANGE_MOD = UUID.fromString("3b35b821-d4d7-4aa3-8c64-e9849f43516a");
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("range", 0.8D, 0.3D);
	public static final DoubleLevelStats REDUCTION = new DoubleLevelStats("reduction", 1D, 2D);
	
	public Range()
	{
		super(new DefaultData("ranged", Rarity.COMMON, 4, false, 12, 8, 75), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return EnumEnchantmentType.DIGGER.canEnchantItem(stack.getItem());
	}
	
	@Override
	public void loadData(Configuration config)
	{
		RANGE.handleConfig(config, getConfigName());
		REDUCTION.handleConfig(config, getConfigName());
	}
}
