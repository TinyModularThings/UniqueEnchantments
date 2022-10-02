package uniquebase.utils.mixin.common.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	
	@ModifyVariable(method = "getDamageAfterMagicAbsorb", at = @At(value = "STORE"), ordinal = 0, remap = false)
    public float overrideResis(float p_70672_2_) {
		LivingEntity ent = ((LivingEntity)(Object)this);
		if(!ent.hasEffect(Effects.DAMAGE_RESISTANCE)) return 1.0f;
		
		int i = ent.getEffect(Effects.DAMAGE_RESISTANCE).getAmplifier()+1;
        return Math.max((float)Math.pow(0.85, i) - 0.05f, 0.01f);
    }

}
