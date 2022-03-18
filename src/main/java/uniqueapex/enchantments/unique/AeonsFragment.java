package uniqueapex.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import uniqueapex.enchantments.ApexEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class AeonsFragment extends ApexEnchantment
{
	public static final IntStat INTERVAL = new IntStat(433, "interval");
	public static final DoubleStat TICK_SCALE = new DoubleStat(1D, "ticks");
	
	public AeonsFragment()
	{
		super("aeons_fragment", EnchantmentType.TRIDENT);
		addStats(INTERVAL, TICK_SCALE);
		setCategory("apex");
	}
	
}
