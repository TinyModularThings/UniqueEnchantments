package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;

public class EnchantmentTreasurersEyes extends UniqueEnchantment
{
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("range", 5D, 9D);
	
	public EnchantmentTreasurersEyes()
	{
		super(new DefaultData("treasurers_eyes", Rarity.RARE, 1, false, 28, 6, 10), EnumEnchantmentType.ARMOR_HEAD, new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD});
	}

	@Override
	public void loadData(Configuration config)
	{
		RANGE.handleConfig(config, getConfigName());
	}
	
}
