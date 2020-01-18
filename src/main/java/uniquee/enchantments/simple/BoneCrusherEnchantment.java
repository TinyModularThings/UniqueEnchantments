package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class BoneCrusherEnchantment extends UniqueEnchantment
{
	public static DoubleStat SCALAR = new DoubleStat(0.2D, "scalar");
	
	public BoneCrusherEnchantment()
	{
		super(new DefaultData("bone_crusher", Rarity.VERY_RARE, true, 18, 5, 50), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem;
	}
	
	@Override
	public int getMaxLevel()
	{
		return 2;
	}
	
	public static boolean isNotArmored(AbstractSkeletonEntity skeleton)
	{
		for(EquipmentSlotType slot : EquipmentSlotType.values())
		{
			if(slot.getSlotType() == Group.ARMOR && !skeleton.getItemStackFromSlot(slot).isEmpty())
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
	}
}
