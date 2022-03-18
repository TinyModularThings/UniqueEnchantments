package uniqueapex.network;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import uniqueapex.handler.structure.ClientRecipeStorage;
import uniqueapex.handler.structure.RecipeTransfer;
import uniquebase.networking.IUEPacket;

public class SyncRecipePacket implements IUEPacket
{
	List<RecipeTransfer> recipes;
	
	public SyncRecipePacket()
	{
		this(new ObjectArrayList<>());
	}
	
	public SyncRecipePacket(List<RecipeTransfer> recipes)
	{
		this.recipes = recipes;
	}

	@Override
	public void write(PacketBuffer buf)
	{
		buf.writeVarInt(recipes.size());
		for(RecipeTransfer transfer : recipes) {
			transfer.write(buf);
		}
	}
	
	@Override
	public void read(PacketBuffer buf)
	{
		for(int i = 0,m=buf.readVarInt();i<m;i++) {
			recipes.add(new RecipeTransfer(buf));
		}
	}
	
	@Override
	public void handlePacket(PlayerEntity player)
	{
		for(RecipeTransfer transfer : recipes)
		{
			ClientRecipeStorage.INSTANCE.addAnimation(transfer.asAnimation(player.level));
		}
	}
	
}
