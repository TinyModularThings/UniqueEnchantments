package uniquee.handler.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import uniquee.enchantments.curse.PestilencesOdium;

public class PestilencesOdiumPotion extends Effect
{
	public PestilencesOdiumPotion()
	{
		super(EffectType.HARMFUL, 3484199);
		setRegistryName("uniquee", "pestilences_odium");
	}
	
	@Override
	public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier)
	{
		EffectInstance effect = entityLivingBaseIn.getEffect(this);
		if(effect == null || entityLivingBaseIn == null)
		{
			return;
		}
		if(entityLivingBaseIn.level.getGameTime() % Math.max(1, (PestilencesOdium.DELAY.get() / Math.max(1, amplifier))) == 0)
		{
			float value = PestilencesOdium.DAMAGE_PER_TICK.getFloat() * amplifier;
			entityLivingBaseIn.hurt(DamageSource.MAGIC, value);
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier)
	{
		return true;
	}
}
