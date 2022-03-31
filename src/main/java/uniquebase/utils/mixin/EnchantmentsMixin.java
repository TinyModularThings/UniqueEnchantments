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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import uniquebase.UEBase;
import uniquebase.utils.MiscUtil;

@Mixin(Enchantment.class)
public class EnchantmentsMixin {
	
	@Inject(method="getFullname", at=@At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void rebuildName(int level, CallbackInfoReturnable<ITextComponent> ci) {
		boolean obfuscated = true;
		if(FMLEnvironment.dist.isClient()) {
			obfuscated = !isHoveringSlot();
		}
		obfuscated = UEBase.obfuscate.get();
		Enchantment enchantment = ((Enchantment)(Object)this);
		IFormattableTextComponent textComponent = new TranslationTextComponent(enchantment.getDescriptionId());

		textComponent.withStyle(MiscUtil.toColor(UEBase.colorMap.getInt(enchantment)));
		if(obfuscated) {
			textComponent.append(" ").append(new TranslationTextComponent("111").withStyle(TextFormatting.OBFUSCATED));
		}
		else if(level != 1 || enchantment.getMaxLevel() != 1) {
			textComponent.append(" ").append(new TranslationTextComponent("enchantment.level." + level));
		}
		
		ci.setReturnValue(textComponent);
	}
	
	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	private boolean isHoveringSlot() {
		Screen screen = Minecraft.getInstance().screen;
		return screen instanceof ContainerScreen && ((ContainerScreen<?>) screen).getSlotUnderMouse() != null;
	}

}
