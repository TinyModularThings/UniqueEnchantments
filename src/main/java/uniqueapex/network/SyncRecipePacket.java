package uniqueapex.network;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
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
	public void write(FriendlyByteBuf buf)
	{
		buf.writeVarInt(recipes.size());
		for(RecipeTransfer transfer : recipes) {
			transfer.write(buf);
		}
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		for(int i = 0,m=buf.readVarInt();i<m;i++) {
			recipes.add(new RecipeTransfer(buf));
		}
	}
	
	@Override
	public void handlePacket(Player player)
	{
		for(RecipeTransfer transfer : recipes)
		{
			ClientRecipeStorage.INSTANCE.addAnimation(transfer.asAnimation(player.level));
		}
	}
	
}
