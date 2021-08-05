package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class EnchantmentEnderMarksmen extends UniqueEnchantment
{
	public static final DoubleStat SCALAR = new DoubleStat(2D, "scalar");
	
	public EnchantmentEnderMarksmen()
	{
		super(new DefaultData("endermarksmen", Rarity.VERY_RARE, 5, true, 28, 25, 16), EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.ECOLOGICAL, Enchantments.MENDING, Enchantments.INFINITY);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR.handleConfig(config, getConfigName());
	}
}
