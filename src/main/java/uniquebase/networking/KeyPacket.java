	package uniquebase.networking;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import uniquebase.UEBase;

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
	public void write(FriendlyByteBuf buf)
	{
		buf.writeVarInt(keyState.size());
		for(Object2BooleanMap.Entry<String> entry : keyState.object2BooleanEntrySet())
		{
			buf.writeUtf(entry.getKey());
			buf.writeBoolean(entry.getBooleanValue());
		}
	}

	@Override
	public void read(FriendlyByteBuf buf)
	{
		int size = buf.readVarInt();
		for(int i = 0;i<size;i++)
		{
			keyState.put(buf.readUtf(32767), buf.readBoolean());
		}
	}

	@Override
	public void handlePacket(Player player)
	{
		UEBase.PROXY.updateData(player, keyState);
	}
	
}
