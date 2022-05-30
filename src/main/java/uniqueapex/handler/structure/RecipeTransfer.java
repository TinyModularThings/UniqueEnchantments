package uniqueapex.handler.structure;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class RecipeTransfer
{
	BlockPos pos;
	int[] entityIds;
	List<Vector3d> basePositions;
	int tick;
	
	public RecipeTransfer(BaseTrackedRecipe recipe)
	{
		pos = recipe.getPos();
		entityIds = recipe.getEntities();
		basePositions = recipe.getBasePosition();
		tick = recipe.getTick();
	}
	
	public RecipeTransfer(PacketBuffer buffer)
	{
		pos = BlockPos.of(buffer.readLong());
		entityIds = buffer.readVarIntArray();
		basePositions = new ObjectArrayList<>();
		for(int i = 0,m=buffer.readVarInt();i<m;i++) {
			basePositions.add(new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
		}
		tick = buffer.readVarInt();
	}
	
	public RecipeAnimator asAnimation(World world) {
		List<EnderCrystalEntity> newList = new ObjectArrayList<>();
		for(int i = 0;i<entityIds.length;i++) {
			newList.add((EnderCrystalEntity)world.getEntity(entityIds[i]));
		}
		return new RecipeAnimator(pos, newList, basePositions, tick);
	}
	
	public void write(PacketBuffer buffer)
	{
		buffer.writeLong(pos.asLong());
		buffer.writeVarIntArray(entityIds);
		buffer.writeVarInt(basePositions.size());
		for(Vector3d entry : basePositions) {
			buffer.writeDouble(entry.x());
			buffer.writeDouble(entry.y());
			buffer.writeDouble(entry.z());
		}
		buffer.writeVarInt(tick);
	}
}
