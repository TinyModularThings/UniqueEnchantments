package uniqueeutils.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
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
	public void write(PacketBuffer buf)
	{
		buf.writeLong(pos);
	}
	
	@Override
	public void read(PacketBuffer buf)
	{
		pos = buf.readLong();
	}
	
	@Override
	public void handlePacket(PlayerEntity player)
	{
		UtilsHandler.INSTANCE.addDrawPosition(BlockPos.of(pos));
	}
	
}
