package uniquebase.utils.mixin.common.enchantments;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.damagesource.CombatRules;
import uniquebase.BaseConfig;

@Mixin(CombatRules.class)
public class CombatRulesMixin {
	
	@Inject(method = "getDamageAfterMagicAbsorb", at = @At("INVOKE"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void getDamageAfterMagicAbsorb(float damage, float points, CallbackInfoReturnable<Float> ci) {
		ci.setReturnValue((float) (damage/(1+BaseConfig.TWEAKS.protectionMultiplier.get().doubleValue()*points*points)));
	}
}
