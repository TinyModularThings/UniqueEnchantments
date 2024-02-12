package uniquee.enchantments.unique;

import net.minecraft.world.entity.EquipmentSlot;
import uniquebase.api.BaseUEMod;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class Grimoire extends UniqueEnchantment
{
	public static final DoubleStat FLAT_SCALING = new DoubleStat(1.0D, "flat_scaling");
	public static final DoubleStat LEVEL_SCALING = new DoubleStat(1.0D, "level_scaling");
	
	public Grimoire()
	{
		super(new DefaultData("grimoire", Rarity.VERY_RARE, 5, true, false, 70, 36, 20).setTrancendenceLevel(500), BaseUEMod.ALL_TYPES, EquipmentSlot.values());
		addStats(FLAT_SCALING, LEVEL_SCALING);
	}
}