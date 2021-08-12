package uniquebase.utils;

import java.util.Objects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uniquebase.api.crops.CropHarvestRegistry;

public class HarvestEntry
{
	int dim;
	long position;
	
	public HarvestEntry(CompoundNBT nbt)
	{
		this(nbt.getInt("dim"), nbt.getLong("pos"));
	}
	
	public HarvestEntry(int dim, long position)
	{
		this.dim = dim;
		this.position = position;
	}
	
	public boolean matches(CompoundNBT nbt)
	{
		return dim == nbt.getInt("dim") && position == nbt.getLong("pos");
	}
	
	public CompoundNBT save()
	{
		CompoundNBT compound = new CompoundNBT();
		compound.putInt("dim", dim);
		compound.putLong("pos", position);
		return compound;
	}
	
	public ActionResultType harvest(World world, PlayerEntity player)
	{
		if(world.getDimension().getType().getId() != dim)
		{
			return ActionResultType.PASS;
		}
		BlockPos pos = BlockPos.fromLong(position);
		return world.isBlockPresent(pos) ? CropHarvestRegistry.INSTANCE.tryHarvest(world.getBlockState(pos), world, pos, player) : ActionResultType.PASS;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(position, dim);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof HarvestEntry)
		{
			HarvestEntry entry = (HarvestEntry)obj;
			return entry.dim == dim && entry.position == position;
		}
		return false;
	}
}
