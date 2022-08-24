package uniqueeutils.potion;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public class SaturationEffect extends MobEffect
{
	private String name;
	
	public SaturationEffect()
	{
		super(MobEffectCategory.BENEFICIAL, 16262179);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	protected String getOrCreateDescriptionId()
	{
		if(this.name == null) this.name = Util.makeDescriptionId("effect", Registry.MOB_EFFECT.getKey(MobEffects.SATURATION));
		return this.name;
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier)
	{
		return true;
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier)
	{
		if(entity instanceof Player && entity.level.getGameTime() % 4 == 0)
		{
			FoodData stat = ((Player)entity).getFoodData();
			int last = stat.getFoodLevel();
			stat.eat(1, amplifier);
			stat.setFoodLevel(last);
		}
	}
}
