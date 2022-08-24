package uniqueapex.handler.structure;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RecipeTransfer
{
	BlockPos pos;
	int[] entityIds;
	List<Vec3> basePositions;
	int tick;
	
	public RecipeTransfer(BaseTrackedRecipe recipe)
	{
		pos = recipe.getPos();
		entityIds = recipe.getEntities();
		basePositions = recipe.getBasePosition();
		tick = recipe.getTick();
	}
	
	public RecipeTransfer(FriendlyByteBuf buffer)
	{
		pos = BlockPos.of(buffer.readLong());
		entityIds = buffer.readVarIntArray();
		basePositions = new ObjectArrayList<>();
		for(int i = 0,m=buffer.readVarInt();i<m;i++) {
			basePositions.add(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
		}
		tick = buffer.readVarInt();
	}
	
	public RecipeAnimator asAnimation(Level world) {
		List<EndCrystal> newList = new ObjectArrayList<>();
		for(int i = 0;i<entityIds.length;i++) {
			newList.add((EndCrystal)world.getEntity(entityIds[i]));
		}
		return new RecipeAnimator(pos, newList, basePositions, tick);
	}
	
	public void write(FriendlyByteBuf buffer)
	{
		buffer.writeLong(pos.asLong());
		buffer.writeVarIntArray(entityIds);
		buffer.writeVarInt(basePositions.size());
		for(Vec3 entry : basePositions) {
			buffer.writeDouble(entry.x());
			buffer.writeDouble(entry.y());
			buffer.writeDouble(entry.z());
		}
		buffer.writeVarInt(tick);
	}
}
