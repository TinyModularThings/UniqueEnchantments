package uniquebase.utils.mixin.common.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	
	@ModifyConstant(method = "getDamageAfterMagicAbsorb", constant = @Constant(floatValue = 25.0F), remap = true)
    public float overrideResis(float p_70672_2_) {
		LivingEntity ent = ((LivingEntity)(Object)this);
		if(!ent.hasEffect(MobEffects.DAMAGE_RESISTANCE)) return 1.0f;
		
		int i = ent.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier();
		System.out.println(1/(Math.max( (float)Math.pow(0.9, i) * 0.8f, 0.01f)));
        return 0.2f*(Math.max( (float)Math.pow(0.9, i) * 0.8f, 0.01f));
    }
}
