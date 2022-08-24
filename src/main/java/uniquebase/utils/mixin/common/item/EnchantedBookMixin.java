package uniquebase.utils.mixin.common.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import uniquebase.UEBase;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookMixin
{
	@Overwrite
	public boolean isFoil(ItemStack stack)
	{
		return UEBase.ENCHANTED_GLINT.get();
	}
}
