package uniquee.api.crops;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CropsHarvester implements ICropHarvest
{
	CropsBlock crop;
	
	public CropsHarvester(CropsBlock crop)
	{
		this.crop = crop;
	}
	
	@Override
	public ActionResult<ItemStack> harvest(BlockState state, World world, BlockPos pos)
	{
		if(state.getBlock() != crop)
		{
			return ActionResult.resultFail(ItemStack.EMPTY);
		}
		if(crop.isMaxAge(state))
		{
			List<ItemStack> drops = Block.getDrops(state, (ServerWorld)world, pos, null);
			world.setBlockState(pos, crop.withAge(0));
			return ActionResult.resultSuccess(drops.isEmpty() ? ItemStack.EMPTY : drops.get(0));
		}
		return ActionResult.resultPass(ItemStack.EMPTY);
	}
	
}
