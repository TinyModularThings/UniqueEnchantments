package uniquee.enchantments.upgrades;

import java.util.EnumSet;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import uniquebase.api.EnchantedUpgrade;
import uniquee.UE;

public class GrimoiresUpgrade extends EnchantedUpgrade
{
	public GrimoiresUpgrade()
	{
		super("uniquee", "grimoire", () -> UE.GRIMOIRE);
		setEquimentSlots(EnumSet.allOf(EquipmentSlotType.class));
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentType.BREAKABLE.canEnchant(stack.getItem());
	}
	
}
