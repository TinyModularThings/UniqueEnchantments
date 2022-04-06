package uniquebase.utils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uniquebase.UEBase;
import uniquebase.utils.MiscUtil;

@Mixin(Enchantment.class)
public class EnchantmentsMixin {
	
	@Inject(method="getFullname", at=@At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void rebuildName(int level, CallbackInfoReturnable<ITextComponent> ci) {
		Enchantment enchantment = ((Enchantment)(Object)this);
		IFormattableTextComponent textComponent = new TranslationTextComponent(enchantment.getDescriptionId());

		textComponent.withStyle(MiscUtil.toColor(UEBase.COLOR_MAP.getOrDefault(enchantment, enchantment.isCurse() ? 0xFF5555 : UEBase.COLOR_MAP.defaultReturnValue())));
		
		if(enchantment.getMaxLevel() != 1) {
			textComponent.append(" ").append(new TranslationTextComponent("enchantment.level." + level));
		}
		
		ci.setReturnValue(textComponent);
	}
	
	
	@OnlyIn(Dist.CLIENT)
	private boolean isHoveringSlot() {
		Screen screen = Minecraft.getInstance().screen;
		return screen instanceof ContainerScreen && ((ContainerScreen<?>) screen).getSlotUnderMouse() != null;
	}

}
