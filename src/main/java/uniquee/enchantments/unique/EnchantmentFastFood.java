package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.IntLevelStats;

public class EnchantmentFastFood extends UniqueEnchantment
{
	public static final IntLevelStats NURISHMENT = new IntLevelStats("nourishment", 1, 2);
	public static double SATURATION = 2D;

	public EnchantmentFastFood()
	{
		super(new DefaultData("fastfood", Rarity.RARE, 2, true, 14, 6, 10), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	public void loadData(Configuration config)
	{
		NURISHMENT.handleConfig(config, getConfigName());
		SATURATION = config.get(getConfigName(), "saturation_scalar", 2D).getDouble();
	}
}
