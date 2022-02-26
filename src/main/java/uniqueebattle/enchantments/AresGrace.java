package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniqueebattle.UniqueEnchantmentsBattle;

public class AresGrace extends UniqueEnchantment
{
	public static final DoubleStat DAMAGE = new DoubleStat(1D, "damage");
	public static final DoubleStat DURABILITY = new DoubleStat(1D, "durability");
	
	public AresGrace()
	{
		super(new DefaultData("ares_grace", Rarity.COMMON, 3, false, 2, 8, 75), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(DAMAGE, DURABILITY);
		setCategory("battle");
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantmentsBattle.ARES_FRAGMENT);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem && stack.getItem() instanceof BowItem && stack.getItem() instanceof HoeItem;
	}
}
