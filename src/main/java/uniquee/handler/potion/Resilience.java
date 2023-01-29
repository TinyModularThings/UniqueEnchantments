package uniquee.handler.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class Resilience extends MobEffect {
	
	public Resilience() {
		super(MobEffectCategory.BENEFICIAL, 9643043);
	}
	
	@Override
	public void applyEffectTick(LivingEntity ent, int amplifier) {
		if(amplifier > 100 && ent.level.getGameTime() % 20 == 0) {
			amplifier++;
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
		return true;
	}
}
