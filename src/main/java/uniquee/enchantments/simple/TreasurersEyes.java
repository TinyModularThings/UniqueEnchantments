package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;

public class TreasurersEyes extends UniqueEnchantment
{
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("range", 5D, 5.5D);
	
	public TreasurersEyes()
	{
		super(new DefaultData("treasurers_eyes", Rarity.RARE, 3, true, 22, 6, 75), EnumEnchantmentType.ARMOR_HEAD, new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD});
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.ENDER_EYES);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		RANGE.handleConfig(config, getConfigName());
	}
	
}
