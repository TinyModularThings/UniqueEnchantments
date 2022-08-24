package uniquebase.utils.mixin.common.enchantments;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import uniquebase.api.IApexEnchantment;
import uniquebase.utils.events.FishingLuckEvent;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin
{
	@Inject(method = "getAvailableEnchantmentResults", at = @At(value = "TAIL", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
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
	
	@Overwrite
	public static int getFishingLuckBonus(ItemStack stack)
	{
		int level = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.FISHING_LUCK, stack);
		FishingLuckEvent event = new FishingLuckEvent(stack, level);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getLevel();
	}
}
