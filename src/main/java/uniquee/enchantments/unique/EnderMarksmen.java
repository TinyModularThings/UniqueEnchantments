package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class EnderMarksmen extends UniqueEnchantment
{
	public static DoubleStat SCALAR = new DoubleStat(2D, "scalar");
	
	public EnderMarksmen()
	{
		super(new DefaultData("endermarksmen", Rarity.VERY_RARE, 1, true, 28, 2, 16), EnchantmentType.BOW, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
			
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.ECOLOGICAL, Enchantments.MENDING, Enchantments.INFINITY);
	}
	
	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
	}
}
