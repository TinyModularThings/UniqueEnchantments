package uniquebase.utils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import uniquebase.UEBase;
import uniquebase.utils.MiscUtil;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	//getItem
	@Inject(method="getHoverName", at=@At("HEAD"), cancellable = true)
	private void nameAdjustment(CallbackInfoReturnable<ITextComponent> ci) {
		ItemStack stack = ((ItemStack)(Object)this);
		if(stack.getItem() == Items.ENCHANTED_BOOK) {
			ListNBT enchantments = EnchantedBookItem.getEnchantments(stack);
			if(enchantments.size() == 1) {
				Enchantment enchantment = MiscUtil.getFirstEnchantment(stack).getKey();
				boolean obfuscated = true;
				if(FMLEnvironment.dist.isClient()) {
					obfuscated = !isHoveringSlot();
				}
				obfuscated = UEBase.obfuscate.get();
				IFormattableTextComponent textComponent = new TranslationTextComponent(enchantment.getDescriptionId());

				textComponent.withStyle(MiscUtil.toColor(UEBase.colorMap.getInt(enchantment)));
				if(obfuscated) {
					textComponent.append(" ").append(new TranslationTextComponent("111").withStyle(TextFormatting.OBFUSCATED));
				}
				
				ci.setReturnValue(textComponent);
			}
		}
	}
	
	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	private boolean isHoveringSlot() {
		Screen screen = Minecraft.getInstance().screen;
		return screen instanceof ContainerScreen && ((ContainerScreen<?>) screen).getSlotUnderMouse() != null;
	}
}
