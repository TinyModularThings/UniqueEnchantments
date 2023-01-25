package uniqueapex.enchantments.unique;

import java.util.UUID;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniqueapex.enchantments.ApexEnchantment;
import uniquebase.utils.DoubleStat;

public class HarbingersOdium extends ApexEnchantment {
	
	public static final UUID DEBUFF_UUID = UUID.fromString("c93152c2-9caf-11ed-a8fc-0242ac120002");
	public static final DoubleStat VALUE_MODIFIER = new DoubleStat(0.01D, "value_modifier");
	public static final DoubleStat VALUE_CAP_SCALING = new DoubleStat(1D, "value_cap_scaling");
	public static final DoubleStat EFFECT_LEVEL_SCALING = new DoubleStat(1D, "effect_level_scaling");
	public static final DoubleStat EFFECT_DURATION_SCALING = new DoubleStat(1D, "effect_duration_scaling");

	public HarbingersOdium() {
		super("harbingers_odium", EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(VALUE_MODIFIER, VALUE_CAP_SCALING, EFFECT_LEVEL_SCALING, EFFECT_DURATION_SCALING);
		setCategory("apex");
	}

	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof TridentItem;
	}
}
