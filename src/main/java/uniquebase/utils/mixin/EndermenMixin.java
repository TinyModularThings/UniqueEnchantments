package uniquebase.utils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import uniquebase.utils.events.EndermenLookEvent;

@Mixin(EndermanEntity.class)
public class EndermenMixin
{
	@Inject(method = "isLookingAtMe", at = @At("HEAD"), cancellable = true)
	private void isLookingAtMe(PlayerEntity player, CallbackInfoReturnable<Boolean> value) 
	{
		if(MinecraftForge.EVENT_BUS.post(new EndermenLookEvent(player)))
		{
			value.setReturnValue(false);
		}
	}
}
