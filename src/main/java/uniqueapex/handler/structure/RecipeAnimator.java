package uniqueapex.handler.structure;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import uniqueapex.utils.AnimationUtils;

public class RecipeAnimator
{
	int tick = 0;
	BlockPos pos;
	List<EndCrystal> endCrystals;
	List<Vec3> basePositions;
	float prevRotation;
	float nextRotation;
	
	public RecipeAnimator(BlockPos pos, List<EndCrystal> endCrystals, List<Vec3> basePositions, int tick)
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
			Level level = Minecraft.getInstance().level;
			level.playSound(Minecraft.getInstance().player, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1F, 1F);
		}
		if(tick < 40) return;
		prevRotation = nextRotation;
		nextRotation += (float)Math.toRadians(AnimationUtils.getRotation(tick));
	}
	
	public void render(Level world, float frameTime)
	{
		float closer = Mth.lerp(frameTime, AnimationUtils.getCloser(tick-1), AnimationUtils.getCloser(tick));
		float yOffset = Mth.lerp(frameTime, AnimationUtils.getYOffset(tick-1), AnimationUtils.getYOffset(tick));
		float masterY = Mth.lerp(frameTime, AnimationUtils.getMasterY(tick-1), AnimationUtils.getMasterY(tick));
		for(int i = 0;i<4;i++)
		{
			Direction dir = Direction.from2DDataValue(i).getOpposite();
			EndCrystal entity = endCrystals.get(i+1);
			Vec3 base = basePositions.get(i).add(dir.getStepX() * closer, yOffset, dir.getStepZ() * closer).subtract(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			Vec3 prevPos = base.yRot(prevRotation).add(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			Vec3 nextPos = base.yRot(nextRotation).add(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			entity.absMoveTo(Mth.lerp(frameTime, prevPos.x(), nextPos.x()), Mth.lerp(frameTime, prevPos.y(), nextPos.y()), Mth.lerp(frameTime, prevPos.z(), nextPos.z()));
		}
		EndCrystal entity = endCrystals.get(0);
		entity.absMoveTo(entity.getX(), pos.getY() + 3.5D + masterY, entity.getZ());
	}

	public boolean isDone()
	{
		return tick >= 260;
	}
}
