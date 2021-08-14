package uniquee.enchantments.complex;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.ResourceLocation;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.IntLevelStats;

public class SmartAss extends UniqueEnchantment
{
	public static final IntLevelStats RANGE = new IntLevelStats("range", 2, 3);
	public static final INamedTag<Block> SMART_ASS = BlockTags.createOptional(new ResourceLocation("uniquee", "smart_ass"));

	public static final Predicate<BlockState> VALID_STATES = new Predicate<BlockState>(){
		@Override
		public boolean test(BlockState t)
		{
			Block block = t.getBlock();
			return BlockTags.LOGS.contains(block) || block instanceof FallingBlock || SMART_ASS.contains(block);
		}
	};
	
	public SmartAss()
	{
		super(new DefaultData("smart_ass", Rarity.RARE, 3, false, true, 28, 6, 40), EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND);
		addStats(RANGE);
	}
}
