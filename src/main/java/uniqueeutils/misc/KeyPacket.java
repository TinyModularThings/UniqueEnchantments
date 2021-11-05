package uniqueeutils.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import uniquebase.networking.IUEPacket;
import uniqueeutils.UniqueEnchantmentsUtils;

public class KeyPacket implements IUEPacket
{
	int pressed;
	
	public KeyPacket()
	{
	}
	
	public KeyPacket(int pressed)
	{
		this.pressed = pressed;
	}

	@Override
	public void write(PacketBuffer buf)
	{
		buf.writeInt(pressed);
	}

	@Override
	public void read(PacketBuffer buf)
	{
		pressed = buf.readInt();
	}

	@Override
	public void handlePacket(PlayerEntity player)
	{
		UniqueEnchantmentsUtils.PROXY.updateData(player, pressed);
	}
	
}
