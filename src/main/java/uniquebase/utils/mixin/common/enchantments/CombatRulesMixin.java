package uniquebase.utils.mixin.common.enchantments;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.CombatRules;

@Mixin(CombatRules.class)
public class CombatRulesMixin {
	
	@Inject(method = "getDamageAfterMagicAbsorb", at = @At("INVOKE"))
	private static void getDamageAfterMagicAbsorb(float damage, float points, CallbackInfoReturnable<Float> ci) {
		ci.setReturnValue((float) (damage/Math.sqrt(1+0.75*points)));
	}
}
