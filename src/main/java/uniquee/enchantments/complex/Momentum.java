package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class Momentum extends UniqueEnchantment
{
	public static final String LAST_MINE = "last_mined";
	public static final String COUNT = "mined";
	public static final DoubleStat SPEED = new DoubleStat(2D, "mining_speed_flat");
	public static final DoubleStat SPEED_MULTIPLIER = new DoubleStat(0.003D, "mining_speed_multiplier");
	public static final IntStat CAP = new IntStat(41, "block_cap");
	public static final DoubleStat CAP_MULTIPLIER = new DoubleStat(1D, "block_cap_multiplier");	
	public static final IntStat MAX_DELAY = new IntStat(200, "max_delay");
	
	public Momentum()
	{
		super(new DefaultData("momentum", Rarity.RARE, 3, true, 10, 5, 75), EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND);
		addStats(SPEED, SPEED_MULTIPLIER, CAP, CAP_MULTIPLIER, MAX_DELAY);
	}	
}
