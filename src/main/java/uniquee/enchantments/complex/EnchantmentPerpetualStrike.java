package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentPerpetualStrike extends UniqueEnchantment
{
	public static double SCALAR = 0.075D;
	public static final String HIT_COUNT = "strikes";
	public static final String HIT_ID = "hit_id";
	
	public EnchantmentPerpetualStrike()
	{
		super(new DefaultData("perpetualstrike", Rarity.VERY_RARE, 3, false, 26, 2, 30), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.SPARTAN_WEAPON);
	}
	
	@Override
	public void loadData(Configuration entry)
	{
		SCALAR = entry.get(getConfigName(), "scalar", 0.075D).getDouble();
	}
}
