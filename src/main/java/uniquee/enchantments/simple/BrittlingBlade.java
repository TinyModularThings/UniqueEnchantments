package uniquee.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class BrittlingBlade extends UniqueEnchantment {

	public static final DoubleStat DAMAGE_SCALING = new DoubleStat(1D, "damage_scaling");
	public static final DoubleStat DURABILITY_EXPONENT = new DoubleStat(8D, "durability_exponent");
	public static final DoubleStat TRANSCENDED_EXPONENT_SCALING = new DoubleStat(0.5D, "transcended_exponent_scaling");
	
	public BrittlingBlade() 
	{
		super(new DefaultData("brittling_blade", Rarity.RARE, 5, false, true, 10, 10, 50).setTrancendenceLevel(500), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);
		addStats(DAMAGE_SCALING, DURABILITY_EXPONENT,TRANSCENDED_EXPONENT_SCALING);
	}

	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof TridentItem;
	}
}
