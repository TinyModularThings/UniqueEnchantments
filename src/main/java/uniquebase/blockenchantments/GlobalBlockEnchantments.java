package uniquebase.blockenchantments;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

public class GlobalBlockEnchantments extends SavedData {
	Long2ObjectMap<Int2ObjectMap<List<EnchantmentInstance>>> chunkEnchantments = new Long2ObjectLinkedOpenHashMap<>();
	Map<Enchantment, LongSet> reverseLookup = new Object2ObjectLinkedOpenHashMap<>();
	
	public static GlobalBlockEnchantments getOverworldEnchantments() {
		return getEnchantments(ServerLifecycleHooks.getCurrentServer().overworld());
	}
	
	public static GlobalBlockEnchantments getEnchantments(ResourceKey<Level> dimension) {
		return getEnchantments(ServerLifecycleHooks.getCurrentServer().getLevel(dimension));
	}
	
	public static GlobalBlockEnchantments getEnchantments(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(GlobalBlockEnchantments::new, GlobalBlockEnchantments::new, "global_block_enchantments");
	}
	
	public GlobalBlockEnchantments() {
	}
	
	public GlobalBlockEnchantments(CompoundTag nbt) {
		ListTag list = nbt.getList("enchantments", Tag.TAG_COMPOUND);
		for(int i = 0,m=list.size();i<m;i++) {
			CompoundTag tag = list.getCompound(i);
			BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
			ListTag enchantments = tag.getList("Enchantments", 10);
			for(int j = 0,n=enchantments.size();j<n;j++) {
				CompoundTag enchTag = enchantments.getCompound(j);
				Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(EnchantmentHelper.getEnchantmentId(enchTag));
				if(ench == null) continue;
				addEnchantment(pos, ench, EnchantmentHelper.getEnchantmentLevel(enchTag));
			}
		}
		setDirty(false);
	}
	
