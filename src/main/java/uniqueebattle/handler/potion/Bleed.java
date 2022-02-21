package uniqueebattle.handler.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

public class Bleed extends Potion
{
	public Bleed()
	{
		super(true, 0xFFFF0000);
		setRegistryName("bleed");
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int amplifier)
	{
		if(entity.world.getTotalWorldTime() % 20 == 0)
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
