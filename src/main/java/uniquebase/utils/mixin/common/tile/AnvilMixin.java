package uniquebase.utils.mixin.common.tile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import uniquebase.UEBase;
import uniquebase.handler.EnchantmentHandler;
import uniquebase.utils.MiscUtil;

@Mixin(RepairContainer.class)
public class AnvilMixin
{
	@Redirect(method = "Lnet/minecraft/inventory/container/RepairContainer;onTake", 
	at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;giveExperienceLevels(I)V"))
	public void drainXP(PlayerEntity player, int levels)
	{
		if(UEBase.XP_OVERRIDE_ANVIL.get()) MiscUtil.drainExperience(player, MathHelper.ceil(MiscUtil.getXPForLvl(-levels) * UEBase.XP_MULTIPLIER_ANVIL.get()));
		else player.giveExperienceLevels(levels);
	}
	
	ItemStack currentAnvilItem = ItemStack.EMPTY;
	
	@ModifyArg(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;setEnchantments(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V"))
	public ItemStack modifyItemsToEnchant(ItemStack stack) {
		currentAnvilItem = stack;
		return stack;
	}
	
	@Inject(method = "createResult", 
			at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;setEnchantments(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V", shift = Shift.AFTER))
	public void limitEnchantments(CallbackInfo info) {
		CompoundNBT nbt = currentAnvilItem.getTag();
		if(nbt == null) nbt = new CompoundNBT();
		EnchantmentHandler.INSTANCE.limitEnchantments(nbt.getList(currentAnvilItem.getItem() == Items.ENCHANTED_BOOK ? "StoredEnchantments" : "Enchantments", 10), currentAnvilItem);
	}
}
