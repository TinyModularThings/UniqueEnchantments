package uniquebase.utils.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import uniquebase.api.events.ItemDurabilityChangeEvent;

@Mixin(ItemStack.class)
public class StackMixin
{
	@Inject(method = "hurtAndBreak", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void afterHurt(int level, LivingEntity living, Consumer<LivingEntity> callback, CallbackInfo info)
	{
		MinecraftForge.EVENT_BUS.post(new ItemDurabilityChangeEvent((ItemStack)(Object)this, living));
	}
}
