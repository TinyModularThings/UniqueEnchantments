package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IGraceEnchantment;
import uniquee.utils.DoubleStat;

public class WarriorsGrace extends UniqueEnchantment implements IGraceEnchantment
{
	public static DoubleStat DURABILITY_GAIN = new DoubleStat(1.1D, "durability_gain");

	public WarriorsGrace()
	{
		super(new DefaultData("warriorsgrace", Rarity.VERY_RARE, 1, true, 22, 2, 30), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
		
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.ECOLOGICAL, UniqueEnchantments.ALCHEMISTS_GRACE, UniqueEnchantments.NATURES_GRACE, Enchantments.MENDING, Enchantments.UNBREAKING);
	}

	@Override
	public void loadData(Builder config)
	{
		DURABILITY_GAIN.handleConfig(config);
	}
	
}
