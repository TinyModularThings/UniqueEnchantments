package uniqueapex.handler.structure;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import uniqueapex.utils.AnimationUtils;

public class RecipeAnimator
{
	int tick = 0;
	BlockPos pos;
	List<EnderCrystalEntity> endCrystals;
	List<Vector3d> basePositions;
	float prevRotation;
	float nextRotation;
	
	public RecipeAnimator(BlockPos pos, List<EnderCrystalEntity> endCrystals, List<Vector3d> basePositions, int tick)
	{
		this.pos = pos;
		this.endCrystals = endCrystals;
		this.basePositions = basePositions;
		this.tick = tick;
		for(int i = 0,m=tick-40;i<m;i++) {
			prevRotation = nextRotation;
			nextRotation += (float)Math.toRadians(AnimationUtils.getRotation(i+40));			
		}
	}
	
	public void tick()
	{
		tick++;
		if(tick == 240)
		{
			World level = Minecraft.getInstance().level;
			level.playSound(Minecraft.getInstance().player, pos, SoundEvents.GENERIC_EXPLODE, SoundCategory.MASTER, 1F, 1F);
		}
		if(tick < 40) return;
		prevRotation = nextRotation;
		nextRotation += (float)Math.toRadians(AnimationUtils.getRotation(tick));
	}
	
	public void render(World world, float frameTime)
	{
		float closer = MathHelper.lerp(frameTime, AnimationUtils.getCloser(tick-1), AnimationUtils.getCloser(tick));
		float yOffset = MathHelper.lerp(frameTime, AnimationUtils.getYOffset(tick-1), AnimationUtils.getYOffset(tick));
		float masterY = MathHelper.lerp(frameTime, AnimationUtils.getMasterY(tick-1), AnimationUtils.getMasterY(tick));
		for(int i = 0;i<4;i++)
		{
			Direction dir = Direction.from2DDataValue(i).getOpposite();
			EnderCrystalEntity entity = endCrystals.get(i+1);
			Vector3d base = basePositions.get(i).add(dir.getStepX() * closer, yOffset, dir.getStepZ() * closer).subtract(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			Vector3d prevPos = base.yRot(prevRotation).add(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			Vector3d nextPos = base.yRot(nextRotation).add(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			entity.setPosAndOldPos(MathHelper.lerp(frameTime, prevPos.x(), nextPos.x()), MathHelper.lerp(frameTime, prevPos.y(), nextPos.y()), MathHelper.lerp(frameTime, prevPos.z(), nextPos.z()));
		}
		EnderCrystalEntity entity = endCrystals.get(0);
		entity.setPosAndOldPos(entity.getX(), pos.getY() + 3.5D + masterY, entity.getZ());
	}

	public boolean isDone()
	{
		return tick >= 260;
	}
}
