package uniquee.handler.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import uniquee.enchantments.curse.EnchantmentPestilencesOdium;

public class PotionPestilencesOdium extends Potion
{
	public PotionPestilencesOdium()
	{
		super(true, 3484199);
		setIconIndex(1, 2);
		setPotionName("potion.uniquee.uniquepestilences_odium");
		setRegistryName("pestilences_odium");
	}
	
	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier)
	{
		PotionEffect effect = entityLivingBaseIn.getActivePotionEffect(this);
		if(effect == null || entityLivingBaseIn == null)
		{
			return;
		}
		if(entityLivingBaseIn.world.getTotalWorldTime() % Math.max(1, (EnchantmentPestilencesOdium.DELAY / Math.max(1, amplifier))) == 0)
		{
			entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, (float)(EnchantmentPestilencesOdium.DAMAGE_PER_TICK * amplifier));
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier)
	{
		return true;
	}
}
