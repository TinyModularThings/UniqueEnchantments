package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentSwift extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("df795085-a576-4035-abaf-02f6ecd4a2c7");
	public static double SCALAR = 0.05D;
	
	public EnchantmentSwift()
	{
		super("swift", Rarity.RARE, EnumEnchantmentType.ARMOR_LEGS, new EntityEquipmentSlot[]{EntityEquipmentSlot.LEGS});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 2;
	}
	
	@Override
	public int getMinEnchantability(int enchantmentLevel)
	{
		return 10 + (4 * enchantmentLevel);
	}
	
	@Override
	public int getMaxEnchantability(int enchantmentLevel)
	{
		return getMinEnchantability(enchantmentLevel) + 18;
	}

	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 0.05D).getDouble();
	}
}
