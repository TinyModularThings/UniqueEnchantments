package uniquebase.utils.mixin.common.enchantments;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import uniquebase.utils.events.FishingLuckEvent;

@Mixin(EnchantmentHelper.class)
public class FishingLuckMixin
{
	@Overwrite
	public static int getFishingLuckBonus(ItemStack stack)
	{
		int level = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.FISHING_LUCK, stack);
		FishingLuckEvent event = new FishingLuckEvent(stack, level);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getLevel();
	}
}
