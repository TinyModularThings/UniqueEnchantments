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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import uniqueapex.network.SyncRecipePacket;
import uniquebase.UEBase;

public class RecipeStorage extends SavedData
{
	List<BaseTrackedRecipe> activeRecipes = new ObjectArrayList<>();
	Long2ObjectMap<Set<BaseTrackedRecipe>> chunkRecipes = new Long2ObjectOpenHashMap<>();
	LongSet activeBeacons = new LongOpenHashSet();
	
	public RecipeStorage()
	{
	}
	
	public RecipeStorage(CompoundTag tag)
	{
		ListTag list = tag.getList("recipes", 10);
		for(int i = 0,m=list.size();i<m;i++) {
			BaseTrackedRecipe recipe = BaseTrackedRecipe.loadRecipe(list.getCompound(i));
			if(recipe != null) addRecipe(recipe, false);
		}
	}
	
	public static RecipeStorage get(ServerLevel world)
	{
		return world.getDataStorage().computeIfAbsent(RecipeStorage::new, RecipeStorage::new, "fusion_storage");
	}
	
	public boolean isInUse(BlockPos pos)
	{
		return activeBeacons.contains(pos.asLong());
	}
	
	public SyncRecipePacket getSyncPacket(ChunkPos pos) {
		Set<BaseTrackedRecipe> recipes = chunkRecipes.get(pos.toLong());
		if(recipes == null || recipes.isEmpty()) return null;
		return new SyncRecipePacket(new ObjectArrayList<>(Lists.transform(new ObjectArrayList<>(recipes), RecipeTransfer::new)));
	}
	
	public void addRecipe(BaseTrackedRecipe recipe)
	{
		addRecipe(recipe, true);
	}
	
	private void addRecipe(BaseTrackedRecipe recipe, boolean notLoading)
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
	
	private void remove(BaseTrackedRecipe recipe) {
		activeBeacons.remove(recipe.getPos().asLong());
		long pos = ChunkPos.asLong(recipe.getPos().getX() >> 4, recipe.getPos().getZ() >> 4);
		Set<BaseTrackedRecipe> recipes = chunkRecipes.get(pos);
		if(recipes == null) return;
		if(recipes.remove(recipe) && recipes.isEmpty()) {
			chunkRecipes.remove(pos);
		}
	}
	
	public void onTick() {
		if(activeRecipes.isEmpty()) return;
		for(Iterator<BaseTrackedRecipe> iter = activeRecipes.iterator();iter.hasNext();)
		{
			BaseTrackedRecipe recipe = iter.next();
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
	public CompoundTag save(CompoundTag nbt)
	{
		ListTag list = new ListTag();
		for(BaseTrackedRecipe recipe : activeRecipes) {
			list.add(recipe.save(new CompoundTag()));
		}
		nbt.put("recipes", list);
		return nbt;
	}
}
