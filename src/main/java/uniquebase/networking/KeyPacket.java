	package uniquebase.networking;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import uniquebase.UniqueEnchantmentsBase;

public class KeyPacket implements IUEPacket
{
	Object2BooleanMap<String> keyState = new Object2BooleanOpenHashMap<>();
	
	public KeyPacket()
	{
	}
	
	public KeyPacket(Object2BooleanMap<String> keyState)
	{
		this.keyState = keyState;
	}

	@Override
	public void write(PacketBuffer buf)
	{
		buf.writeVarInt(keyState.size());
		for(Object2BooleanMap.Entry<String> entry : keyState.object2BooleanEntrySet())
		{
			buf.writeUtf(entry.getKey());
			buf.writeBoolean(entry.getBooleanValue());
		}
	}

	@Override
	public void read(PacketBuffer buf)
	{
		int size = buf.readVarInt();
		for(int i = 0;i<size;i++)
		{
			keyState.put(buf.readUtf(32767), buf.readBoolean());
		}
	}

	@Override
	public void handlePacket(PlayerEntity player)
	{
		UniqueEnchantmentsBase.PROXY.updateData(player, keyState);
	}
	
}
