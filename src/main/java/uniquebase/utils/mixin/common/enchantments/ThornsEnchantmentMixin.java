package uniquebase.utils.mixin.common.enchantments;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import uniquebase.utils.EnchantmentContainer;
import uniquebase.utils.MiscUtil;

@Mixin(ThornsEnchantment.class)
public class ThornsEnchantmentMixin {

	@Inject(method ="getDamage", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void getThornDamage(int levels, RandomSource rand, CallbackInfoReturnable<Integer> ci) {
		//work-around needed
		if(EnchantmentContainer.ThornsClass.ONCE) {
			if(EnchantmentContainer.ThornsClass.ENT instanceof LivingEntity) {
				levels = MiscUtil.getCombinedEnchantmentLevel(Enchantments.THORNS, (LivingEntity)EnchantmentContainer.ThornsClass.ENT);
			}
			
			if(levels == 0) return;
			ci.setReturnValue(levels > 6 ? (rand.nextInt(Mth.ceil((levels-6)*0.5)+6))+1 : rand.nextInt(levels)+1);
			
			EnchantmentContainer.ThornsClass.ONCE = false;
			EnchantmentContainer.ThornsClass.ENT = null;
		}
	}
}
