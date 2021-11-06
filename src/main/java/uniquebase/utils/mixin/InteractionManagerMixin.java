package uniquebase.utils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.management.PlayerInteractionManager;

@Mixin(PlayerInteractionManager.class)
public interface InteractionManagerMixin
{
	@Accessor("isDestroyingBlock")
	public boolean isMiningBlock();
}
