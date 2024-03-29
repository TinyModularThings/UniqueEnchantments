package uniqueeutils.enchantments.curse;

import net.minecraft.world.entity.EquipmentSlot;
import uniquebase.api.BaseUEMod;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class PhanesRegret extends UniqueEnchantment
{
	public static final DoubleStat CHANCE = new DoubleStat(0.105D, "deny_percent");
	
	public PhanesRegret()
	{
		super(new DefaultData("phanes_regret", Rarity.RARE, 2, true, true, 40, 2, 75), BaseUEMod.ALL_TYPES, EquipmentSlot.values());
		setCategory("utils");
		addStats(CHANCE);
		setCurse();
	}
}