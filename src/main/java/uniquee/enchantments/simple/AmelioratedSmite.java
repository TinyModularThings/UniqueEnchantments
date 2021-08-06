package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class AmelioratedSmite extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(3D, "bonus_damage");

	public AmelioratedSmite()
	{
		super(new DefaultData("undead", Rarity.RARE, 5, true, 6, 4, 30), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND);
		addStats(BONUS_DAMAGE);
	}
	
    @Override
	public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType)
    {
    	return creatureType == EnumCreatureAttribute.UNDEAD ? BONUS_DAMAGE.getFloat(level) : 0F;
    }
}
