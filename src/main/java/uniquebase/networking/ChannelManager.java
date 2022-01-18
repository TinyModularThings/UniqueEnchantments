package uniquebase.networking;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;

@Sharable
public class ChannelManager extends FMLIndexedMessageToMessageCodec<IUEPacket>
{
	@Override
	public void encodeInto(ChannelHandlerContext ctx, IUEPacket msg, ByteBuf target) throws Exception
	{
		try { msg.write(new PacketBuffer(target)); }
		catch(Exception e) { e.printStackTrace(); }
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, IUEPacket msg)
	{
		try { msg.read(new PacketBuffer(source)); }
		catch(Exception e) { e.printStackTrace(); }
	}
}