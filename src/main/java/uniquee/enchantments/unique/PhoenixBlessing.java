package uniquee.enchantments.unique;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import uniquebase.api.BaseUEMod;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.DoubleStat;

public class PhoenixBlessing extends UniqueEnchantment
{
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("range", 3D, 0.25D);
	public static final DoubleStat TRANSCENDED_DURATION = new DoubleStat(600, "transcended_duration");
	
	public PhoenixBlessing()
	{
		super(new DefaultData("phoenixs_blessing", Rarity.RARE, 2, true, true, 26, 2, 2), BaseUEMod.ALL_TYPES, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(RANGE, TRANSCENDED_DURATION);
		setDisableDefaultItems();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() == Items.TOTEM_OF_UNDYING;
	}
}
