package uniquee.handler.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AmelioratedStrength extends MobEffect {

	public AmelioratedStrength() {
		super(MobEffectCategory.BENEFICIAL, 9643043);
		this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "84bef156-9356-11ed-a1eb-0242ac120002", 0.0D, Operation.MULTIPLY_TOTAL);
	}
	
	@Override
	public double getAttributeModifierValue(int p_19457_, AttributeModifier p_19458_) {
		return (p_19457_+1) * 0.15 + 0.1;
	}

}
