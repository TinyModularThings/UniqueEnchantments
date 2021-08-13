package uniqueeutils.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class SaturationEffect extends Effect
{
	private String name;
	
	public SaturationEffect()
	{
		super(EffectType.BENEFICIAL, 16262179);
		setRegistryName("saturation");
	}
	
	@Override
	@SuppressWarnings("deprecation")
	protected String getOrCreateDescriptionId()
	{
		if(this.name == null) this.name = Util.makeTranslationKey("effect", Registry.EFFECTS.getKey(Effects.SATURATION));
		return this.name;
	}
	
	@Override
	public boolean isReady(int duration, int amplifier)
	{
		return true;
	}
	
	@Override
	public void performEffect(LivingEntity entity, int amplifier)
	{
		if(entity instanceof PlayerEntity && entity.world.getGameTime() % 4 == 0)
		{
			FoodStats stat = ((PlayerEntity)entity).getFoodStats();
			int last = stat.getFoodLevel();
			stat.addStats(1, amplifier);
			stat.setFoodLevel(last);
		}
	}
}
