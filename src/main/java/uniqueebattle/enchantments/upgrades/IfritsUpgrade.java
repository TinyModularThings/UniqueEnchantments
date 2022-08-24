package uniqueebattle.enchantments.upgrades;

import java.util.EnumSet;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import uniquebase.api.EnchantedUpgrade;
import uniqueebattle.UEBattle;

public class IfritsUpgrade extends EnchantedUpgrade
{
	public IfritsUpgrade()
	{
		super("uniquebattle", "ifrits_judgement", "upgrade.uniquebattle.loot", () -> UEBattle.IFRITS_JUDGEMENT);
		setEquimentSlots(EnumSet.allOf(EquipmentSlot.class));
	}
	
	@Override
	public boolean isValid(ItemStack stack)
	{
		return !stack.isEmpty();
	}
}
