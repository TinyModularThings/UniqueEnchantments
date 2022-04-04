package uniquebase.utils.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import uniquebase.handler.EnchantmentHandler;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	//getItem
//	@Inject(method="getHoverName", at=@At("HEAD"), cancellable = true)
//	private void nameAdjustment(CallbackInfoReturnable<ITextComponent> ci) {
//		ItemStack stack = ((ItemStack)(Object)this);
//		if(stack.getItem() == Items.ENCHANTED_BOOK) {
//			ListNBT enchantments = EnchantedBookItem.getEnchantments(stack);
//			if(enchantments.size() == 1) {
//				Enchantment enchantment = MiscUtil.getFirstEnchantment(stack).getKey();
//				boolean obfuscated = true;
//				if(FMLEnvironment.dist.isClient()) {
//					obfuscated = !isHoveringSlot();
//				}
//				obfuscated = UEBase.IS_OBFUSCATED.get();
//				IFormattableTextComponent textComponent = new TranslationTextComponent(enchantment.getDescriptionId());
//
//				textComponent.withStyle(MiscUtil.toColor(UEBase.COLOR_MAP.getInt(enchantment)));
//				if(obfuscated) {
//					textComponent.append(" ").append(new TranslationTextComponent("111").withStyle(TextFormatting.OBFUSCATED));
//				}
//				
//				ci.setReturnValue(textComponent);
//			}
//		}
//	}
	
	private static final ThreadLocal<ItemStack> ACTIVE_STACK = ThreadLocal.withInitial(() -> ItemStack.EMPTY);
	
	@Inject(method="getTooltipLines", at=@At("HEAD"))
	private void onToolTipBegin(CallbackInfoReturnable<List<ITextComponent>> result)
	{
		ACTIVE_STACK.set((ItemStack)(Object)this); 
	}
	
	@Inject(method="getTooltipLines", at=@At("RETURN"))
	private void onToolTipEnd(CallbackInfoReturnable<List<ITextComponent>> result)
	{
		ACTIVE_STACK.remove(); 
	}
	
	
	@Inject(method="appendEnchantmentNames", at=@At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void overrideEnchantmentTooltips(List<ITextComponent> tooltips, ListNBT enchantments, CallbackInfo info)
	{
		if(EnchantmentHandler.INSTANCE.addEnchantmentInfo(enchantments, tooltips, ACTIVE_STACK.get().getItem()))
		{
			info.cancel();
		}
	}
	
//	@OnlyIn(Dist.CLIENT)
//	private boolean isHoveringSlot() {
//		Screen screen = Minecraft.getInstance().screen;
//		return screen instanceof ContainerScreen && ((ContainerScreen<?>) screen).getSlotUnderMouse() != null;
//	}
}
