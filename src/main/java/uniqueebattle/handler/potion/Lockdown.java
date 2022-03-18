package uniqueebattle.handler.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class Lockdown extends Effect
{
	public Lockdown()
	{
		super(EffectType.HARMFUL, 5926017);
		setRegistryName("uniquebattle", "snare");
		addAttributeModifier(Attributes.MOVEMENT_SPEED, "4617e38d-8ea1-426f-9749-e768e8344be3", -1D, AttributeModifier.Operation.MULTIPLY_TOTAL);
	}
}
