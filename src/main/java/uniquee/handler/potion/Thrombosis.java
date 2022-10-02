package uniquee.handler.potion;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class Thrombosis extends Effect {

	public double CHANCE = 0.1;
	
	public Thrombosis() {
		super(EffectType.HARMFUL, 13212947);
		setRegistryName("uniquee", "thrombosis");
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
