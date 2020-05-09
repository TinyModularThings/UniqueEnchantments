package uniqueeutils.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;

public class EnchantmentRocketMan extends UniqueEnchantment
{
	public static final DoubleLevelStats FLIGHT_TIME = new DoubleLevelStats("duration", 0.05D, 0.15D);
			
	public EnchantmentRocketMan()
	{
		super(new DefaultData("rocketman", Rarity.VERY_RARE, true, 25, 4, 20), EnumEnchantmentType.ARMOR_CHEST, new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
		setCategory("utils");
	}
	@Override
	public int getMaxLevel()
	{
		return 3;
	}
	
	@Override
	public boolean isCurse()
	{
		return true;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled && stack.getItem() instanceof ItemElytra;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		FLIGHT_TIME.handleConfig(config, getConfigName());
	}
	
}
