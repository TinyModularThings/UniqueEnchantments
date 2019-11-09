package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentMomentum extends UniqueEnchantment
{
	public static final String LAST_MINE = "last_mined";
	public static final String COUNT = "mined";
	public static double SCALAR = 2.5D;
	public static int CAP = 2;
	public static int MAX_DELAY = 40;
	
	public EnchantmentMomentum()
	{
		super(new DefaultData("momentum", Rarity.RARE, true, 10, 5, 75), EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 3;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 2.5D).getDouble();
		CAP = config.get(getConfigName(), "cap", 2).getInt();
		MAX_DELAY = config.get(getConfigName(), "max_delay", 40).getInt();
	}
}
