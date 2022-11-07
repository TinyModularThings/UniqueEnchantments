package uniquee.handler.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class Thrombosis extends MobEffect {

	public double CHANCE = 0.1;
	
	public Thrombosis() {
		super(MobEffectCategory.HARMFUL, 13212947);
	}
	
	@Override
	public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
		return false;
	}
	
	public double getChance() {
		return this.CHANCE;
	}
	
	public void setChance(double chance) {
		this.CHANCE = chance;
	}

}
