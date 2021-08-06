package uniqueeutils.enchantments.complex;

import java.util.UUID;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.IntStat;

public class SleipnirsGrace extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("16aea480-f9a0-4409-8a14-416ba3382d31");
	public static final String HORSE_NBT = "timetracker";
	public static final DoubleLevelStats CAP = new DoubleLevelStats("cap", 1D, 2D);
	public static final IntStat LIMITER = new IntStat(16, "limiter");
	
	public SleipnirsGrace()
	{
		super(new DefaultData("sleipnirs_grace", Rarity.UNCOMMON, 3, true, 20, 3, 40), EnumEnchantmentType.ARMOR_CHEST);
		setCategory("utils");
		addStats(CAP, LIMITER);
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled && HorseArmorType.isHorseArmor(stack);
	}
}