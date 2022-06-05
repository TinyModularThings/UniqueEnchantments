package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class EnderMarksmen extends UniqueEnchantment
{
	public static final DoubleStat EXTRA_DURABILITY = new DoubleStat(2D, "extra_durability");
	public static final DoubleStat TRANSCENDE_CHANCE = new DoubleStat(0.2D, "transcended_chance_level_scaling");
	public static final DoubleStat TRANSCENDE_DAMAGE = new DoubleStat(2.0D, "transcended_damage_base");
	
	public EnderMarksmen()
	{
		super(new DefaultData("ender_marksmen", Rarity.VERY_RARE, 5, true, false, 28, 25, 16), EnchantmentType.BOW, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(EXTRA_DURABILITY);
	}
			
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.ECOLOGICAL, Enchantments.MENDING, Enchantments.INFINITY_ARROWS);
	}
}