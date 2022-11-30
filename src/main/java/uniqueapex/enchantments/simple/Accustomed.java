package uniqueapex.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import uniqueapex.enchantments.ApexEnchantment;
import uniquebase.api.BaseUEMod;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class Accustomed extends ApexEnchantment
{
	public static final String GAGUE_COUNTER = "gauge_counter";
	public static final DoubleStat SCALE = new DoubleStat(1.0, "level_scale");
	public static final IntStat GAUGE = new IntStat(500, "gauge_requirement");
	
	public Accustomed()
	{
		super("accustomed", BaseUEMod.ALL_TYPES, EquipmentSlot.values());
		setCategory("apex");
		addStats(SCALE, GAUGE);
	}
}
