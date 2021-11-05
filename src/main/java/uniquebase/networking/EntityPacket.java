package uniquebase.networking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class EntityPacket implements IUEPacket
{
	int entityId;
	CompoundNBT data = new CompoundNBT();
	
	public EntityPacket()
	{
	}
	
	public EntityPacket(int entityId, CompoundNBT data)
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
		data = buf.readCompoundTag(); 
	}
	
	@Override
	public void handlePacket(PlayerEntity player)
	{
		Entity entity = player.world.getEntityByID(entityId);
		if(entity == null) return;
		entity.getPersistentData().merge(data);
	}
}