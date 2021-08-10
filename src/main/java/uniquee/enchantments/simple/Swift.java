package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;

public class Swift extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("df795085-a576-4035-abaf-02f6ecd4a2c7");
	public static final DoubleLevelStats SPEED_BONUS = new DoubleLevelStats("speed_bonus", 0.03D, 0.06D);
	
	public Swift()
	{
		super(new DefaultData("swift", Rarity.RARE, 2, false, 14, 4, 18), EnchantmentType.ARMOR_LEGS, new EquipmentSlotType[]{EquipmentSlotType.LEGS});
	}
	
	@Override
	public void loadData(Builder config)
	{
		SPEED_BONUS.handleConfig(config);
	}
}
