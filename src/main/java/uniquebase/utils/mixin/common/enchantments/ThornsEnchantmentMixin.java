package uniquebase.utils.mixin.common.enchantments;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import uniquebase.utils.EnchantmentContainer;
import uniquebase.utils.MiscUtil;

@Mixin(ThornsEnchantment.class)
public class ThornsEnchantmentMixin {

	@Inject(method ="getDamage", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void getThornDamage(int levels, Random rand, CallbackInfoReturnable<Integer> ci) {
		//work-around needed
		System.out.println(EnchantmentContainer.ThornsClass.ONCE);
		if(EnchantmentContainer.ThornsClass.ONCE) {
			System.out.println(EnchantmentContainer.ThornsClass.ENT);
			if(EnchantmentContainer.ThornsClass.ENT instanceof LivingEntity) {
				levels = MiscUtil.getCombinedEnchantmentLevel(Enchantments.THORNS, (LivingEntity)EnchantmentContainer.ThornsClass.ENT);
			}
			
			System.out.println(levels);
			if(levels == 0) return;
			int value = levels > 6 ? (rand.nextInt(MathHelper.ceil((levels-6)*0.5)+6))+1 : rand.nextInt(levels)+1;
			System.out.println(value);
			ci.setReturnValue(value);
			
			EnchantmentContainer.ThornsClass.ONCE = false;
			EnchantmentContainer.ThornsClass.ENT = null;
		}
	}
}
