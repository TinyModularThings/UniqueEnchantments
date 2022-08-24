package uniquebase.utils.mixin.common.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.effect.MobEffectInstance;

@Mixin(MobEffectInstance.class)
public interface PotionMixin
{
	@Accessor("duration")
	public void setPotionDuration(int time);
}
