package uniqueeutils.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class EssenceOfSlime extends UniqueEnchantment
{
	public static final DoubleStat DURABILITY_LOSS = new DoubleStat(3D, "durability_loss");
	public static final DoubleStat FALL_DISTANCE_MULTIPLIER = new DoubleStat(3D, "fall_distance_multiplier");
	
	
	public EssenceOfSlime()
	{
		super(new DefaultData("essence_of_slime", Rarity.VERY_RARE, 5, true, 15, 5, 10), EnumEnchantmentType.ARMOR_FEET, EntityEquipmentSlot.FEET);
		setCategory("utils");
		addStats(DURABILITY_LOSS, FALL_DISTANCE_MULTIPLIER);
	}
	
	
}
