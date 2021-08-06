package uniquebase.api.crops;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICropHarvest
{	
	public ActionResult<ItemStack> harvest(IBlockState state, World world, BlockPos pos);
}
