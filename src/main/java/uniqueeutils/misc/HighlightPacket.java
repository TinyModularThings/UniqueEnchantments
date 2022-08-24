package uniqueeutils.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import uniquebase.networking.IUEPacket;
import uniqueeutils.handler.UtilsHandler;

public class HighlightPacket implements IUEPacket
{
	long pos;
	
	public HighlightPacket()
	{
	}
	
	public HighlightPacket(BlockPos pos)
	{
		this.pos = pos.asLong();
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeLong(pos);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		pos = buf.readLong();
	}
	
	@Override
	public void handlePacket(Player player)
	{
		UtilsHandler.INSTANCE.addDrawPosition(BlockPos.of(pos));
	}
	
}
