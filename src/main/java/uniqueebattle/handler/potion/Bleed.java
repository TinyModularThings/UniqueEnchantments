package uniqueebattle.handler.potion;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class Bleed extends MobEffect
{
	public Bleed()
	{
		super(MobEffectCategory.HARMFUL, 0xFFFF0000);
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier)
	{
		if(entity.level.getGameTime() % 20 == 0)
		{
			entity.hurt(DamageSource.OUT_OF_WORLD, (float)Math.sqrt(amplifier+1)/2);
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier)
	{
		return true;
	}
}
