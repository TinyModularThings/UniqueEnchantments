package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IGraceEnchantment;
import uniquee.utils.DoubleStat;

public class EnchantmentWarriorsGrace extends UniqueEnchantment implements IGraceEnchantment
{
	public static final DoubleStat DURABILITY_GAIN = new DoubleStat(1.1D, "durability_gain");

	public EnchantmentWarriorsGrace()
	{
		super(new DefaultData("warriorsgrace", Rarity.RARE, 1, true, 22, 2, 5), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.ECOLOGICAL, UniqueEnchantments.ALCHEMISTS_GRACE, UniqueEnchantments.NATURES_GRACE, Enchantments.MENDING, Enchantments.UNBREAKING);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		DURABILITY_GAIN.handleConfig(config, getConfigName());
	}
}
