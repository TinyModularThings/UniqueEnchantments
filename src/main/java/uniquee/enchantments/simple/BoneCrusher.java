package uniquee.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class BoneCrusher extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(0.15D, "bonus_damage");
	public static final DoubleStat TRANSCENDED_CHANCE = new DoubleStat(0.4D, "transcended_loot_chance");
	
	public BoneCrusher()
	{
		super(new DefaultData("bone_crusher", Rarity.VERY_RARE, 4, true, true, 2, 8, 20).setTrancendenceLevel(500), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);
		addStats(BONUS_DAMAGE, TRANSCENDED_CHANCE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem;
	}
	
	public static boolean isNotArmored(AbstractSkeleton skeleton)
	{
		for(EquipmentSlot slot : EquipmentSlot.values())
		{
			if(slot.getType() == Type.ARMOR && slot != EquipmentSlot.HEAD && !skeleton.getItemBySlot(slot).isEmpty())
			{
				return false;
			}
		}
		return true;
	}
}
