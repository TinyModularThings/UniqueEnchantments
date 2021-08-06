package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class BoneCrusher extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(0.15D, "bonus_damage");
	
	public BoneCrusher()
	{
		super(new DefaultData("bone_crusher", Rarity.VERY_RARE, 4, true, 2, 8, 20), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND);
		addStats(BONUS_DAMAGE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe;
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
