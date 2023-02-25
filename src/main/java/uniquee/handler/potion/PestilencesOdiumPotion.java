package uniquee.handler.potion;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import uniquee.enchantments.curse.PestilencesOdium;

public class PestilencesOdiumPotion extends MobEffect
{
	public PestilencesOdiumPotion()
	{
		super(MobEffectCategory.HARMFUL, 3484199);
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier)
	{
		MobEffectInstance effect = entity.getEffect(this);
		if(effect == null || entity == null || !(entity.getAbsorptionAmount() > 1))
		{
			return;
		}
		if(entity.level.getGameTime() % Math.max(100-amplifier,10) == 0)
		{
			float value = (float) Math.log(1+PestilencesOdium.DAMAGE_PER_TICK.get(entity.getHealth()*(1+entity.getActiveEffects().size())));
			if(entity.getHealth() > value+0.1F)
			{
				entity.hurt(DamageSource.MAGIC.bypassMagic(), value);
			}
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier)
	{
		return true;
	}

	
	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attrMan, int amplifier) {
		if(!(entity.getAbsorptionAmount() > 1)) return;
		Multimap<Attribute, AttributeModifier> mods = HashMultimap.create();
		mods.put(Attributes.ARMOR, new AttributeModifier(PestilencesOdium.PESTILENCE_ARMOR_MOD, (1/Math.log(3.2+amplifier))-1, Operation.MULTIPLY_TOTAL));
		mods.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(PestilencesOdium.PESTILENCE_TOUGHNESS_MOD, (1/Math.log(3.2+amplifier))-1, Operation.MULTIPLY_TOTAL));
		attrMan.addTransientAttributeModifiers(mods);
	}
}
