package uniquee.handler.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EternalFlamePotion extends MobEffect {

	public EternalFlamePotion() {
		super(MobEffectCategory.BENEFICIAL, 13212940);
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		entity.invulnerableTime += 1;
	}

	@Override
	public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
		return true;
	}
}
