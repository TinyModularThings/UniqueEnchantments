package uniquee.utils;

import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uniquee.api.crops.CropHarvestRegistry;

public class HarvestEntry
{
	int dim;
	long position;
	
	public HarvestEntry(NBTTagCompound nbt)
	{
		this(nbt.getInteger("dim"), nbt.getLong("pos"));
	}
	
	public HarvestEntry(int dim, long position)
	{
		this.dim = dim;
		this.position = position;
	}
	
	public boolean matches(NBTTagCompound nbt)
	{
		return dim == nbt.getInteger("dim") && position == nbt.getLong("pos");
	}
	
	public NBTTagCompound save()
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("dim", dim);
		compound.setLong("pos", position);
		return compound;
	}
	
	public EnumActionResult harvest(World world, EntityPlayer player)
	{
		if(world.provider.getDimension() != dim)
		{
			return EnumActionResult.PASS;
		}
		BlockPos pos = BlockPos.fromLong(position);
		return world.isBlockLoaded(pos) ? CropHarvestRegistry.INSTANCE.tryHarvest(world.getBlockState(pos), world, pos, player) : EnumActionResult.PASS;
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
