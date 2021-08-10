package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;
import uniquee.utils.IntStat;

public class EnderMending extends UniqueEnchantment
{
	public static final String ENDER_TAG = "ender_mending";
	public static final DoubleLevelStats ABSORBTION_RATIO = new DoubleLevelStats("absorbtion_ratio", 0.25D, 0.25D);
	public static final IntStat LIMIT = new IntStat(250, "limit");
	
	public EnderMending()
	{
		super(new DefaultData("ender_mending", Rarity.VERY_RARE, 3, true, 26, 8, 5), EnchantmentType.BREAKABLE, EquipmentSlotType.values());
	}
	
	public void loadIncompats()
	{
		addIncomats(Enchantments.MENDING, UniqueEnchantments.ENDERMARKSMEN, UniqueEnchantments.WARRIORS_GRACE, UniqueEnchantments.ECOLOGICAL);
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
