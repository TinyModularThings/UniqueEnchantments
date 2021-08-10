package uniquee.enchantments.complex;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class PerpetualStrike extends UniqueEnchantment
{
	public static DoubleStat SCALAR = new DoubleStat(0.075D, "scalar");
	public static final String HIT_COUNT = "strikes";
	public static final String HIT_ID = "hit_id";
	
	public PerpetualStrike()
	{
		super(new DefaultData("perpetualstrike", Rarity.VERY_RARE, 3, false, 26, 2, 30), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.SPARTAN_WEAPON);
	}
	
	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
	}
}
