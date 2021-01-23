package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.IntLevelStats;

public class CloudwalkerEnchantment extends UniqueEnchantment
{
	public static final String TIMER = "cloud";
	public static final String TRIGGER = "cloud_trigger";
	public static final String ENABLED = "cloud_enabled";
	public static final IntLevelStats TICKS = new IntLevelStats("duration", 8, 28);
	
	public CloudwalkerEnchantment()
	{
		super(new DefaultData("cloudwalker", Rarity.RARE, 4, true, 16, 3, 16), EnchantmentType.ARMOR_FEET, new EquipmentSlotType[]{EquipmentSlotType.FEET});
	}
	
	@Override
	public void loadData(Builder config)
	{
		TICKS.handleConfig(config);
	}
}
