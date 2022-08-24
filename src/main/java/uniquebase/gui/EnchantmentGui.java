package uniquebase.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class EnchantmentGui extends AbstractContainerScreen<EnchantmentContainer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("uniquebase:textures/enchantments.png");
    int slots = 0;
    Screen parent;
    
	public EnchantmentGui(ItemStack stack, Player player, Screen parent)
	{
		super(new EnchantmentContainer(stack), player.getInventory(), Component.literal("I am error"));
		slots = ((EnchantmentContainer)menu).enchantments.size();
		this.parent = parent;
	}
	
	@Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks)
    {
    	this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }
	
	@Override
	protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY)
	{
		RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(stack, x, y, 0, 0, this.imageWidth, 178);
        for(int i = 0;i<slots;i++)
        {
        	int xPos = x + (i % 8) * 18 + 16;
        	int yPos = y + (i / 8) * 18 + 36;
        	boolean hovered = mouseX >= xPos && mouseX <= xPos + 18 && mouseY >= yPos && mouseY <= yPos + 18; 
        	this.blit(stack, xPos, yPos, hovered ? 196 : 176, 0, 18, 18);
        }
	}
	
	@Override
	public void onClose()
	{
		minecraft.setScreen(parent);
	}
	
	@Override
	protected void renderLabels(PoseStack stack, int mouseX, int mouseY)
	{
		Component s = Component.literal("Applied Enchantments");
        this.font.draw(stack, s, this.imageWidth / 2 - this.font.width(s) / 2, 6, 4210752);
		drawString(stack, font, this.menu.inventory.getItem(0).getDisplayName(), 41, 20, -1);
	}
}
