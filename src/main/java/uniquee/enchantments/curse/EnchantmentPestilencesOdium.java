package uniquee.enchantments.curse;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;
import uniquee.utils.IntStat;

public class EnchantmentPestilencesOdium extends UniqueEnchantment
{
	public static final DoubleStat RADIUS = new DoubleStat(7, "radius");
	public static final IntStat DELAY = new IntStat(300, "delay");
	public static final DoubleStat DAMAGE_PER_TICK = new DoubleStat(0.25D, "damage_per_tick");
	
	public EnchantmentPestilencesOdium()
	{
		super(new DefaultData("pestilences_odium", Rarity.RARE, 2, false, 10, 4, 40), EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
	}
		
	@Override
	public boolean isCurse()
	{
		return true;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		RADIUS.handleConfig(config, getConfigName());
		DELAY.handleConfig(config, getConfigName());
		DAMAGE_PER_TICK.handleConfig(config, getConfigName());
	}
	
}
