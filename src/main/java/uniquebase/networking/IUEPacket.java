package uniquebase.networking;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public interface IUEPacket
{
	public void write(PacketBuffer buf);
	public void read(PacketBuffer buf);
	public void handlePacket(EntityPlayer player);
}
