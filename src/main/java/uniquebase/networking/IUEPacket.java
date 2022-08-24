package uniquebase.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public interface IUEPacket
{
	public void write(FriendlyByteBuf buf);
	public void read(FriendlyByteBuf buf);
	public void handlePacket(Player player);
}
