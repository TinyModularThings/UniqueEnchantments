package uniquebase.utils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;

@Mixin(AbstractArrowEntity.class)
public interface ArrowMixin
{
	@Invoker("getPickupItem")
	public ItemStack getPickupItem();
}
