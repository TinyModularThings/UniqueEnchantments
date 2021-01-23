package uniquee.enchantments.complex;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.IntLevelStats;

public class SmartAssEnchantment extends UniqueEnchantment
{
	public static final IntLevelStats STATS = new IntLevelStats("range", 2, 3);
	public static final Tag<Block> SMART_ASS = new BlockTags.Wrapper(new ResourceLocation("uniquee", "smart_ass"));

	public static final Predicate<BlockState> VALID_STATES = new Predicate<BlockState>(){
		@Override
		public boolean test(BlockState t)
		{
			Block block = t.getBlock();
			return BlockTags.LOGS.contains(block) || block instanceof FallingBlock || SMART_ASS.contains(block);
		}
	};
	
	public SmartAssEnchantment()
	{
		super(new DefaultData("smart_ass", Rarity.RARE, 3, false, 28, 6, 40), EnchantmentType.DIGGER, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
	
	@Override
	public void loadData(Builder config)
	{
		STATS.handleConfig(config);
	}
}
