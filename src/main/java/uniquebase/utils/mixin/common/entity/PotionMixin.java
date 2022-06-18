package uniquebase.utils.mixin.common.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.potion.EffectInstance;

@Mixin(EffectInstance.class)
public interface PotionMixin
{
	@Accessor("duration")
	public void setPotionDuration(int time);
}
