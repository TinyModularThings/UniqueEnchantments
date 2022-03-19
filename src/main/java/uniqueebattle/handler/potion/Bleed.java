package uniqueebattle.handler.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

public class Bleed extends Effect
{
	public Bleed()
	{
		super(EffectType.HARMFUL, 0xFFFF0000);
		setRegistryName("uniquebattle", "bleed");
	}
	
	@Override
	public void performEffect(LivingEntity entity, int amplifier)
	{
		if(entity.world.getGameTime() % 20 == 0)
		{
			entity.attackEntityFrom(DamageSource.OUT_OF_WORLD, (float)Math.sqrt(amplifier+1)/2);
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier)
	{
		return true;
	}
}
