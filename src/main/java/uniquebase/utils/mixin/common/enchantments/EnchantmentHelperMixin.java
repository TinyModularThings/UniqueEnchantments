package uniquebase.utils.mixin.common.enchantments;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import uniquebase.api.IApexEnchantment;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin
{
	@Inject(method = "getAvailableEnchantmentResults", at = @At(value = "TAIL", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public static void removeApexEnchantments(int level, ItemStack stack, boolean treasure, CallbackInfoReturnable<List<EnchantmentData>> result, List<EnchantmentData> list) {
		for(int i = 0,m=list.size();i<m;i++) {
			if(list.get(i).enchantment instanceof IApexEnchantment) {
				list.remove(i--);
				m--;
			}
		}
	}
}
