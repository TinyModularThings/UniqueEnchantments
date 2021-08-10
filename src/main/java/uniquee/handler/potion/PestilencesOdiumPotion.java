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
	public void performEffect(LivingEntity entityLivingBaseIn, int amplifier)
	{
		EffectInstance effect = entityLivingBaseIn.getActivePotionEffect(this);
		if(effect == null || entityLivingBaseIn == null)
		{
			return;
		}
		if(entityLivingBaseIn.world.getGameTime() % Math.max(1, (PestilencesOdium.DELAY.get() / Math.max(1, amplifier))) == 0)
		{
			float value = PestilencesOdium.DAMAGE_PER_TICK.getFloat() * amplifier;
			System.out.println("Value: "+value);
			entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, value);
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier)
	{
		return true;
	}
}
