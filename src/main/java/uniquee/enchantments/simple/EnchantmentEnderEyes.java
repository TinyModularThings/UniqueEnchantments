package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentEnderEyes extends UniqueEnchantment
{
	public EnchantmentEnderEyes()
	{
		super(new DefaultData("ender_eyes", Rarity.UNCOMMON, false, 10, 2, 5), EnumEnchantmentType.ARMOR_HEAD, new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 1;
	}
	
	@Override
	public void loadData(Configuration config)
	{
	}
}
