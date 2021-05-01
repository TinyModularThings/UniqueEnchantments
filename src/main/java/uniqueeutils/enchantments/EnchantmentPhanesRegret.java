package uniqueeutils.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentPhanesRegret extends UniqueEnchantment
{
	public static double CHANCE = 0.125D;
	
	public EnchantmentPhanesRegret()
	{
		super(new DefaultData("phanes_regret", Rarity.UNCOMMON, 1, true, 10, 2, 75), EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
		setCategory("utils");
	}
	
	@Override
	public boolean isCurse()
	{
		return true;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		CHANCE = config.get(getConfigName(), "chance", 0.125D).getDouble();

	}
	
}
