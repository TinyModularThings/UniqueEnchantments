package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class SagesBlessing extends UniqueEnchantment
{
	public static final DoubleStat XP_BOOST = new DoubleStat(0.1D, "xp_boost");
	public static final DoubleStat TRANSCENDED_BOOST = new DoubleStat(2.0, "transcended_exponent");
	
	public SagesBlessing()
	{
		super(new DefaultData("sages_blessing", Rarity.UNCOMMON, 5, false, false, 15, 5, 20).setTrancendenceLevel(500), EnchantmentType.BREAKABLE, EquipmentSlotType.values());
		addStats(XP_BOOST, TRANSCENDED_BOOST);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.FAST_FOOD, Enchantments.SILK_TOUCH);
	}	
}