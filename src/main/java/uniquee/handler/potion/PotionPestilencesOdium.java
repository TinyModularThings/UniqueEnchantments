package uniquee.handler.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import uniquee.enchantments.curse.EnchantmentPestilencesOdium;

public class PotionPestilencesOdium extends Effect
{
	public PotionPestilencesOdium()
	{
		super(EffectType.HARMFUL, 3484199);
		setRegistryName("pestilences_odium");
	}
	
	@Override
	public void performEffect(LivingEntity entityLivingBaseIn, int amplifier)
	{
		EffectInstance effect = entityLivingBaseIn.getActivePotionEffect(this);
		if(effect == null || entityLivingBaseIn == null)
		{
			return;
		}
		if(entityLivingBaseIn.world.getGameTime() % Math.max(1, (EnchantmentPestilencesOdium.DELAY.get() / Math.max(1, amplifier))) == 0)
		{
			entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, EnchantmentPestilencesOdium.DAMAGE_PER_TICK.getFloat() * amplifier);
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier)
	{
		return true;
	}
}
