package uniquebase.networking;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import uniquebase.api.BaseUEMod;

public class PacketHandler
{
	public final String version = "1.0.0";
	SimpleChannel channel;
	
	public PacketHandler()
	{
		channel = NetworkRegistry.newSimpleChannel(new ResourceLocation("uniquebase", "networking"), () -> version, version::equals, version::equals);
		registerPacket(EntityPacket.class, EntityPacket::new, 0);
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
	
	protected void writePacket(IUEPacket packet, PacketBuffer buffer)
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
	
	protected <T extends IUEPacket> T readPacket(PacketBuffer buffer, Supplier<T> values)
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
		try
		{
			Context context = provider.get();
			PlayerEntity player = context.getSender();
			if(player == null) return;
			context.enqueueWork(() -> {
				packet.handlePacket(player);
				context.setPacketHandled(true);
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void sendToServer(IUEPacket packet)
	{
		channel.send(PacketDistributor.SERVER.noArg(), packet);
	}
	
	public void sendToPlayer(IUEPacket packet, PlayerEntity player)
	{
		if(!(player instanceof ServerPlayerEntity))
		{
			throw new RuntimeException("Sending a Packet to a Player from client is not allowed");
		}
		channel.send(PacketDistributor.PLAYER.with(() -> ((ServerPlayerEntity)player)), packet);
	}
	
	public void sendToAllPlayers(IUEPacket packet)
	{
		channel.send(PacketDistributor.ALL.noArg(), packet);
	}
	
	public void sendToAllChunkWatchers(Chunk chunk, IUEPacket packet)
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