package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;

public class EnchantmentPhoenixBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	
	public EnchantmentPhoenixBlessing()
	{
		super(new DefaultData("phoenixs_blessing", Rarity.RARE, 1, true, 26, 2, 2), EnumEnchantmentType.ALL, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled && stack.getItem() == Items.TOTEM_OF_UNDYING;
	}
	
	@Override
	public void loadData(Configuration config)
	{
	}
	
}
