package uniquebase.utils.mixin.common.item;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import uniquebase.api.events.ItemDurabilityChangeEvent;
import uniquebase.api.events.SetItemDurabilityEvent;

@Mixin(ItemStack.class)
public class StackMixin
{
	ItemStack stack = (ItemStack)(Object)this;
	
	@Inject(method = "hurtAndBreak", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void afterHurt(int damage, LivingEntity living, Consumer<LivingEntity> callback, CallbackInfo info)
	{
		MinecraftForge.EVENT_BUS.post(new ItemDurabilityChangeEvent(stack, damage, living));
	}
	
	@Inject(method = "getMaxDamage", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void maxDurability(CallbackInfoReturnable<Integer> info)
	{
		SetItemDurabilityEvent event = new SetItemDurabilityEvent(stack, info.getReturnValueI());
		MinecraftForge.EVENT_BUS.post(event);
		info.setReturnValue(event.getDurability());
	}
}
