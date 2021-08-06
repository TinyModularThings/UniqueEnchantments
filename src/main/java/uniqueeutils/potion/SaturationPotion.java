package uniqueeutils.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.FoodStats;

public class SaturationPotion extends Potion
{
	public SaturationPotion()
	{
		super(false, 16262179);
		setPotionName("effect.saturation");
		setBeneficial();
	}
	
	@Override
	public boolean isReady(int duration, int amplifier)
	{
		return true;
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int amplifier)
	{
		if(entity instanceof EntityPlayer && entity.world.getTotalWorldTime() % 4 == 0)
		{
			FoodStats stat = ((EntityPlayer)entity).getFoodStats();
			int last = stat.getFoodLevel();
			stat.addStats(1, amplifier);
			stat.setFoodLevel(last);
		}
	}
}
