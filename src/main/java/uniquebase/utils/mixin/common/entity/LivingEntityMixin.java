package uniquebase.utils.mixin.common.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	
	@ModifyVariable(method = "getDamageAfterMagicAbsorb", at = @At(value = "STORE"), ordinal = 0, remap = true)
    public float overrideResis(float p_70672_2_) {
		LivingEntity ent = ((LivingEntity)(Object)this);
		if(!ent.hasEffect(MobEffects.DAMAGE_RESISTANCE)) return 1.0f;
		
		int i = ent.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier()+1;
        return Math.max((float)Math.pow(0.85, i) - 0.05f, 0.01f);
    }
}
