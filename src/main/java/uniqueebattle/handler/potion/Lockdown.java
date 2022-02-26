package uniqueebattle.handler.potion;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class Lockdown extends Effect
{
	public Lockdown()
	{
		super(EffectType.HARMFUL, 5926017);
		setRegistryName("snare");
		addAttributesModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "4617e38d-8ea1-426f-9749-e768e8344be3", 0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
	}
}
