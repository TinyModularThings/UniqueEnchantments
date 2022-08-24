package uniquebase.gui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TooltipIcon implements TooltipComponent, ClientTooltipComponent
{
	List<ItemStack> list;

	public TooltipIcon(List<ItemStack> list)
	{
		this.list = list;
	}
	
	@Override
	public void renderImage(Font font, int x, int y, PoseStack matrix, ItemRenderer item, int zOffset)
	{
		PoseStack stack = RenderSystem.getModelViewStack();
		stack.pushPose();
		stack.scale(0.5F, 0.5F, 1);
		stack.translate(x, y, 0F);
		for(int i = 0;i<list.size();i++)
		{
			item.renderGuiItem(list.get(i), x + i * 18, y);
		}
		stack.popPose();
		RenderSystem.applyModelViewMatrix();
	}
	
	@Override
	public int getHeight()
	{
		return 10;
	}

	@Override
	public int getWidth(Font font)
	{
		return 9 * list.size();
	}

}
