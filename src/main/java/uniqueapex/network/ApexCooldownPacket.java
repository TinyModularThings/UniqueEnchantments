package uniqueapex.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import uniquebase.networking.IUEPacket;

public class ApexCooldownPacket implements IUEPacket
{
	int boost;
	
	public ApexCooldownPacket()
	{
	}

	public ApexCooldownPacket(int boost)
	{
		this.boost = boost;
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeVarInt(boost);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		boost = buf.readVarInt();
	}
	
	@Override
	public void handlePacket(Player player)
	{
		for(int i = 0;i<boost;i++) player.getCooldowns().tick();
	}
	
}
