package uniquebase.utils.mixin.common.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;

@Mixin(FireworkRocketEntity.class)
public interface FireworkMixin
{
	@Accessor("attachedToEntity")
	public LivingEntity getRidingEntity();
}
