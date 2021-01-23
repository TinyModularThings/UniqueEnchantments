package uniquee.enchantments.complex;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.unique.EcologicalEnchantment;
import uniquee.enchantments.unique.EnderMarksmenEnchantment;
import uniquee.enchantments.unique.WarriorsGraceEnchantment;
import uniquee.utils.DoubleLevelStats;
import uniquee.utils.IntStat;

public class EnderMendingEnchantment extends UniqueEnchantment
{
	public static final String ENDER_TAG = "ender_mending";
	public static final DoubleLevelStats ABSORBTION_RATIO = new DoubleLevelStats("absorbtion_ratio", 0.25D, 0.25D);
	public static final IntStat LIMIT = new IntStat(250, "limit");
	
	public EnderMendingEnchantment()
	{
		super(new DefaultData("ender_mending", Rarity.VERY_RARE, 3, true, 26, 8, 5), EnchantmentType.BREAKABLE, EquipmentSlotType.values());
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof MendingEnchantment || ench instanceof EnderMarksmenEnchantment || ench instanceof WarriorsGraceEnchantment || ench instanceof EcologicalEnchantment ? false : super.canApplyTogether(ench);
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return EnchantmentType.ARMOR.canEnchantItem(stack.getItem());
	}
	
	@Override
	public void loadData(Builder config)
	{
		ABSORBTION_RATIO.handleConfig(config);
		LIMIT.handleConfig(config);
	}
	
}
