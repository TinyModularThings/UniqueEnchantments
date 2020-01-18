package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;
import uniquee.utils.IntStat;

public class MomentumEnchantment extends UniqueEnchantment
{
	public static final String LAST_MINE = "last_mined";
	public static final String COUNT = "mined";
	public static DoubleStat SCALAR = new DoubleStat(2.5D, "scalar");
	public static IntStat CAP = new IntStat(2, "cap");
	public static IntStat MAX_DELAY = new IntStat(40, "max_delay");
	
	public MomentumEnchantment()
	{
		super(new DefaultData("momentum", Rarity.RARE, true, 10, 5, 75), EnchantmentType.DIGGER, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 3;
	}

	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
		CAP.handleConfig(config);
		MAX_DELAY.handleConfig(config);
	}
	
}
