package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class EnchantmentBoneCrusher extends UniqueEnchantment
{
	public static final DoubleStat SCALAR = new DoubleStat(0.2D, "scalar");
	
	public EnchantmentBoneCrusher()
	{
		super(new DefaultData("bone_crusher", Rarity.VERY_RARE, 2, true, 18, 5, 50), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR.handleConfig(config, getConfigName());
	}
	
	public static boolean isNotArmored(AbstractSkeleton skeleton)
	{
		for(EntityEquipmentSlot slot : EntityEquipmentSlot.values())
		{
			if(slot.getSlotType() == Type.ARMOR && slot != EntityEquipmentSlot.HEAD && !skeleton.getItemStackFromSlot(slot).isEmpty())
			{
				return false;
			}
		}
		return true;
	}
}
