package uniquebase.networking;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import net.minecraftforge.network.simple.SimpleChannel;
import uniquebase.api.BaseUEMod;

public class PacketHandler
{
	public final String version = "1.0.0";
	SimpleChannel channel;
	
	public PacketHandler()
	{
		channel = NetworkRegistry.newSimpleChannel(new ResourceLocation("uniquebase", "networking"), () -> version, version::equals, version::equals);
		registerPacket(EntityPacket.class, EntityPacket::new, 0);
		registerPacket(KeyPacket.class, KeyPacket::new, 1);
	}
	
	public <T extends IUEPacket> void registerInternalPacket(BaseUEMod mod, Class<T> packet, Supplier<T> creator, int index)
	{
		if(!BaseUEMod.containsMod(mod)) throw new IllegalStateException("A non UE Addon tried registering a Internal Network packet. Not allowed since Ids are hardcoded and have to be the same");
		registerPacket(packet, creator, index);
	}
	
	private <T extends IUEPacket> void registerPacket(Class<T> packet, Supplier<T> creator, int index)
	{
		channel.registerMessage(index, packet, this::writePacket, (K) -> readPacket(K, creator), this::handlePacket);
	}
	
	protected void writePacket(IUEPacket packet, FriendlyByteBuf buffer)
	{
		try
		{
			packet.write(buffer);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected <T extends IUEPacket> T readPacket(FriendlyByteBuf buffer, Supplier<T> values)
	{
		try
		{
			T packet = values.get();
			packet.read(buffer);
			return packet;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	protected void handlePacket(IUEPacket packet, Supplier<NetworkEvent.Context> provider)
	{
		Context context = provider.get();
		try
		{
			Player player = getPlayer(context);
			context.enqueueWork(() -> {
				packet.handlePacket(player);
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		context.setPacketHandled(true);
	}
	
	private Player getPlayer(Context context)
	{
		Player player = context.getSender();
		return player == null ? getClientPlayer() : player;
	}
	
	@OnlyIn(Dist.CLIENT)
	private Player getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}
	
	public void sendToServer(IUEPacket packet)
	{
		channel.send(PacketDistributor.SERVER.noArg(), packet);
	}
	
	public void sendToPlayer(IUEPacket packet, Player player)
	{
		if(!(player instanceof ServerPlayer))
		{
			throw new RuntimeException("Sending a Packet to a Player from client is not allowed");
		}
		channel.send(PacketDistributor.PLAYER.with(() -> ((ServerPlayer)player)), packet);
	}
	
	public void sendToAllPlayers(IUEPacket packet)
	{
		channel.send(PacketDistributor.ALL.noArg(), packet);
	}
	
	public void sendToAllChunkWatchers(LevelChunk chunk, IUEPacket packet)
	{
		channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
	}
	
	public void sendToAllEntityWatchers(Entity entity, IUEPacket packet)
	{
		channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), packet);
	}
	
	public void sendToNearby(TargetPoint point, IUEPacket packet)
	{
		channel.send(PacketDistributor.NEAR.with(() -> point), packet);
	}
}