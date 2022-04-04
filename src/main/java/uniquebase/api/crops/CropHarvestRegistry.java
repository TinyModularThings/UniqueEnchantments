package uniquebase.api.crops;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class CropHarvestRegistry
{
	public static CropHarvestRegistry INSTANCE = new CropHarvestRegistry();
	Map<Block, ICropHarvest> harvesters = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
	
	public void init()
	{
		for(Block block : ForgeRegistries.BLOCKS)
		{
			if(block instanceof CropsBlock)
			{
				register(block, new CropsHarvester((CropsBlock)block));
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
	
	public ActionResultType tryHarvest(BlockState state, World world, BlockPos pos, PlayerEntity player)
	{
		ICropHarvest harvest = harvesters.get(state.getBlock());
		if(harvest != null)
		{
			ActionResult<ItemStack> result = harvest.harvest(state, world, pos);
			if(result.getResult() == ActionResultType.FAIL)
			{
				return ActionResultType.FAIL;
			}
			if(result.getResult() == ActionResultType.PASS)
			{
				return ActionResultType.PASS;
			}
			ItemStack stack = result.getObject().copy();
			if(!stack.isEmpty())
			{
				stack.setCount(1);
			}
			player.getEnderChestInventory().addItem(stack);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}
	
}
