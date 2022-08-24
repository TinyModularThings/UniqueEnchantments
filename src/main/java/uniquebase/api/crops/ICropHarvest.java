package uniquebase.api.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ICropHarvest
{	
	public InteractionResultHolder<ItemStack> harvest(BlockState state, Level world, BlockPos pos);
}
