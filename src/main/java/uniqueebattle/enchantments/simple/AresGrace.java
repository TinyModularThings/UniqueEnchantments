package uniqueebattle.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniqueebattle.UEBattle;

public class AresGrace extends UniqueEnchantment
{
	public static final DoubleStat DAMAGE = new DoubleStat(1D, "damage");
	public static final DoubleStat DURABILITY = new DoubleStat(1D, "durability");
	
	public AresGrace()
	{
		super(new DefaultData("ares_grace", Rarity.COMMON, 3, false, false, 2, 8, 75), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(DAMAGE, DURABILITY);
		setCategory("battle");
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UEBattle.ARES_FRAGMENT);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem && stack.getItem() instanceof BowItem && stack.getItem() instanceof HoeItem;
	}
}
