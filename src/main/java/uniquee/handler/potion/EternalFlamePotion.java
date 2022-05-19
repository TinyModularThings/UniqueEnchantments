package uniquee.handler.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class EternalFlamePotion extends Effect {

	public EternalFlamePotion() {

		super(EffectType.BENEFICIAL, 13212940);
		setRegistryName("uniquee", "eternal_flame");
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
