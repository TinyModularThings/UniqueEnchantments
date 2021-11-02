package uniquebase.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public interface IUEPacket
{
	public void write(ByteBuf buf);
	public void read(ByteBuf buf);
	public void handlePacket(EntityPlayer player);
}
