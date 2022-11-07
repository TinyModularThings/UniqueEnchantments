package uniquebase.utils.mixin.common.tile;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

@Mixin(LevelChunk.class)
public interface ChunkMixin
{
	@Accessor("tickersInLevel")
	public Map<BlockPos, TickingBlockEntity> getTickerMap();
}
