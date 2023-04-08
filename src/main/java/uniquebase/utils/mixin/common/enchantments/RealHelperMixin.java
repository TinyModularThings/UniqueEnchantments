package uniquebase.utils.mixin.common.enchantments;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import uniquebase.api.IApexEnchantment;

/**
 * 
 * @author Speiger
 * 
 * With Permission of Shadow of fire to ensure Mod Comapt between UE and Apotheosis
 * This simply targets the Enchantment Helper from him instead of vanilla due to him basically overriding everything and deleting our required logic.
 * He could reduce his reach of the mixin but the solution we all came up with is to target his mixins instead if it is found
 *
 */
@Mixin(targets = "shadows.apotheosis.ench.table.RealEnchantmentHelper", remap = false)
public class RealHelperMixin
{
	@Inject(method = "getAvailableEnchantmentResults", at = @At(value = "TAIL", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION, remap = false)
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
