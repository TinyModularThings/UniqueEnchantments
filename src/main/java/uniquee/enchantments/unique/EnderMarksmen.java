package uniquee.enchantments.unique;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class EnderMarksmen extends UniqueEnchantment
{
	public static final DoubleStat EXTRA_DURABILITY = new DoubleStat(2D, "extra_durability");
	
	public EnderMarksmen()
	{
		super(new DefaultData("ender_marksmen", Rarity.VERY_RARE, 5, true, false, 28, 25, 16), EnchantmentCategory.BOW, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(EXTRA_DURABILITY);
	}
			
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.ECOLOGICAL, Enchantments.MENDING, Enchantments.INFINITY_ARROWS);
	}
}