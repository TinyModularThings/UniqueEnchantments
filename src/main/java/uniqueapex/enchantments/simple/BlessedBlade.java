package uniqueapex.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniqueapex.enchantments.ApexEnchantment;
import uniquebase.utils.DoubleStat;

public class BlessedBlade extends ApexEnchantment
{
	public static final DoubleStat ATTACK = new DoubleStat(1.5D, "attack");
	public static final DoubleStat LEVEL_SCALE = new DoubleStat(1D, "level_scale");	
	
	public BlessedBlade()
	{
		super("blessed_blade", EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(ATTACK, LEVEL_SCALE);
		setCategory("apex");
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof TridentItem;
	}
	
	@Override
	public float getDamageBonus(int level, MobType mobType, ItemStack enchantedItem) {
		return (float) Math.log(1+Math.pow(ATTACK.get(level), 3));
	}
}
