package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class LunaticDespair extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(0.2D, "bonus_damage");
	
	public LunaticDespair()
	{
		super(new DefaultData("lunatic_despair", Rarity.VERY_RARE, 2, true, 10, 4, 40), EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.values());
		setCategory("battle");
		addStats(BONUS_DAMAGE);
		setCurse();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemHoe;
	}
}