package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
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
		super(new DefaultData("sages_blessing", Rarity.COMMON, 5, false, 5, 5, 20), EnumEnchantmentType.DIGGER, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		addStats(XP_BOOST);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return EnumEnchantmentType.WEAPON.canEnchantItem(stack.getItem());
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.FAST_FOOD, Enchantments.SILK_TOUCH);
	}	
}