package uniqueebattle.enchantments;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.BaseUEMod;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class LunaticDespair extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(0.2D, "bonus_damage");
	
	public LunaticDespair()
	{
		super(new DefaultData("lunatic_despair", Rarity.VERY_RARE, 2, true, false, 10, 4, 40), BaseUEMod.ALL_TYPES, EquipmentSlotType.values());
		addStats(BONUS_DAMAGE);
		setCategory("battle");
		setCurse();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof HoeItem;
	}
	
}