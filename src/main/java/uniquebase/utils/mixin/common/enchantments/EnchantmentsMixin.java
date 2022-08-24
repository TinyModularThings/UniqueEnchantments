package uniquebase.utils.mixin.common.enchantments;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import uniquebase.utils.MiscUtil;

@Mixin(Enchantment.class)
public class EnchantmentsMixin {
	
	@Inject(method="getFullname", at=@At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void rebuildName(int level, CallbackInfoReturnable<Component> ci) {
		ci.setReturnValue(MiscUtil.createEnchantmentName(((Enchantment)(Object)this), level, true));
	}
	
	@Inject(method ="canEnchant", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onExposionCheck(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		Enchantment ench = ((Enchantment)(Object)this);
		if(ench == Enchantments.ALL_DAMAGE_PROTECTION || ench == Enchantments.PROJECTILE_PROTECTION || ench == Enchantments.FALL_PROTECTION || ench == Enchantments.BLAST_PROTECTION || ench == Enchantments.FIRE_PROTECTION) {
			ci.setReturnValue(true);
		}
	}
	
	@Inject(method ="canApplyAtEnchantingTable", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION, remap = false)
	private void isTableCompatible(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		Enchantment ench = ((Enchantment)(Object)this);
		Item item = stack.getItem();
		if(ench == Enchantments.IMPALING && (item instanceof SwordItem || item instanceof AxeItem || item instanceof HoeItem)) {
			ci.setReturnValue(true);
			return;
		}
		if(ench == Enchantments.THORNS && item instanceof ArmorItem) {
			ci.setReturnValue(true);
			return;
		}
		if(item instanceof TridentItem && (ench == Enchantments.SHARPNESS || ench == Enchantments.SMITE || ench == Enchantments.BANE_OF_ARTHROPODS || ench == Enchantments.KNOCKBACK || ench == Enchantments.FIRE_ASPECT)) {
			ci.setReturnValue(true);
		}
	}
	
	@Inject(method="checkCompatibility", at=@At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void compat(Enchantment ench, CallbackInfoReturnable<Boolean> ci) {
		Enchantment self = ((Enchantment)(Object)this);
		if(self == Enchantments.IMPALING && ench == Enchantments.SHARPNESS) {
			ci.setReturnValue(false);
			return;
		}
		if(self == Enchantments.SHARPNESS && ench == Enchantments.IMPALING) {
			ci.setReturnValue(false);
		}
	}
}