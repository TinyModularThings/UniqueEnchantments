package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;

public class EnderEyesEnchantment extends UniqueEnchantment
{
	public EnderEyesEnchantment()
	{
		super(new DefaultData("ender_eyes", Rarity.UNCOMMON, false, 10, 2, 5), EnchantmentType.ARMOR_HEAD, new EquipmentSlotType[]{EquipmentSlotType.HEAD});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 1;
	}
	
	@Override
	public void loadData(Builder config)
	{
	}
}
