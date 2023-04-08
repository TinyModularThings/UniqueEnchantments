package uniquee.enchantments.complex;

import java.util.UUID;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class SwiftBlade extends UniqueEnchantment
{
	public static final UUID SWIFT_MOD = UUID.fromString("3b538121-d4d7-4aa3-8c64-e9849f43526a");
	public static final DoubleStat ATTACK_SPEED_CAP = new DoubleStat(20.0D, "transcended_attack_speed_cap");

	public SwiftBlade()
	{
		super(new DefaultData("swift_blade", Rarity.VERY_RARE, 2, false, false, 30, 75, 5).setTrancendenceLevel(200), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);
		addStats(ATTACK_SPEED_CAP);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof TridentItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.BERSERKER);
	}
}
