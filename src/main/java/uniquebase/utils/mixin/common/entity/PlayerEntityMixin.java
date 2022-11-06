package uniquebase.utils.mixin.common.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import uniquebase.utils.MiscUtil;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

	@ModifyVariable(method = "getDigSpeed", at = @At(value = "STORE", ordinal = 1), remap = false)
    public float overrideEff(float speed) {
		PlayerEntity ent = ((PlayerEntity)(Object)this);
		int level = 0;
		for(ItemStack stack:ent.getHandSlots()) {
			if(!stack.isEmpty()) {
				level += MiscUtil.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, stack);
			}
		}
		speed -= Math.pow(level,2) + 1;
        return speed + 6 + level * 4;
    }
	
}
