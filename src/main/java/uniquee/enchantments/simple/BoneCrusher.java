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

public class BoneCrusher extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(0.15D, "scalar");
	
	public BoneCrusher()
	{
		super(new DefaultData("bone_crusher", Rarity.VERY_RARE, 4, true, 2, 8, 20), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		BONUS_DAMAGE.handleConfig(config, getConfigName());
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
