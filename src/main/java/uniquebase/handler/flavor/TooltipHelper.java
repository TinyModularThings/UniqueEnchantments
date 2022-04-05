package uniquebase.handler.flavor;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class TooltipHelper {
	
	static Map<ItemStack, Float> speedMap = new HashMap<>();
	
	public static void addStatTooltip(ItemStack stack, ItemTooltipEvent event) {
		
		PlayerEntity player = event.getPlayer();
		if(player != null) {
			if(stack.getItem() instanceof TieredItem) {
				if(((TieredItem)stack.getItem()).getTier().getSpeed() > 1.1) {
					event.getToolTip().add(new TranslationTextComponent("Mining Speed: " + ((TieredItem)stack.getItem()).getTier().getSpeed()));
					event.getToolTip().add(new TranslationTextComponent("Mining Level: " + ((TieredItem)stack.getItem()).getTier().getLevel()));
				}
				
			}
		}
	}
	
}
