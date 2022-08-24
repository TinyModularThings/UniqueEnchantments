package uniquebase.utils.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uniquebase.handler.EnchantmentHandler;

@Mixin(ItemStack.class)
@OnlyIn(Dist.CLIENT)
public class ItemStackMixin {
	
	private static final ThreadLocal<ItemStack> ACTIVE_STACK = ThreadLocal.withInitial(() -> ItemStack.EMPTY);
	
	@Inject(method="getTooltipLines", at=@At("HEAD"))
	private void onToolTipBegin(CallbackInfoReturnable<List<Component>> result)
	{
		ACTIVE_STACK.set((ItemStack)(Object)this); 
	}
	
	@Inject(method="getTooltipLines", at=@At("RETURN"))
	private void onToolTipEnd(CallbackInfoReturnable<List<Component>> result)
	{
		ACTIVE_STACK.remove(); 
	}
	
	
	@Inject(method="appendEnchantmentNames", at=@At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void overrideEnchantmentTooltips(List<Component> tooltips, ListTag enchantments, CallbackInfo info)
	{
		if(EnchantmentHandler.INSTANCE.addEnchantmentInfo(enchantments, tooltips, ACTIVE_STACK.get().getItem()))
		{
			info.cancel();
		}
	}
	

}
