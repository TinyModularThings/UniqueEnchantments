package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentEndestReap extends UniqueEnchantment
{
	public static final String REAP_STORAGE = "reap_storage";
	public static double BONUS_DAMAGE_LEVEL = 0.7D;
	public static double REAP_MULTIPLIER = 0.005D;
	
	public EnchantmentEndestReap()
	{
		super(new DefaultData("endest_reap", Rarity.RARE, 4, true, 30, 6, 20), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(Enchantments.SHARPNESS, Enchantments.MENDING, UniqueEnchantments.ADV_SHARPNESS, UniqueEnchantments.ENDER_MENDING, UniqueEnchantments.SPARTAN_WEAPON, UniqueEnchantments.SAGES_BLESSING);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		BONUS_DAMAGE_LEVEL = config.get(getConfigName(), "bonus_damage_level", 0.7D).getDouble();
		REAP_MULTIPLIER = config.get(getConfigName(), "reap_multiplier", 0.005D).getDouble();
	}
	
}
