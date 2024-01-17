package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;

public class Swift extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("df795085-a576-4035-abaf-02f6ecd4a2c7");
	
	public Swift()
	{
		super(new DefaultData("swift", Rarity.UNCOMMON, 2, false, false, 14, 12, 10), EnchantmentCategory.BREAKABLE, EquipmentSlot.LEGS);
	}
}
