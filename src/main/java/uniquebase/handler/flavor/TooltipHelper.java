package uniquebase.handler.flavor;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniquebase.UEBase;
import uniqueeutils.handler.UtilsHandler;

public class TooltipHelper {
	
	static Map<ItemStack, Float> speedMap = new HashMap<>();
	
	public static void addStatTooltip(ItemStack stack, ItemTooltipEvent event) {
		
		PlayerEntity player = event.getPlayer();
		if(player != null) {
			if(stack.getItem() instanceof TieredItem) {
				event.getToolTip().add(new TranslationTextComponent("Mining Speed: " + ((TieredItem)stack.getItem()).getTier().getSpeed()));
				event.getToolTip().add(new TranslationTextComponent("Mining Level: " + ((TieredItem)stack.getItem()).getTier().getLevel()));
			}
		}
	}
	
}
