package uniquebase.utils.mixin.common.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.DamageSource;
import uniquebase.utils.EnchantmentContainer;
import uniquebase.utils.MiscUtil;

@Mixin(Entity.class)
public class EntityMixinP {
	
	@Inject(method ="isInvulnerableTo", at = @At("HEAD"), cancellable = true)
	private void isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> ci) {
		if(((Entity)(Object)this) instanceof ItemEntity) {
			ItemEntity ent = (ItemEntity) ((Entity)(Object)this);
			if(source == DamageSource.CACTUS && MiscUtil.getEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, ent.getItem()) > 0) {
				ci.setReturnValue(true);
			} else if((source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE || source == DamageSource.LAVA) && MiscUtil.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, ent.getItem()) > 0) {
				ci.setReturnValue(true);
			} else if(source.isExplosion() && MiscUtil.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, ent.getItem()) > 0) {
				ci.setReturnValue(true);
			}
		}
	}
	
	@Inject(method = "doEnchantDamageEffects", at = @At("HEAD"))
	private void doHurt(LivingEntity owner, Entity target, CallbackInfo ci) {
		EnchantmentContainer.ThornsClass.ENT = target;
		EnchantmentContainer.ThornsClass.ONCE = true;
	}
}
