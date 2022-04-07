package uniquebase.utils.mixin.common.tile;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import uniquebase.UEBase;
import uniquebase.handler.EnchantmentHandler;
import uniquebase.utils.MiscUtil;

@Mixin(EnchantmentContainer.class)
public class EnchTableMixin
{
	@Redirect(method = "Lnet/minecraft/inventory/container/EnchantmentContainer;lambda$clickMenuButton$1", 
	at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;onEnchantmentPerformed(Lnet/minecraft/item/ItemStack;I)V"))
	public void onEnchanted(PlayerEntity player, ItemStack stack, int level)
	{
		if(UEBase.XP_OVERRIDE_ENCHANT.get()) MiscUtil.drainExperience(player, MathHelper.ceil(MiscUtil.getXPForLvl(level) * UEBase.XP_MULTIPLIER_ENCHANT.get()));
		else player.onEnchantmentPerformed(stack, level);
	}
	
	@Inject(method = "getEnchantmentList", at = @At(value = "TAIL", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public void limitEnchantments(ItemStack stack, int unused, int unusde, CallbackInfoReturnable<List<EnchantmentData>> info, List<EnchantmentData> list)
	{
		EnchantmentHandler.INSTANCE.limitEnchantments(list, stack);
	}
}
