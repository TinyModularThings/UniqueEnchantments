package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentEnderMarksmen extends UniqueEnchantment
{
	public static double SCALAR = 2D;
	
	public EnchantmentEnderMarksmen()
	{
		super(new DefaultData("endermarksmen", Rarity.VERY_RARE, 1, true, 28, 2, 16), EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.ECOLOGICAL, Enchantments.MENDING, Enchantments.INFINITY);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 2D).getDouble();
	}
}
