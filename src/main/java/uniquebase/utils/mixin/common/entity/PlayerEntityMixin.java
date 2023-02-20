package uniquebase.utils.mixin.common.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import uniquebase.utils.MiscUtil;

@Mixin(value = Player.class, priority = 1001)
public class PlayerEntityMixin {

	@ModifyVariable(method = "getDigSpeed", at = @At(value = "STORE", ordinal = 1), remap = false)
    public float overrideEff(float speed) {
		System.out.println(speed);
		Player ent = ((Player)(Object)this);
		int level = 0;
		ItemStack stack = ent.getMainHandItem();
		if(!stack.isEmpty()) {
			level += MiscUtil.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, stack);
		}
		speed -= Math.pow(level,2) + 1;
        return speed + 6 + level * 4;
    }
	
}
