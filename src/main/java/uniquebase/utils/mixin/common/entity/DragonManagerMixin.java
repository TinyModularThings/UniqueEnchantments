package uniquebase.utils.mixin.common.entity;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.end.DragonFightManager;

@Mixin(DragonFightManager.class)
public interface DragonManagerMixin
{
	@Accessor("dragonUUID")
	public void setNewDragon(UUID dragon);
}
