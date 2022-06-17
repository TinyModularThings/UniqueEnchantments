package uniqueebattle.enchantments.complex;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class DeepWounds extends UniqueEnchantment
{
	public static final IntStat DURATION = new IntStat(24, "duration");
	public static final DoubleStat BLEED_SCALE = new DoubleStat(1D, "bleed_scale");
	public static final DoubleStat SCALE = new DoubleStat(1D, "scale");
	
	
	public DeepWounds()
	{
		super(new DefaultData("deep_wounds", Rarity.RARE, 4, true, false, 28, 4, 20), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(DURATION, BLEED_SCALE, SCALE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem;
	}
	
}
