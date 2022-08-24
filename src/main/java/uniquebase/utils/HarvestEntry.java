package uniquebase.utils;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import uniquebase.api.crops.CropHarvestRegistry;

public class HarvestEntry
{
	ResourceLocation dim;
	long position;
	
	public HarvestEntry(CompoundTag nbt)
	{
		this(new ResourceLocation(nbt.getString("dim")), nbt.getLong("pos"));
	}
	
	public HarvestEntry(ResourceLocation dim, long position)
	{
		this.dim = dim;
		this.position = position;
	}
	
	public boolean matches(CompoundTag nbt)
	{
		return dim.toString().equals(nbt.getString("dim")) && position == nbt.getLong("pos");
	}
	
	public CompoundTag save()
	{
		CompoundTag compound = new CompoundTag();
		compound.putString("dim", dim.toString());
		compound.putLong("pos", position);
		return compound;
	}
	
	public InteractionResult harvest(Level world, Player player)
	{
		if(!world.dimension().location().equals(dim))
		{
			return InteractionResult.PASS;
		}
		BlockPos pos = BlockPos.of(position);
		return world.isLoaded(pos) ? CropHarvestRegistry.INSTANCE.tryHarvest(world.getBlockState(pos), world, pos, player) : InteractionResult.PASS;
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
