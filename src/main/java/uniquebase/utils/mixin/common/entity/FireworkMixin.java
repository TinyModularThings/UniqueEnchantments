package uniquebase.utils.mixin.common.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;

@Mixin(FireworkRocketEntity.class)
public interface FireworkMixin
{
	@Accessor("attachedToEntity")
	public LivingEntity getRidingEntity();
}
