package uniqueeutils.misc;

import net.minecraft.entity.player.EntityPlayer;
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
		this.pos = pos.toLong();
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
	public void handlePacket(EntityPlayer player)
	{
		UtilsHandler.INSTANCE.addDrawPosition(BlockPos.fromLong(pos));
	}
	
}
