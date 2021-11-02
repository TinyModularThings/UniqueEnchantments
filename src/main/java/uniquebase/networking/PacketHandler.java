package uniquebase.networking;

import java.util.EnumMap;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.FMLOutboundHandler.OutboundTarget;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uniquebase.api.BaseUEMod;

@Sharable
public class PacketHandler extends SimpleChannelInboundHandler<IUEPacket>
{
	EnumMap<Side, FMLEmbeddedChannel> channel;
	ChannelManager packetRegistry = new ChannelManager();
	
	
	public PacketHandler()
	{
		channel = NetworkRegistry.INSTANCE.newChannel("ue", packetRegistry, this);
	}
	
	public void registerInternalPacket(BaseUEMod mod, Class<? extends IUEPacket> packet, int id)
	{
		if(mod == null || !BaseUEMod.containsMod(mod)) throw new IllegalStateException("A non UE Addon tried registering a Internal Network packet. Not allowed since Ids are hardcoded and have to be the same");
		packetRegistry.addDiscriminator(id, packet);
	}
	
	public void sendToPlayer(IUEPacket par1, EntityPlayer par2)
	{
		if(!(par2 instanceof EntityPlayerMP))
		{
			FMLLog.log.info("UE Networking: Invalid Player Found: "+par2);
			return;
		}
		channel.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		channel.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(par2);
		channel.get(Side.SERVER).writeOutbound(par1);
	}
	
	public void sendToServer(IUEPacket par1)
	{
		channel.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.TOSERVER);
		channel.get(Side.CLIENT).writeOutbound(par1);
	}
	
	public void sendToAllPlayers(IUEPacket par1)
	{
		channel.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		channel.get(Side.SERVER).writeOutbound(par1);
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IUEPacket msg) throws Exception
	{
		INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
		final EntityPlayer player = getPlayer(netHandler);
        IThreadListener thread = FMLCommonHandler.instance().getWorldThread(netHandler);
        if(!thread.isCallingFromMinecraftThread())
        {
        	thread.addScheduledTask(() -> handleMessage(msg, player));
        	return;
        }
        handleMessage(msg, player);
	}
	
	private void handleMessage(IUEPacket packet, EntityPlayer player)
	{
		try
		{
			packet.handlePacket(player);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private EntityPlayer getPlayer(INetHandler handler)
	{
		if(handler instanceof NetHandlerPlayServer)
		{
			return ((NetHandlerPlayServer)handler).player;
		}
		return getClientPlayer();
	}
	
	@SideOnly(Side.CLIENT)
	private EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().player;
	}
}