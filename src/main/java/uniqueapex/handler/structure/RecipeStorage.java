package uniqueapex.handler.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import uniqueapex.network.SyncRecipePacket;
import uniquebase.UEBase;

public class RecipeStorage extends WorldSavedData
{
	List<TrackedRecipe> activeRecipes = new ObjectArrayList<>();
	Long2ObjectMap<Set<TrackedRecipe>> chunkRecipes = new Long2ObjectOpenHashMap<>();
	LongSet activeBeacons = new LongOpenHashSet();
	
	public RecipeStorage()
	{
		super("fusion_storage");
	}
	
	public static RecipeStorage get(ServerWorld world)
	{
		return world.getDataStorage().computeIfAbsent(RecipeStorage::new, "fusion_storage");
	}
	
	public boolean isInUse(BlockPos pos)
	{
		return activeBeacons.contains(pos.asLong());
	}
	
	public SyncRecipePacket getSyncPacket(ChunkPos pos) {
		Set<TrackedRecipe> recipes = chunkRecipes.get(pos.toLong());
		if(recipes == null || recipes.isEmpty()) return null;
		return new SyncRecipePacket(new ObjectArrayList<>(Lists.transform(new ObjectArrayList<>(recipes), RecipeTransfer::new)));
	}
	
	public void addRecipe(TrackedRecipe recipe)
	{
		addRecipe(recipe, true);
	}
	
	private void addRecipe(TrackedRecipe recipe, boolean notLoading)
	{
		activeRecipes.add(recipe);
		activeBeacons.add(recipe.getPos().asLong());
		chunkRecipes.computeIfAbsent(ChunkPos.asLong(recipe.getPos().getX() >> 4, recipe.getPos().getZ() >> 4), T -> new ObjectOpenHashSet<>()).add(recipe);
		if(notLoading)
		{
			setDirty();
			UEBase.NETWORKING.sendToAllChunkWatchers(recipe.getChunk(), new SyncRecipePacket(ObjectLists.singleton(new RecipeTransfer(recipe))));
		}
	}
	
	private void remove(TrackedRecipe recipe) {
		activeBeacons.remove(recipe.getPos().asLong());
		long pos = ChunkPos.asLong(recipe.getPos().getX() >> 4, recipe.getPos().getZ() >> 4);
		Set<TrackedRecipe> recipes = chunkRecipes.get(pos);
		if(recipes == null) return;
		if(recipes.remove(recipe) && recipes.isEmpty()) {
			chunkRecipes.remove(pos);
		}
	}
	
	public void onTick() {
		if(activeRecipes.isEmpty()) return;
		for(Iterator<TrackedRecipe> iter = activeRecipes.iterator();iter.hasNext();)
		{
			TrackedRecipe recipe = iter.next();
			recipe.tick();
			if(recipe.isDone()) {
				recipe.finishRecipe();
				recipe.remove();
				iter.remove();
				remove(recipe);
			}
		}
		setDirty();
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt)
	{
		ListNBT list = new ListNBT();
		for(TrackedRecipe recipe : activeRecipes) {
			list.add(recipe.save());
		}
		nbt.put("recipes", list);
		return nbt;
	}
	
	@Override
	public void load(CompoundNBT nbt)
	{
		ListNBT list = nbt.getList("recipes", 10);
		for(int i = 0,m=list.size();i<m;i++) {
			TrackedRecipe recipe = TrackedRecipe.loadRecipe(list.getCompound(i));
			if(recipe != null) addRecipe(recipe, false);
		}
	}
}
