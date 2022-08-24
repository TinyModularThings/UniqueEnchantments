package uniquebase.utils.mixin.common.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import uniquebase.utils.events.EndermenLookEvent;

@Mixin(EnderMan.class)
public class EndermenMixin
{
	@Inject(method = "isLookingAtMe", at = @At("HEAD"), cancellable = true)
	private void isLookingAtMe(Player player, CallbackInfoReturnable<Boolean> value) 
	{
		if(MinecraftForge.EVENT_BUS.post(new EndermenLookEvent(player)))
		{
			value.setReturnValue(false);
		}
	}
}
