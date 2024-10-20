package uniquee.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class Berserk extends UniqueEnchantment
{
	public static final DoubleStat PERCENTUAL_DAMAGE = new DoubleStat(1.0D, "percentual_damage");
	public static final DoubleStat MIN_HEALTH = new DoubleStat(1D, "min_health");
	public static final DoubleStat TRANSCENDED_HEALTH = new DoubleStat(0.5D, "transcended_health_percentage");
	
	public Berserk()
	{
		super(new DefaultData("berserker", Rarity.RARE, 2, false, true, 10, 8, 22).setTrancendenceLevel(200), EnchantmentCategory.ARMOR_CHEST, EquipmentSlot.CHEST);
		addStats(MIN_HEALTH, PERCENTUAL_DAMAGE, TRANSCENDED_HEALTH);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.VITAE, UE.ARES_BLESSING);
	}
}
