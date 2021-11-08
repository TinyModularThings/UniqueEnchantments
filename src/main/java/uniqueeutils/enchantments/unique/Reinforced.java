package uniqueeutils.enchantments.unique;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class Reinforced extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("2db7b6c3-38d6-4fb2-8ef1-6292254949ef");
	public static final String SHIELD = "shield";
	public static final DoubleStat FLAT_REPAIR = new DoubleStat(1D, "repair_flat");
	public static final DoubleStat MUL_REPAIR = new DoubleStat(1D, "repair_multiplier");
	public static final IntStat BASE_DURATION = new IntStat(3600, "base_duration");
	public static final DoubleStat BASE_REDUCTION = new DoubleStat(0.95, "base_reduction");
	
	public Reinforced()
	{
		super(new DefaultData("reinforced", Rarity.RARE, 10, true, 10, 6, 75), EnchantmentType.BREAKABLE, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(FLAT_REPAIR, MUL_REPAIR, BASE_DURATION, BASE_REDUCTION);
		setCategory("utils");
	}
	
}
