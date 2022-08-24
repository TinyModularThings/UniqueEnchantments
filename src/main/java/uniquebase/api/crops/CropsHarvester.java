package uniquebase.api.crops;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CropsHarvester implements ICropHarvest
{
	CropBlock crop;
	
	public CropsHarvester(CropBlock crop)
	{
		this.crop = crop;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> harvest(BlockState state, Level world, BlockPos pos)
	{
		if(state.getBlock() != crop)
		{
			return InteractionResultHolder.fail(ItemStack.EMPTY);
		}
		if(crop.isMaxAge(state))
		{
			List<ItemStack> drops = Block.getDrops(state, (ServerLevel)world, pos, null);
			world.setBlockAndUpdate(pos, crop.getStateForAge(0));
			return InteractionResultHolder.success(drops.isEmpty() ? ItemStack.EMPTY : drops.get(0));
		}
		return InteractionResultHolder.pass(ItemStack.EMPTY);
	}
	
}
