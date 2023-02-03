package uniquebase.utils.mixin.common.tile;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import uniquebase.BaseConfig;
import uniquebase.handler.EnchantmentHandler;
import uniquebase.utils.MiscUtil;

@Mixin(EnchantmentMenu.class)
public class EnchTableMixin
{
	@Redirect(method = {"lambda$clickMenuButton$1", "m_39475_"}, 
	at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onEnchantmentPerformed(Lnet/minecraft/world/item/ItemStack;I)V"))
	public void onEnchanted(Player player, ItemStack stack, int level)
	{
		if(BaseConfig.TWEAKS.tableOverride.get()) MiscUtil.drainExperience(player, Mth.ceil(MiscUtil.getXPForLvl(level) * BaseConfig.TWEAKS.tableMultiplier.get()));
		else player.onEnchantmentPerformed(stack, level);
	}
	
	@Inject(method = "getEnchantmentList", at = @At(value = "TAIL", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public void limitEnchantments(ItemStack stack, int unused, int unusde, CallbackInfoReturnable<List<EnchantmentInstance>> info, List<EnchantmentInstance> list)
	{
		EnchantmentHandler.INSTANCE.limitEnchantments(list, stack);
	}
}
