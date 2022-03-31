package uniquebase.utils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;

@Mixin(Enchantment.class)
public interface EnchantmentMixin
{
	@Accessor("slots")
	EquipmentSlotType[] getSlots();
	
}
