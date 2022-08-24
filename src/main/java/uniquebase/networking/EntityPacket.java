package uniquebase.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EntityPacket implements IUEPacket
{
	int entityId;
	CompoundTag data = new CompoundTag();
	
	public EntityPacket()
	{
	}
	
	public EntityPacket(int entityId, CompoundTag data)
	{
		this.entityId = entityId;
		this.data = data;
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeInt(entityId);
		buf.writeNbt(data);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		entityId = buf.readInt();
		data = buf.readNbt(); 
	}
	
	@Override
	public void handlePacket(Player player)
	{
		Entity entity = player.level.getEntity(entityId);
		if(entity == null) return;
		entity.getPersistentData().merge(data);
	}
}