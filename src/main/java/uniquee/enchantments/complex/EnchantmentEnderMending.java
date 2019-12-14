package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentEnderMending extends UniqueEnchantment
{
	public static final String ENDER_TAG = "ender_mending";
	public static double SCALAR_VALUE = 0.25D;
	public static double SCALAR_LEVEL = 0.25D;
	public static int LIMIT = 250;
	
	public EnchantmentEnderMending()
	{
		super(new DefaultData("ender_mending", Rarity.VERY_RARE, true, 26, 8, 5), EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.values());
	}

	@Override
	public void loadData(Configuration config)
	{
		SCALAR_LEVEL = config.get(getConfigName(), "scalar_level", 0.25D).getDouble();
		SCALAR_VALUE = config.get(getConfigName(), "scalar_value", 0.25D).getDouble();
		LIMIT = config.get(getConfigName(), "limit", 250).getInt();
	}
	
}
