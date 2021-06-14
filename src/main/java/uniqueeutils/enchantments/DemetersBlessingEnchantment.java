package uniqueeutils.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;

public class DemetersBlessingEnchantment extends UniqueEnchantment
{
	public DemetersBlessingEnchantment()
	{
		super(new DefaultData("demeters_blessing", Rarity.VERY_RARE, 2, false, 12, 8, 75), EnchantmentType.BREAKABLE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof HoeItem);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(new ResourceLocation("uniquee", "demeters_soul"));
	}
	
	@Override
	public void loadData(Builder config)
	{	
	}
}