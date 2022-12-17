package uniquee.enchantments.unique;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;

public class MidasBlessing extends UniqueEnchantment
{
	public static final ToIntFunction<ItemStack> VALIDATOR = new ToIntFunction<ItemStack>(){
		@Override
		public int applyAsInt(ItemStack value)
		{
			return value.is(Tags.Items.INGOTS_GOLD) ? 1 : 0;
		}
	};
	public static final Predicate<BlockState> IS_GEM = new Predicate<BlockState>(){
		@Override
		public boolean test(BlockState t)
		{
			return t.is(MIDIAS) || t.is(Tags.Blocks.ORES);
		}
	};
	public static TagKey<Block> MIDIAS = BlockTags.create(new ResourceLocation("uniquee", "midias_blessing"));
	public static String GOLD_COUNTER = "gold_storage";
	public static final DoubleLevelStats GOLD_COST = new DoubleLevelStats("gold_cost", 1D, 1D);
	
	public MidasBlessing()
	{
		super(new DefaultData("midas_blessing", Rarity.VERY_RARE, 3, true, false, 14, 6, 75), EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND);
		addStats(GOLD_COST);
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof PickaxeItem);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.BLOCK_FORTUNE, Enchantments.SILK_TOUCH);
	}
}
