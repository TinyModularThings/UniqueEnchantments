package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
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
		super(new DefaultData("ares_grace", Rarity.COMMON, 3, false, 2, 8, 75), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
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
		return stack.getItem() instanceof ItemAxe && stack.getItem() instanceof ItemBow && stack.getItem() instanceof ItemHoe;
	}
}
