package uniquee.enchantments.curse;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;
import uniquee.utils.IntStat;

public class PestilencesOdium extends UniqueEnchantment
{
	public static DoubleStat RADIUS = new DoubleStat(7, "radius");
	public static IntStat DELAY = new IntStat(300, "delay");
	public static DoubleStat DAMAGE_PER_TICK = new DoubleStat(0.25F, "damage_per_tick");
	
	public PestilencesOdium()
	{
		super(new DefaultData("pestilences_odium", Rarity.RARE, 1, false, 10, 4, 40), EnchantmentType.ALL, EquipmentSlotType.values());
	}
	
	@Override
	public boolean isCurse()
	{
		return true;
	}
	
	@Override
	public void loadData(Builder config)
	{
		RADIUS.handleConfig(config);
		DELAY.handleConfig(config);
		DAMAGE_PER_TICK.handleConfig(config);
	}
	
}
