package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;
import uniquee.utils.DoubleStat;

public class EnchantmentSagesBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	public static final DoubleStat XP_BOOST = new DoubleStat(0.2D, "xp_boost");
	
	public EnchantmentSagesBlessing()
	{
		super(new DefaultData("sages_blessing", Rarity.COMMON, 5, false, 5, 5, 20), EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
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
	
	@Override
	public void loadData(Configuration config)
	{
		XP_BOOST.handleConfig(config, getConfigName());
	}
	
}
