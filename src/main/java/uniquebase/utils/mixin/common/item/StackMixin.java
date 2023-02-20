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
import uniquebase.utils.MiscUtil;
import uniquee.UE;
import uniquee.enchantments.unique.Grimoire;

@Mixin(ItemStack.class)
public class StackMixin
{
	ItemStack stack = (ItemStack)(Object)this;
	
	@Inject(method = "hurtAndBreak", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void afterHurt(int damage, LivingEntity living, Consumer<LivingEntity> callback, CallbackInfo info)
	{
		MinecraftForge.EVENT_BUS.post(new ItemDurabilityChangeEvent((ItemStack)(Object)this, damage, living));
	}
	
	@Inject(method = "getMaxDamage", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void maxDurability(CallbackInfoReturnable<Integer> info)
	{
		int oldDur = info.getReturnValueI();
		int level = MiscUtil.getEnchantmentLevel(UE.GRIMOIRE, stack);
		if(level > 0) {
			int enchantability = stack.getEnchantmentValue();
			int totalLevel = MiscUtil.getItemLevel(stack);
			int newDur = (int)Math.ceil((oldDur+Grimoire.FLAT_SCALING.get(totalLevel))*Math.sqrt((100+(totalLevel+enchantability)*Grimoire.LEVEL_SCALING.get(level))/100));
			info.setReturnValue(newDur);
		} else info.setReturnValue(oldDur);
	}
}
