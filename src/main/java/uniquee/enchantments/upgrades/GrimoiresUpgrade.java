package uniquee.enchantments.upgrades;

import java.util.EnumSet;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.EnchantedUpgrade;
import uniquee.UE;

public class GrimoiresUpgrade extends EnchantedUpgrade
{
	public GrimoiresUpgrade()
	{
		super("uniquee", "grimoire", "upgrade.uniquee.durability", () -> UE.GRIMOIRE);
		setEquimentSlots(EnumSet.allOf(EquipmentSlot.class));
	}

	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentCategory.BREAKABLE.canEnchant(stack.getItem());
	}
	
}
