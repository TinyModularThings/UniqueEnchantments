package uniquebase.utils.mixin.common.tile;

import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.ForgeRegistries;
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
	
	@Redirect(method = "createResult", 
			at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;setEnchantments(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V"))
	public void limitEnchantments(Map<Enchantment, Integer> enchantments, ItemStack stack)
	{
		ListNBT list = new ListNBT();
		for(Entry<Enchantment, Integer> entry : enchantments.entrySet())
		{
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("id", ForgeRegistries.ENCHANTMENTS.getKey(entry.getKey()).toString());
            nbt.putShort("lvl", entry.getValue().shortValue());
            list.add(nbt);
		}
		EnchantmentHandler.INSTANCE.limitEnchantments(list, stack);
	}
}
