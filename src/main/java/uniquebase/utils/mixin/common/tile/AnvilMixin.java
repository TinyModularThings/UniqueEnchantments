package uniquebase.utils.mixin.common.tile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import uniquebase.BaseConfig;
import uniquebase.handler.EnchantmentHandler;
import uniquebase.utils.MiscUtil;

@Mixin(AnvilMenu.class)
public class AnvilMixin
{
	@Redirect(method = "onTake", 
	at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;giveExperienceLevels(I)V"))
	public void drainXP(Player player, int levels)
	{
		if(BaseConfig.TWEAKS.anvilOverride.get()) MiscUtil.drainExperience(player, Mth.ceil(MiscUtil.getXPForLvl(-levels) * BaseConfig.TWEAKS.anvilMultiplier.get()));
		else player.giveExperienceLevels(levels);
	}
	
	ItemStack currentAnvilItem = ItemStack.EMPTY;
	
	@ModifyArg(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;setEnchantments(Ljava/util/Map;Lnet/minecraft/world/item/ItemStack;)V"))
	public ItemStack modifyItemsToEnchant(ItemStack stack) {
		currentAnvilItem = stack;
		return stack;
	}
	
	@Inject(method = "createResult", 
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;setEnchantments(Ljava/util/Map;Lnet/minecraft/world/item/ItemStack;)V", shift = Shift.AFTER))
	public void limitEnchantments(CallbackInfo info) {
		CompoundTag nbt = currentAnvilItem.getTag();
		if(nbt == null) nbt = new CompoundTag();
		EnchantmentHandler.INSTANCE.limitEnchantments(nbt.getList(currentAnvilItem.getItem() == Items.ENCHANTED_BOOK ? "StoredEnchantments" : "Enchantments", 10), currentAnvilItem);
	}
}
