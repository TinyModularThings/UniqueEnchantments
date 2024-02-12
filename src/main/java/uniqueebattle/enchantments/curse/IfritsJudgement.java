package uniqueebattle.enchantments.curse;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.IntLevelStats;
import uniquebase.utils.IntStat;

public class IfritsJudgement extends UniqueEnchantment
{
	public static final String FLAG_JUDGEMENT_ID = "judge_id";
	public static final String FLAG_JUDGEMENT_COUNT = "judge_count";
	public static final String FLAG_JUDGEMENT_SUCCESS = "judge_success";
	public static final ResourceLocation JUDGEMENT_LOOT = new ResourceLocation("uniquebattle", "judge_loot");

	public static final DoubleLevelStats FIRE_DAMAGE = new DoubleLevelStats("fire_damage", 0.4D, 0.1D);
	public static final DoubleLevelStats LAVA_DAMAGE = new DoubleLevelStats("lava_damage", 0.4D, 0.1D);
	public static final IntLevelStats DURATION = new IntLevelStats("burn_duration", 30, 10);
	public static final IntStat LAVA_HITS = new IntStat(1, "lava_hits");
	
	public IfritsJudgement()
	{
		super(new DefaultData("ifrits_judgement", Rarity.RARE, 2, false, false, 10, 4, 40), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		setCategory("battle");
		setCurse();
		addStats(FIRE_DAMAGE, LAVA_DAMAGE, DURATION, LAVA_HITS);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof DiggerItem || stack.getItem() instanceof BowItem || stack.getItem() instanceof HoeItem;
	}
}
