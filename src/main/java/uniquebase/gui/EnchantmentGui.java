package uniquebase.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class EnchantmentGui extends ContainerScreen<EnchantmentContainer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("uniquebase:textures/enchantments.png");
    int slots = 0;
    
	public EnchantmentGui(ItemStack stack, PlayerEntity player)
	{
		super(new EnchantmentContainer(stack), player.inventory, new StringTextComponent("I am error"));
		slots = ((EnchantmentContainer)container).enchantments.size();
	}
	
	@Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
    	this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.blit(x, y, 0, 0, this.xSize, 178);
        for(int i = 0;i<slots;i++)
        {
        	int xPos = x + (i % 8) * 18 + 16;
        	int yPos = y + (i / 8) * 18 + 36;
        	boolean hovered = mouseX >= xPos && mouseX <= xPos + 18 && mouseY >= yPos && mouseY <= yPos + 18; 
        	this.blit(xPos, yPos, hovered ? 196 : 176, 0, 18, 18);
        }
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String s = "Applied Enchantments";
        this.font.drawString(s, this.xSize / 2 - this.font.getStringWidth(s) / 2, 6, 4210752);
		drawString(font, this.container.getInventory().get(0).getDisplayName().getFormattedText(), 41, 20, -1);
	}
}
