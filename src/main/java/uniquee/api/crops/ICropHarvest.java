package uniquee.api.crops;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICropHarvest
{	
	public ActionResult<ItemStack> harvest(BlockState state, World world, BlockPos pos);
}
