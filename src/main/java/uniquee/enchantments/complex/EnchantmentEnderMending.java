package uniquee.enchantments.complex;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentMending;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.unique.EnchantmentEcological;
import uniquee.enchantments.unique.EnchantmentEnderMarksmen;
import uniquee.enchantments.unique.EnchantmentWarriorsGrace;
import uniquee.utils.DoubleLevelStats;

public class EnchantmentEnderMending extends UniqueEnchantment
{
	public static final String ENDER_TAG = "ender_mending";
	public static final DoubleLevelStats ABSORBTION_RATIO = new DoubleLevelStats("absorbtion_ratio", 0.25D, 0.25D);
	public static int LIMIT = 250;
	
	public EnchantmentEnderMending()
	{
		super(new DefaultData("ender_mending", Rarity.VERY_RARE, 3, true, 26, 8, 5), EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.values());
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentMending || ench instanceof EnchantmentEnderMarksmen || ench instanceof EnchantmentWarriorsGrace || ench instanceof EnchantmentEcological ? false : super.canApplyTogether(ench);
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return EnumEnchantmentType.ARMOR.canEnchantItem(stack.getItem());
	}
	
	@Override
	public void loadData(Configuration config)
	{
		ABSORBTION_RATIO.handleConfig(config, getConfigName());
		LIMIT = config.get(getConfigName(), "limit", 250).getInt();
	}
	
}
