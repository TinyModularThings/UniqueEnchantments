package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;

public class TreasurersEyes extends UniqueEnchantment
{
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("range", 5D, 9D);
	
	public TreasurersEyes()
	{
		super(new DefaultData("treasurers_eyes", Rarity.RARE, 1, false, 28, 6, 10), EnchantmentType.ARMOR_HEAD, new EquipmentSlotType[]{EquipmentSlotType.HEAD});
	}
		
	@Override
	public void loadData(Builder config)
	{
		RANGE.handleConfig(config);
	}
	
	
}
