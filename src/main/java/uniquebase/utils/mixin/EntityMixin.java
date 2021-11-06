package uniquebase.utils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public interface EntityMixin
{
	@Invoker("setSharedFlag")
	public void setFlag(int flag, boolean value);
}
