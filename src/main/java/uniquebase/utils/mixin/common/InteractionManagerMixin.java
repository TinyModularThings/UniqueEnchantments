package uniquebase.utils.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.level.ServerPlayerGameMode;

@Mixin(ServerPlayerGameMode.class)
public interface InteractionManagerMixin
{
	@Accessor("isDestroyingBlock")
	public boolean isMiningBlock();
}
