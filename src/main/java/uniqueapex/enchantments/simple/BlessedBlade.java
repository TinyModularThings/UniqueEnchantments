package uniqueapex.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import uniqueapex.enchantments.ApexEnchantment;
import uniquebase.utils.DoubleStat;

public class BlessedBlade extends ApexEnchantment
{
	public static final DoubleStat ATTACK = new DoubleStat(1.5D, "attack");
	public static final DoubleStat LEVEL_SCALE = new DoubleStat(1D, "level_scale");	
	
	public BlessedBlade()
	{
		super("blessed_blade", EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(ATTACK, LEVEL_SCALE);
		setCategory("apex");
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof TridentItem;
	}
	
    @Override
	public float getDamageBonus(int level, CreatureAttribute creatureType)
    {
    	return (float)Math.sqrt(ATTACK.getFloat(level));
    }
	
}
