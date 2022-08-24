package uniquee.handler.potion;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import uniquee.enchantments.curse.PestilencesOdium;

public class PestilencesOdiumPotion extends MobEffect
{
	public PestilencesOdiumPotion()
	{
		super(MobEffectCategory.HARMFUL, 3484199);
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier)
	{
		MobEffectInstance effect = entity.getEffect(this);
		if(effect == null || entity == null)
		{
			return;
		}
		if(entity.level.getGameTime() % Math.max(1, (PestilencesOdium.DELAY.get() / Math.max(1, amplifier))) == 0)
		{
			float value = PestilencesOdium.DAMAGE_PER_TICK.getFloat() * amplifier;
			if(entity.getHealth() > value+0.1F)
			{
				entity.hurt(DamageSource.MAGIC, value);
			}
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier)
	{
		return true;
	}
}
