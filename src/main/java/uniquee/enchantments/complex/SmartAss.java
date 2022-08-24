package uniquee.enchantments.complex;

import java.util.function.Predicate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.IntLevelStats;

public class SmartAss extends UniqueEnchantment
{
	public static final IntLevelStats RANGE = new IntLevelStats("range", 2, 3);
	public static final TagKey<Block> SMART_ASS = BlockTags.create(new ResourceLocation("uniquee", "smart_ass"));

	public static final Predicate<BlockState> VALID_STATES = new Predicate<BlockState>(){
		@Override
		public boolean test(BlockState t)
		{
			return t.is(BlockTags.LOGS) || t.getBlock() instanceof FallingBlock || t.is(SMART_ASS);
		}
	};
	
	public SmartAss()
	{
		super(new DefaultData("smart_ass", Rarity.RARE, 3, false, true, 28, 6, 40), EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND);
		addStats(RANGE);
	}
}
