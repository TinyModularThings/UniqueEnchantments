package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.api.filters.IBlessingEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class SagesBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	public static final DoubleStat XP_BOOST = new DoubleStat(0.2D, "xp_boost");
	
	public SagesBlessing()
	{
		super(new DefaultData("sages_blessing", Rarity.COMMON, 5, false, false, 5, 5, 20), EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(XP_BOOST);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return EnchantmentType.WEAPON.canEnchantItem(stack.getItem()) || stack.getItem() instanceof CrossbowItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.FAST_FOOD, Enchantments.SILK_TOUCH);
	}	
}