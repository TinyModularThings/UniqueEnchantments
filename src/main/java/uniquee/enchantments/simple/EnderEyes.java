package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;

public class EnderEyes extends UniqueEnchantment
{
	public EnderEyes()
	{
		super(new DefaultData("ender_eyes", Rarity.UNCOMMON, 1, false, 10, 2, 5), EnchantmentType.ARMOR_HEAD, new EquipmentSlotType[]{EquipmentSlotType.HEAD});
	}
	
	@Override
	public void loadData(Builder config)
	{
	}
}
