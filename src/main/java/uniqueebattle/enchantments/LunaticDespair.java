package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class LunaticDespair extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(0.2D, "bonus_damage");
	public static final DoubleStat SELF_DAMAGE = new DoubleStat(0.25D, "self_damage");
	public static final DoubleStat SELF_MAGIC_DAMAGE = new DoubleStat(0.25D, "self_magic_damage");
	
	public LunaticDespair()
	{
		super(new DefaultData("lunatic_despair", Rarity.UNCOMMON, 1, true, 10, 4, 40), EnchantmentType.ALL, EquipmentSlotType.values());
		addStats(BONUS_DAMAGE, SELF_DAMAGE, SELF_MAGIC_DAMAGE);
		setCategory("battle");
		setCurse();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof HoeItem;
	}
	
}