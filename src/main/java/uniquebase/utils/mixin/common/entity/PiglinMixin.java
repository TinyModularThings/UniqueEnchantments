package uniquebase.utils.mixin.common.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraftforge.common.MinecraftForge;
import uniquebase.utils.events.PiglinWearableCheckEvent;

@Mixin(PiglinTasks.class)
public class PiglinMixin
{
	@Inject(method = "isWearingGold", at = @At(value = "RETURN", shift = Shift.BEFORE), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void onPiglinMixin(LivingEntity entity, CallbackInfoReturnable<Boolean> result)
	{
		if(MinecraftForge.EVENT_BUS.post(new PiglinWearableCheckEvent(entity)))
		{
			result.setReturnValue(true);
		}
	}
}
