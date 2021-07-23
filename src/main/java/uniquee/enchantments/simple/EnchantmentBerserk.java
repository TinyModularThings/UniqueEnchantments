package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class EnchantmentBerserk extends UniqueEnchantment
{
	public static final DoubleStat SCALAR = new DoubleStat(0.125D, "scalar");
	
	public EnchantmentBerserk()
	{
		super(new DefaultData("berserk", Rarity.RARE, 2, false, 20, 2, 22), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
        return stack.getItem() instanceof ItemAxe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.SWIFT_BLADE, UniqueEnchantments.SPARTAN_WEAPON);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR.handleConfig(config, getConfigName());
	}
}
