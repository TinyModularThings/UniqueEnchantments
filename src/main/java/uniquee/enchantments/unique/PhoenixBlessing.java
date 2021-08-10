package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;

public class PhoenixBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	public PhoenixBlessing()
	{
		super(new DefaultData("phoenixs_blessing", Rarity.RARE, 1, true, 26, 2, 2), EnchantmentType.ALL, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled.get() && stack.getItem() == Items.TOTEM_OF_UNDYING;
	}

	@Override
	public void loadData(Builder config)
	{
	}
	
}
