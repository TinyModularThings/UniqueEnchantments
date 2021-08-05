package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;

public class Swift extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("df795085-a576-4035-abaf-02f6ecd4a2c7");
	public static final DoubleLevelStats SPEED_BONUS = new DoubleLevelStats("speed_bonus", 0.07D, 0.04D);
	
	public Swift()
	{
		super(new DefaultData("swift", Rarity.UNCOMMON, 2, false, 14, 12, 10), EnumEnchantmentType.ARMOR_LEGS, new EntityEquipmentSlot[]{EntityEquipmentSlot.LEGS});
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SPEED_BONUS.handleConfig(config, getConfigName());
	}
}
