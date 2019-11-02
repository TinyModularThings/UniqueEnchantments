package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentCloudwalker extends UniqueEnchantment
{
	public static int TICKS = 50;

	public EnchantmentCloudwalker()
	{
		super(new DefaultData("cloudwalker", Rarity.RARE, true, 16, 3, 16), EnumEnchantmentType.ARMOR_FEET, new EntityEquipmentSlot[]{EntityEquipmentSlot.FEET});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 4;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		TICKS = config.get(getConfigName(), "ticks", 50).getInt();
	}
}
