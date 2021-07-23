package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;
import uniquee.utils.IntStat;

public class EnchantmentMomentum extends UniqueEnchantment
{
	public static final String LAST_MINE = "last_mined";
	public static final String COUNT = "mined";
	public static final DoubleStat SCALAR = new DoubleStat(2.5D, "scalar");
	public static final IntStat CAP = new IntStat(2, "cap");
	public static final IntStat MAX_DELAY = new IntStat(40, "max_delay");
	
	public EnchantmentMomentum()
	{
		super(new DefaultData("momentum", Rarity.RARE, 3, true, 10, 5, 75), EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR.handleConfig(config, getConfigName());
		CAP.handleConfig(config, getConfigName());
		MAX_DELAY.handleConfig(config, getConfigName());
	}
}
