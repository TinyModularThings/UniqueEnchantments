package uniqueeutils.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class EnchantmentPhanesRegret extends UniqueEnchantment
{
	public static final DoubleStat CHANCE = new DoubleStat(0.125D, "chance");
	
	public EnchantmentPhanesRegret()
	{
		super(new DefaultData("phanes_regret", Rarity.UNCOMMON, 2, true, 10, 2, 75), EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
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
		CHANCE.handleConfig(config, getConfigName());
	}
	
}
