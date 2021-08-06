package uniquebase.api.crops;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class CropHarvestRegistry
{
	public static CropHarvestRegistry INSTANCE = new CropHarvestRegistry();
	Map<Block, ICropHarvest> harvesters = new Object2ObjectOpenHashMap<Block, ICropHarvest>();
	
	public void init()
	{
		for(Block block : ForgeRegistries.BLOCKS)
		{
			if(block instanceof BlockCrops)
			{
				register(block, new CropsHarvester((BlockCrops)block));
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
	
	public EnumActionResult tryHarvest(IBlockState state, World world, BlockPos pos, EntityPlayer player)
	{
		ICropHarvest harvest = harvesters.get(state.getBlock());
		if(harvest != null)
		{
			ActionResult<ItemStack> result = harvest.harvest(state, world, pos);
			if(result.getType() == EnumActionResult.FAIL)
			{
				return EnumActionResult.FAIL;
			}
			if(result.getType() == EnumActionResult.PASS)
			{
				return EnumActionResult.PASS;
			}
			ItemStack stack = result.getResult().copy();
			if(!stack.isEmpty())
			{
				stack.setCount(1);
			}
			player.getInventoryEnderChest().addItem(stack);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}
	
}
