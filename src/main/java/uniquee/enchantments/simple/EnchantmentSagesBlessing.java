package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;

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
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.FAST_FOOD, Enchantments.SILK_TOUCH);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		XP_BOOST = config.get(getConfigName(), "xp_boost", 0.2D).getDouble();
	}
	
}
