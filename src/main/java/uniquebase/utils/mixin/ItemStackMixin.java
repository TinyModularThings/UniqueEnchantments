package uniquebase.utils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	//getItem
	@Inject(method="setHoverName", at=@At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void nameAdjustment(CallbackInfoReturnable<ITextComponent> ci) {
		ItemStack stack = ((ItemStack)(Object)this);
		if(stack.getItem() == Items.ENCHANTED_BOOK) {
			
		}
		IFormattableTextComponent textComponent = new TranslationTextComponent("");
		
		ci.setReturnValue(textComponent);
	}
}
