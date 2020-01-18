package uniquee.enchantments.simple;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.complex.SwiftBladeEnchantment;
import uniquee.utils.DoubleStat;

public class FocusImpactEnchantment extends UniqueEnchantment
{
	public static DoubleStat DAMAGE = new DoubleStat(1.05D, "damage");
	
	public FocusImpactEnchantment()
	{
		super(new DefaultData("focus_impact", Rarity.RARE, false, 20, 2, 17), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 3;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof SwiftBladeEnchantment ? false : super.canApplyTogether(ench);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem;
	}

	@Override
	public void loadData(Builder config)
	{
		config.comment("Important Info: Turning the Scalar to 0.639 or below will end up Healing the enemy with the Bonus Damage instead of Damaging on LvL 3 for a 1.6 Attack Speed(Default Attack Speed). so keep it above");
		DAMAGE.handleConfig(config);
	}
	
}
