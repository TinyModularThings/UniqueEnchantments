package uniquebase.utils.mixin.common.enchantments;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.ITextComponent;
import uniquebase.utils.MiscUtil;

@Mixin(Enchantment.class)
public class EnchantmentsMixin {
	
	@Inject(method="getFullname", at=@At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void rebuildName(int level, CallbackInfoReturnable<ITextComponent> ci) {
		ci.setReturnValue(MiscUtil.createEnchantmentName(((Enchantment)(Object)this), level, true));
	}
}