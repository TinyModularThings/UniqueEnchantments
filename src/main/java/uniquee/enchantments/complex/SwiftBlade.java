package uniquee.enchantments.complex;

import java.util.UUID;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class SwiftBlade extends UniqueEnchantment
{
	public static final UUID SWIFT_MOD = UUID.fromString("3b538121-d4d7-4aa3-8c64-e9849f43526a");
	public static final DoubleStat BASE_SPEED = new DoubleStat(1.2D, "base_speed");
	public static final DoubleStat TRANSCENDED_ATTACK_SPEED_MULTIPLIER = new DoubleStat(2.0, "transcended_attack_speed_multiplier");

	public SwiftBlade()
	{
		super(new DefaultData("swift_blade", Rarity.VERY_RARE, 2, false, false, 30, 75, 5).setTrancendenceLevel(200), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);
		addStats(BASE_SPEED, TRANSCENDED_ATTACK_SPEED_MULTIPLIER);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.BERSERKER);
	}
}