	@Override
	public CompoundTag save(CompoundTag nbt) {
		ListTag list = new ListTag();
		for(Long2ObjectMap.Entry<Int2ObjectMap<List<EnchantmentInstance>>> chunkEntry : chunkEnchantments.long2ObjectEntrySet()) {
			long position = chunkEntry.getLongKey();
			int chunkX = ChunkPos.getX(position) * 16;
			int chunkZ = ChunkPos.getZ(position) * 16;
			for(Int2ObjectMap.Entry<List<EnchantmentInstance>> blockEntry : chunkEntry.getValue().int2ObjectEntrySet()) {
				ListTag enchantments = new ListTag();
				for(EnchantmentInstance enchantment : blockEntry.getValue()) {
					enchantments.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(enchantment.enchantment), (byte)enchantment.level));
				}
				int blockPos = blockEntry.getIntKey();
				CompoundTag tag = new CompoundTag();
				tag.put("Enchantments", enchantments);
				tag.putInt("x", chunkX + getX(blockPos));
				tag.putInt("y", getY(blockPos));
				tag.putInt("z", chunkZ + getZ(blockPos));
				list.add(tag);
			}
		}
		nbt.put("enchantments", list);
		return nbt;
	}
	
	public boolean addEnchantment(BlockPos pos, Enchantment ench, int level) {
		List<EnchantmentInstance> instances = chunkEnchantments.computeIfAbsent(ChunkPos.asLong(pos), T -> new Int2ObjectLinkedOpenHashMap<>()).computeIfAbsent(getCoordIndex(pos.getX(), pos.getY(), pos.getZ()), T -> new ObjectArrayList<>());
		for(int i = 0,m=instances.size();i<m;i++) {
			if(instances.get(i).enchantment == ench) return false;
		}
		reverseLookup.computeIfAbsent(ench, T -> new LongLinkedOpenHashSet()).add(pos.asLong());
		setDirty();
		return true;
	}
	
	public boolean containsEnchantment(Enchantment ench) {
		return reverseLookup.get(ench) != null;
	}
	
	public boolean containsEnchantment(BlockPos position, Enchantment ench) {
		LongSet reverse = reverseLookup.get(ench);
		return reverse != null && reverse.contains(position.asLong());
	}
	
	public int getEnchantmentLevel(BlockPos pos, Enchantment ench) {
		Int2ObjectMap<List<EnchantmentInstance>> map = chunkEnchantments.get(ChunkPos.asLong(pos));
		if(map == null) return 0;
		List<EnchantmentInstance> instances = map.get(getCoordIndex(pos));
		if(instances == null) return 0;
		for(int i = 0,m=instances.size();i<m;i++) {
			EnchantmentInstance instance = instances.get(i);
			if(instance.enchantment == ench) {
				return instance.level;
			}
		}
		return 0;
	}
	
	public Object2IntMap<Enchantment> getAllEnchantments(BlockPos pos) {
		Int2ObjectMap<List<EnchantmentInstance>> map = chunkEnchantments.get(ChunkPos.asLong(pos));
		if(map == null) return Object2IntMaps.emptyMap();
		List<EnchantmentInstance> instances = map.get(getCoordIndex(pos));
		if(instances == null) return Object2IntMaps.emptyMap();
		Object2IntMap<Enchantment> result = new Object2IntLinkedOpenHashMap<>();
		for(int i = 0,m=instances.size();i<m;i++) {
			EnchantmentInstance instance = instances.get(i);
			result.put(instance.enchantment, instance.level);
		}
		return result;
	}
	
	public LongSet getRawPositionOfEnchantment(Enchantment ench) {
		LongSet reverse = reverseLookup.get(ench);
		return reverse == null ? LongSets.emptySet() : LongSets.unmodifiable(reverse);
	}
	
	public Stream<BlockPos> getPositionsForEnchantment(Enchantment ench) {
		LongSet reverse = reverseLookup.get(ench);
		return reverse == null ? Stream.empty() : StreamSupport.longStream(reverse.spliterator(), false).mapToObj(BlockPos::of);
	}
	
	public void cleanAllEnchantments() {
		chunkEnchantments.clear();
		reverseLookup.clear();
		setDirty();
	}
	
	public boolean removeEnchantmentFromChunk(ChunkPos pos) {
		Int2ObjectMap<List<EnchantmentInstance>> map = chunkEnchantments.remove(pos.toLong());
		if(map == null) return false;
		int chunkX = pos.x * 16;
		int chunkZ = pos.z * 16;
		for(Entry<List<EnchantmentInstance>> entry : map.int2ObjectEntrySet()) {
			int position = entry.getIntKey();
			cleanup(new BlockPos(chunkX + getX(position), getY(position), chunkZ + getZ(position)), null, entry.getValue());
		}
		setDirty();
		return true;
	}
	
	public boolean removeEnchantment(BlockPos pos, Enchantment ench) {
		Int2ObjectMap<List<EnchantmentInstance>> map = chunkEnchantments.get(ChunkPos.asLong(pos));
		if(map == null) return false;
		List<EnchantmentInstance> instances = map.get(getCoordIndex(pos));
		if(instances == null) return false;
		for(int i = 0,m=instances.size();i<m;i++) {
			EnchantmentInstance instance = instances.get(i);
			if(instance.enchantment == ench) {
				instances.remove(i--);
				if(instances.isEmpty()) {
					map.remove(getCoordIndex(pos));
					cleanup(pos, map, ObjectLists.singleton(instance));
				}
				setDirty();
				return true;
			}
		}
		return false;
	}
	
	public boolean removeEnchantment(BlockPos pos) {
		Int2ObjectMap<List<EnchantmentInstance>> map = chunkEnchantments.get(ChunkPos.asLong(pos));
		if(map == null) return false;
		List<EnchantmentInstance> result = map.remove(getCoordIndex(pos));
		if(result != null) {
			cleanup(pos, map, result);
			setDirty();
			return true;
		}
		return false;
	}
	
	private void cleanup(BlockPos pos, Int2ObjectMap<List<EnchantmentInstance>> map, Collection<EnchantmentInstance> toClean) {
		if(map != null && map.isEmpty()) {
			chunkEnchantments.remove(ChunkPos.asLong(pos));
		}
		long blockPos = pos.asLong();
		for(EnchantmentInstance instance : toClean) {
			LongSet reverse = reverseLookup.get(instance.enchantment);
			if(reverse == null) continue;
			if(reverse.remove(blockPos) && reverse.isEmpty()) {
				reverseLookup.remove(instance.enchantment);
			}
		}
	}
	
	public static int getCoordIndex(BlockPos pos) {
		return getCoordIndex(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public static int getCoordIndex(int x, int y, int z) {
		return (y & 0xFFFF) << 8 | (x & 15) << 4 | (z & 15);
	}
	
	public static int getX(int coord) {
		return (coord >> 4) & 15;
	}
	
	public static int getZ(int coord) {
		return coord & 15;
	}
		
	public static int getY(int coord) {
		return coord >> 8 & 0xFFFF;
	}
}
