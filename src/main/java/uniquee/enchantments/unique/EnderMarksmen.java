package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class EnderMarksmen extends UniqueEnchantment
{
	public static final DoubleStat EXTRA_DURABILITY = new DoubleStat(2D, "extra_durability");
	
	public EnderMarksmen()
	{
		super(new DefaultData("endermarksmen", Rarity.VERY_RARE, 5, true, 28, 25, 16), EnumEnchantmentType.BOW, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		addStats(EXTRA_DURABILITY);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.ECOLOGICAL, Enchantments.MENDING, Enchantments.INFINITY);
	}
}