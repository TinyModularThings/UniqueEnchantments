package uniquebase.utils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookMixin
{
	@Overwrite
	public boolean isFoil(ItemStack stack)
	{
		return false;
	}
}
