package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.IntLevelStats;

public class EnchantmentCloudwalker extends UniqueEnchantment
{
	public static final String TIMER = "cloud";
	public static final String TRIGGER = "cloud_trigger";
	public static final String ENABLED = "cloud_enabled";
	public static final IntLevelStats TICKS = new IntLevelStats("duration", 8, 28);

	public EnchantmentCloudwalker()
	{
		super(new DefaultData("cloudwalker", Rarity.RARE, 4, true, 16, 3, 16), EnumEnchantmentType.ARMOR_FEET, new EntityEquipmentSlot[]{EntityEquipmentSlot.FEET});
	}
	
	@Override
	public void loadData(Configuration config)
	{
		TICKS.handleConfig(config, getConfigName());
	}
}
