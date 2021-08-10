package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;
import uniquee.utils.DoubleStat;

public class SagesBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	public static DoubleStat XP_BOOST = new DoubleStat(0.2D, "xp_boost");
	
	public SagesBlessing()
	{
		super(new DefaultData("sages_blessing", Rarity.COMMON, 5, false, 10, 4, 10), EnchantmentType.DIGGER, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
		
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return EnchantmentType.WEAPON.canEnchantItem(stack.getItem());
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.FAST_FOOD, Enchantments.SILK_TOUCH);
	}

	@Override
	public void loadData(Builder config)
	{
		XP_BOOST.handleConfig(config);
	}
	
}
