package uniquee.enchantments.curse;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentPestilencesOdium extends UniqueEnchantment
{
	public static double RADIUS = 7;
	public static int DELAY = 300;
	public static double DAMAGE_PER_TICK = 0.25F;
	
	public EnchantmentPestilencesOdium()
	{
		super(new DefaultData("pestilences_odium", Rarity.UNCOMMON, false, 10, 4, 40), EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
	}
	
	@Override
	public int getMaxLevel()
	{
		return 1;
	}
	
	@Override
	public boolean isCurse()
	{
		return true;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		RADIUS = config.get(getConfigName(), "radius", 7D).getDouble();
		DELAY = config.get(getConfigName(), "delay", 300).getInt();
		DAMAGE_PER_TICK = config.get(getConfigName(), "damage_per_tick", 0.25F).getDouble();
	}
	
}
