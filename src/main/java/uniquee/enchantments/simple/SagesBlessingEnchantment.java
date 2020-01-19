package uniquee.enchantments.simple;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.SilkTouchEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;
import uniquee.enchantments.unique.FastFoodEnchantment;
import uniquee.utils.DoubleStat;

public class SagesBlessingEnchantment extends UniqueEnchantment implements IBlessingEnchantment
{
	public static DoubleStat XP_BOOST = new DoubleStat(0.2D, "xp_boost");
	
	public SagesBlessingEnchantment()
	{
		super(new DefaultData("sages_blessing", Rarity.COMMON, false, 10, 4, 10), EnchantmentType.DIGGER, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 5;
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return EnchantmentType.WEAPON.canEnchantItem(stack.getItem());
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof SilkTouchEnchantment || ench instanceof FastFoodEnchantment ? false : super.canApplyTogether(ench);
	}

	@Override
	public void loadData(Builder config)
	{
		XP_BOOST.handleConfig(config);
	}
	
}
