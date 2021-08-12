package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.ResourceLocation;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.IntLevelStats;

public class IfritsJudgement extends UniqueEnchantment
{
	public static final String FLAG_JUDGEMENT_ID = "judge_id";
	public static final String FLAG_JUDGEMENT_COUNT = "judge_count";
	public static final String FLAG_JUDGEMENT_LOOT = "judge_loot";
	public static final ResourceLocation JUDGEMENT_LOOT = new ResourceLocation("uniqueebattle", "judge_loot");

	public static final DoubleLevelStats FIRE_DAMAGE = new DoubleLevelStats("fire_damage", 0.4D, 0.1D);
	public static final DoubleLevelStats LAVA_DAMAGE = new DoubleLevelStats("lava_damage", 0.4D, 0.1D);
	public static final IntLevelStats DURATION = new IntLevelStats("burn_duration", 30, 10);
	
	public IfritsJudgement()
	{
		super(new DefaultData("ifrits_judgement", Rarity.COMMON, 2, false, 10, 4, 40), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		setCategory("battle");
		setCurse();
		addStats(FIRE_DAMAGE, LAVA_DAMAGE, DURATION);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof ToolItem || stack.getItem() instanceof BowItem;
	}
}
