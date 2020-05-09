package uniqueeutils.enchantments;

import java.util.UUID;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;

public class EnchantmentSleipnirsGrace extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("16aea480-f9a0-4409-8a14-416ba3382d31");
	public static final String HORSE_NBT = "timetracker";
	public static final DoubleLevelStats CAP = new DoubleLevelStats("cap", 1D, 2D);
	public static int LIMITER = 16;
	
	public EnchantmentSleipnirsGrace()
	{
		super(new DefaultData("sleipnirs_grace", Rarity.UNCOMMON, true, 20, 3, 0), EnumEnchantmentType.ARMOR_CHEST, new EntityEquipmentSlot[0]);
		setCategory("utils");
	}
	
	@Override
	public int getMaxLevel()
	{
		return 3;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled && HorseArmorType.isHorseArmor(stack);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		CAP.handleConfig(config, getConfigName());
		LIMITER = config.get(getConfigName(), "limiter", 16).getInt();
	}
	
}
