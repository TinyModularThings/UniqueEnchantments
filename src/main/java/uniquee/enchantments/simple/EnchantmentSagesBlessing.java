package uniquee.enchantments.simple;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentUntouching;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;
import uniquee.enchantments.unique.EnchantmentFastFood;

public class EnchantmentSagesBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	public static double XP_BOOST = 0.2D;
	
	public EnchantmentSagesBlessing()
	{
		super(new DefaultData("sages_blessing", Rarity.COMMON, 5, false, 10, 4, 10), EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return EnumEnchantmentType.WEAPON.canEnchantItem(stack.getItem());
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentUntouching || ench instanceof EnchantmentFastFood ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		XP_BOOST = config.get(getConfigName(), "xp_boost", 0.2D).getDouble();
	}
	
}
