package uniquebase.utils.mixin.common.enchantments;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;

@Mixin(DigDurabilityEnchantment.class)
public class UnbreakingEnchantmentMixin {

	@Inject(method ="shouldIgnoreDurabilityDrop", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void getThornDamage(ItemStack stack, int levels, RandomSource rand, CallbackInfoReturnable<Boolean> ci) {
		ci.setReturnValue(rand.nextDouble() > 1-(1/(1+Math.sqrt(levels))));
	}
}
