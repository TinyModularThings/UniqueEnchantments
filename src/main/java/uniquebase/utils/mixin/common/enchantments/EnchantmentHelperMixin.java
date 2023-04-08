package uniquebase.utils.mixin.common.enchantments;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import uniquebase.api.IApexEnchantment;

@Mixin(value = EnchantmentHelper.class, priority = 999)
public class EnchantmentHelperMixin
{
	@Inject(method = "getAvailableEnchantmentResults", at = @At(value = "RETURN", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void removeApexEnchantments(int level, ItemStack stack, boolean treasure, CallbackInfoReturnable<List<EnchantmentInstance>> result, List<EnchantmentInstance> list) {
		for(int i = 0, m = list.size();i < m;i++)
		{
			if(list.get(i).enchantment instanceof IApexEnchantment)
			{
				list.remove(i--);
				m--;
			}
		}
	}
}
