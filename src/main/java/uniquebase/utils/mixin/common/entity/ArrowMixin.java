package uniquebase.utils.mixin.common.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

@Mixin(AbstractArrow.class)
public interface ArrowMixin
{
	@Invoker("getPickupItem")
	public ItemStack getArrowItem();
}
