package uniqueebattle.enchantments;

import java.util.UUID;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.DoubleStat;

public class Fury extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("d0eb6348-96e6-42c7-aaf0-f83e37d3b4ce");
	public static final DoubleStat ATTACK_SPEED_SCALE = new DoubleStat(1D, "attack_speed_scale");
	public static final DoubleLevelStats DROP_CHANCE = new DoubleLevelStats("drop_chance", 0.01D, 0.03D);
	
	public Fury()
	{
		super(new DefaultData("fury", Rarity.RARE, 3, true, 20, 4, 50), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND);
		addStats(ATTACK_SPEED_SCALE, DROP_CHANCE);
		setDisableDefaultItems();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe;
	}
}
