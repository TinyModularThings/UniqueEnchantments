package uniqueeutils.enchantments;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;
import uniquee.utils.IntStat;

public class SleipnirsGraceEnchantment extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("16aea480-f9a0-4409-8a14-416ba3382d31");
	public static final String HORSE_NBT = "timetracker";
	public static final DoubleLevelStats CAP = new DoubleLevelStats("cap", 1D, 2D);
	public static final IntStat LIMITER = new IntStat(16, "limiter");
	
	public SleipnirsGraceEnchantment()
	{
		super(new DefaultData("sleipnirs_grace", Rarity.UNCOMMON, 3, true, 20, 3, 0), EnchantmentType.ARMOR_CHEST, new EquipmentSlotType[0]);
		setCategory("utils");
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled.get() && stack.getItem() instanceof HorseArmorItem;
	}
	
	@Override
	public void loadData(ForgeConfigSpec.Builder config)
	{
		CAP.handleConfig(config, getConfigName());
		LIMITER.handleConfig(config);
	}
	
}
