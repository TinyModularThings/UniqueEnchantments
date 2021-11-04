package uniquebase.networking;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class EntityPacket implements IUEPacket
{
	int entityId;
	NBTTagCompound data = new NBTTagCompound();
	
	public EntityPacket()
	{
	}
	
	public EntityPacket(int entityId, NBTTagCompound data)
	{
		this.entityId = entityId;
		this.data = data;
	}

	@Override
	public void write(PacketBuffer buf)
	{
		buf.writeInt(entityId);
		buf.writeCompoundTag(data);
	}
	
	@Override
	public void read(PacketBuffer buf)
	{
		entityId = buf.readInt();
		try { data = buf.readCompoundTag(); }
		catch(IOException e) {}
	}
	
	@Override
	public void handlePacket(EntityPlayer player)
	{
		Entity entity = player.world.getEntityByID(entityId);
		if(entity == null) return;
		entity.getEntityData().merge(data);
	}
}