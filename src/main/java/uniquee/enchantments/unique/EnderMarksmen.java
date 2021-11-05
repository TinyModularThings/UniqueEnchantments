package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class EnderMarksmen extends UniqueEnchantment
{
	public static final DoubleStat EXTRA_DURABILITY = new DoubleStat(2D, "extra_durability");
	
	public EnderMarksmen()
	{
		super(new DefaultData("endermarksmen", Rarity.VERY_RARE, 5, true, false, 28, 25, 16), EnchantmentType.BOW, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(EXTRA_DURABILITY);
	}
			
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.ECOLOGICAL, Enchantments.MENDING, Enchantments.INFINITY_ARROWS);
	}
}