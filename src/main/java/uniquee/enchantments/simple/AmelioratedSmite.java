package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class AmelioratedSmite extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(3D, "bonus_damage");

	public AmelioratedSmite()
	{
		super(new DefaultData("undead", Rarity.RARE, 5, true, 6, 4, 30), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}

	@Override
	public void loadData(Configuration config)
	{
		BONUS_DAMAGE.handleConfig(config, getConfigName());
	}
	
    @Override
	public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType)
    {
    	return creatureType == EnumCreatureAttribute.UNDEAD ? BONUS_DAMAGE.getFloat(level) : 0F;
    }
}
