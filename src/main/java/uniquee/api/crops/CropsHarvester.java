package uniquee.api.crops;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CropsHarvester implements ICropHarvest
{
	BlockCrops crop;
	
	public CropsHarvester(BlockCrops crop)
	{
		this.crop = crop;
	}
	
	@Override
	public ActionResult<ItemStack> harvest(IBlockState state, World world, BlockPos pos)
	{
		if(state.getBlock() != crop)
		{
			return ActionResult.newResult(EnumActionResult.FAIL, ItemStack.EMPTY);
		}
		if(crop.isMaxAge(state))
		{
	        NonNullList<ItemStack> drops = NonNullList.create();
	        crop.getDrops(drops, world, pos, state, 0);
			world.setBlockState(pos, crop.withAge(0));
			return ActionResult.newResult(EnumActionResult.SUCCESS, drops.isEmpty() ? ItemStack.EMPTY : drops.get(0));
		}
		return ActionResult.newResult(EnumActionResult.PASS, ItemStack.EMPTY);
	}
	
}
