package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.IntLevelStats;

public class EnchantmentCloudwalker extends UniqueEnchantment
{
	public static final String TIMER = "cloud";
	public static final IntLevelStats TICKS = new IntLevelStats("duration", 8, 28);

	public EnchantmentCloudwalker()
	{
		super(new DefaultData("cloudwalker", Rarity.RARE, true, 16, 3, 16), EnchantmentType.ARMOR_FEET, new EquipmentSlotType[]{EquipmentSlotType.FEET});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 4;
	}
	
	@Override
	public void loadData(Builder config)
	{
		TICKS.handleConfig(config);
	}
}
