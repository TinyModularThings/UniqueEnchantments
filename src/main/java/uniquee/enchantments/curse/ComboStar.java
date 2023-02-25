package uniquee.enchantments.curse;

import java.util.UUID;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class ComboStar extends UniqueEnchantment
{
	public static final String COMBO_NAME = "combo_counter";
	public static final UUID SPEED_EFFECT = UUID.fromString("d546dc69-4df3-482b-bf17-7caa9d7942f6");
	public static final DoubleStat DAMAGE_LOSS = new DoubleStat(0.95D, "damage_loss");
	public static final DoubleStat CRIT_DAMAGE = new DoubleStat(1D, "crit_bonus");
	public static final DoubleStat COUNTER_MULTIPLIER = new DoubleStat(1D, "counter_multiplier");
	
	public ComboStar()
	{
		super(new DefaultData("combo_star", Rarity.VERY_RARE, 4, false, false, 20, 10, 25).setHardCap(600), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(DAMAGE_LOSS, CRIT_DAMAGE, COUNTER_MULTIPLIER);
		setCurse();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof TridentItem;
	}
}