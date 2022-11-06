package uniqueapex.handler.misc;

import java.util.Iterator;

import it.unimi.dsi.fastutil.objects.AbstractObject2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;

public class MiningArea
{
	Iterator<BlockPos> progress;
	Object2FloatMap.Entry<BlockPos> stuckPos = null;
	float miningSpeed;
	float mineProgress = 0F;
	
	public MiningArea(BlockPos from, BlockPos to, float miningSpeed)
	{
		progress = new Walker(from, to);
		this.miningSpeed = miningSpeed;
	}


	public boolean mine(World world)
	{
		mineProgress += miningSpeed;
		if(stuckPos != null) {
			if(stuckPos.getFloatValue() <= mineProgress) {
				world.setBlockAndUpdate(stuckPos.getKey(), Blocks.AIR.defaultBlockState());
				mineProgress -= stuckPos.getFloatValue();
				stuckPos = null;
			}
			else return false;
		}
		for(int i = 0;i<5;i++)
		{
			if(!progress.hasNext()) return true;
			BlockPos pos = progress.next();
			BlockState state = world.getBlockState(pos);
			if(state.getBlock().isAir(state, world, pos)) continue;
			float resistance = state.getDestroySpeed(world, pos);
			if(resistance < 0) continue;
			if(mineProgress >= resistance) {
				world.setBlockAndUpdate(pos.immutable(), Blocks.AIR.defaultBlockState());
				mineProgress -= resistance;
			}
			else {
				stuckPos = new AbstractObject2FloatMap.BasicEntry<>(pos.immutable(), resistance);
				break;
			}
		}
		return !progress.hasNext();
	}
	
	public static class Walker implements Iterator<BlockPos>
	{
		Mutable pos = new Mutable();
		int minX;
		int minY;
		int minZ;
		int maxX;
		int maxY;
		int maxZ;
		int x;
		int y;
		int z;
		boolean hasNext = true;
		
		public Walker(BlockPos from, BlockPos to)
		{
			this.minX = Math.min(from.getX(), to.getX());
			this.minY = Math.min(from.getY(), to.getY());
			this.minZ = Math.min(from.getZ(), to.getZ());
			this.x = this.maxX = Math.max(from.getX(), to.getX());
			this.y = this.maxY = Math.max(from.getY(), to.getY());
			this.z = this.maxZ = Math.max(from.getZ(), to.getZ());
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public BlockPos next()
		{
			pos.set(x, y, z);
			if(--x < minX)
			{
				x = maxX;
				if(--z < minZ)
				{
					z = maxZ;
					if(--y < minY)
					{
						y = maxY;
						hasNext = false;
					}
				}
			}
			return pos;
		}
	}
}
