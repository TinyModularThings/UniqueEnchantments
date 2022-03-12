package uniqueeutils.enchantments.curse;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;

public class PhanesRegret extends UniqueEnchantment
{
	public PhanesRegret()
	{
		super(new DefaultData("phanes_regret", Rarity.RARE, 2, true, 40, 2, 75), EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
		setCategory("utils");
		setCurse();
	}
}