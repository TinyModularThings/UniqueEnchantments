package uniquee.enchantments.simple;

import java.util.UUID;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class FocusedImpact extends UniqueEnchantment
{
	public static final UUID IMPACT_MOD = UUID.fromString("3b535821-d4d7-4aa3-8c64-e9849f43516a");
	public static final DoubleStat BASE_SPEED = new DoubleStat(1.05D, "attack_speed_comparison");
	public static final DoubleStat TRANSCENDED_ATTACK_SPEED_MULTIPLIER = new DoubleStat(0.715D, "transcended_attack_speed_mulitiplier");
	
	public FocusedImpact()
	{
		super(new DefaultData("focused_impact", Rarity.RARE, 3, false, true, 2, 8, 17).setTrancendenceLevel(200), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);
		addStats(BASE_SPEED, TRANSCENDED_ATTACK_SPEED_MULTIPLIER);
	}
		
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.SWIFT_BLADE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof TridentItem;
	}
}
