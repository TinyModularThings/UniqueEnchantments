package uniqueeutils.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;

public class RocketManEnchantment extends UniqueEnchantment
{
	public static final DoubleLevelStats FLIGHT_TIME = new DoubleLevelStats("duration", 0.05D, 0.15D);
			
	public RocketManEnchantment()
	{
		super(new DefaultData("rocketman", Rarity.VERY_RARE, 3, true, 25, 4, 20), EnchantmentType.ARMOR_CHEST, new EquipmentSlotType[]{EquipmentSlotType.CHEST});
		setCategory("utils");
	}
	
	@Override
	public boolean isCurse()
	{
		return true;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled.get() && stack.getItem() instanceof ElytraItem;
	}
	
	@Override
	public void loadData(ForgeConfigSpec.Builder config)
	{
		FLIGHT_TIME.handleConfig(config);
	}
	
}
