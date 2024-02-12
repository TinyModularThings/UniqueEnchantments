package uniqueeutils.enchantments;

import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.saveddata.SavedData;

public class BlockEnchantmentsExample extends SavedData {
	Long2ObjectMap<Int2ObjectMap<List<EnchantmentInstance>>> chunkEnchantments;
	Map<Enchantment, LongList> reverseLookup;
	
	
	public BlockEnchantmentsExample() {}
	public BlockEnchantmentsExample(CompoundTag loading) {
	}
	

	@Override
	public CompoundTag save(CompoundTag p_77763_) {
		return null;
	}
	
	public static BlockEnchantmentsExample getEnchantments(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(BlockEnchantmentsExample::new, BlockEnchantmentsExample::new, "UEBlockEnchantments");
	}
}
