package uniqueebattle.enchantments.upgrades;

import java.util.EnumSet;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.EnchantedUpgrade;
import uniqueebattle.UEBattle;

public class LunaticUpgrade extends EnchantedUpgrade
{
	public LunaticUpgrade()
	{
		super("uniquebattle", "lunatic_despair", "upgrade.uniquebattle.use_time", () -> UEBattle.LUNATIC_DESPAIR);
		setEquimentSlots(EnumSet.allOf(EquipmentSlot.class));
		
	}
	
	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentCategory.ARMOR.canEnchant(stack.getItem());
	}
}
