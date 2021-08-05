package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;
import uniquee.utils.DoubleStat;
import uniquee.utils.IntStat;

public class EnderMending extends UniqueEnchantment
{
	public static final String ENDER_TAG = "ender_mending";
	public static final DoubleLevelStats ABSORBTION_RATIO = new DoubleLevelStats("absorbtion_ratio", 0.55D, 0.15D);
	public static final DoubleStat ABSORBTION_CAP = new DoubleStat(2D, "absorbtion_cap");
	public static final IntStat LIMIT = new IntStat(250, "limit");
	
	public EnderMending()
	{
		super(new DefaultData("ender_mending", Rarity.VERY_RARE, 3, true, 20, 10, 5), EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.values());
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.MENDING, UniqueEnchantments.ENDERMARKSMEN, UniqueEnchantments.WARRIORS_GRACE, UniqueEnchantments.ECOLOGICAL);
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
		ABSORBTION_CAP.handleConfig(config, getConfigName());
		LIMIT.handleConfig(config, getConfigName());
	}
	
}
