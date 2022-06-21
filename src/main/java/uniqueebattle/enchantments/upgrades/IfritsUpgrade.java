package uniqueebattle.enchantments.upgrades;

import java.util.EnumSet;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import uniquebase.api.EnchantedUpgrade;
import uniqueebattle.UEBattle;

public class IfritsUpgrade extends EnchantedUpgrade
{
	public IfritsUpgrade()
	{
		super("uniquebattle", "ifrits_judgement", "upgrade.uniquebattle.loot", () -> UEBattle.IFRITS_JUDGEMENT);
		setEquimentSlots(EnumSet.allOf(EquipmentSlotType.class));
	}
	
	@Override
	public boolean isValid(ItemStack stack)
	{
		return !stack.isEmpty();
	}
}
