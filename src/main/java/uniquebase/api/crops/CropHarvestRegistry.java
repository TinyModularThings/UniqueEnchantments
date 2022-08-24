package uniquebase.api.crops;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class CropHarvestRegistry
{
	public static CropHarvestRegistry INSTANCE = new CropHarvestRegistry();
	Map<Block, ICropHarvest> harvesters = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
	
	public void init()
	{
		for(Block block : ForgeRegistries.BLOCKS)
		{
			if(block instanceof CropBlock)
			{
				register(block, new CropsHarvester((CropBlock)block));
			}
		}
	}
	
	public void register(Block block, ICropHarvest harvest)
	{
		harvesters.put(block, harvest);
	}
	
	public boolean isValid(Block block)
	{
		return harvesters.containsKey(block);
	}
	
	public InteractionResult tryHarvest(BlockState state, Level world, BlockPos pos, Player player)
	{
		ICropHarvest harvest = harvesters.get(state.getBlock());
		if(harvest != null)
		{
			InteractionResultHolder<ItemStack> result = harvest.harvest(state, world, pos);
			if(result.getResult() == InteractionResult.FAIL)
			{
				return InteractionResult.FAIL;
			}
			if(result.getResult() == InteractionResult.PASS)
			{
				return InteractionResult.PASS;
			}
			ItemStack stack = result.getObject().copy();
			if(!stack.isEmpty())
			{
				stack.setCount(1);
			}
			player.getEnderChestInventory().addItem(stack);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}
	
}
