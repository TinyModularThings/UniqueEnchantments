package uniquebase.utils.mixin.common.enchantments;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

@Mixin(Enchantment.class)
public interface EnchantmentMixin
{
	@Accessor("slots")
	EquipmentSlot[] getSlots();
	
}
