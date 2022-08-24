package uniqueebattle.handler.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Lockdown extends MobEffect
{
	public Lockdown()
	{
		super(MobEffectCategory.HARMFUL, 5926017);
		addAttributeModifier(Attributes.MOVEMENT_SPEED, "4617e38d-8ea1-426f-9749-e768e8344be3", -1D, AttributeModifier.Operation.MULTIPLY_TOTAL);
	}
}
