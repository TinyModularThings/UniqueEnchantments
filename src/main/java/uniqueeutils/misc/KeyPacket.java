package uniqueeutils.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import uniquebase.networking.IUEPacket;
import uniqueeutils.UniqueEnchantmentsUtils;

public class KeyPacket implements IUEPacket
{
	boolean pressed;
	
	public KeyPacket()
	{
	}
	
	public KeyPacket(boolean pressed)
	{
		this.pressed = pressed;
	}

	@Override
	public void write(ByteBuf buf)
	{
		buf.writeBoolean(pressed);
	}

	@Override
	public void read(ByteBuf buf)
	{
		pressed = buf.readBoolean();
	}

	@Override
	public void handlePacket(EntityPlayer player)
	{
		UniqueEnchantmentsUtils.PROXY.updateData(player, pressed);
	}
	
}
