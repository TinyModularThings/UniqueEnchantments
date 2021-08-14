package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class BoneCrusher extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(0.15D, "bonus_damage");
	
	public BoneCrusher()
	{
		super(new DefaultData("bone_crusher", Rarity.VERY_RARE, 4, true, true, 2, 8, 20), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(BONUS_DAMAGE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem;
	}
	
	public static boolean isNotArmored(AbstractSkeletonEntity skeleton)
	{
		for(EquipmentSlotType slot : EquipmentSlotType.values())
		{
			if(slot.getSlotType() == Group.ARMOR && slot != EquipmentSlotType.HEAD && !skeleton.getItemStackFromSlot(slot).isEmpty())
			{
				return false;
			}
		}
		return true;
	}
}
