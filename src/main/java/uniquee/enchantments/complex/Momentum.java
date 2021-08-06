package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class Momentum extends UniqueEnchantment
{
	public static final String LAST_MINE = "last_mined";
	public static final String COUNT = "mined";
	public static final DoubleStat SPEED = new DoubleStat(0.575D, "mining_speed");
	public static final IntStat CAP = new IntStat(3, "block_cap");
	public static final IntStat MAX_DELAY = new IntStat(40, "max_delay");
	
	public Momentum()
	{
		super(new DefaultData("momentum", Rarity.RARE, 3, true, 10, 5, 75), EnumEnchantmentType.DIGGER, EntityEquipmentSlot.MAINHAND);
		addStats(SPEED, CAP, MAX_DELAY);
	}
}
