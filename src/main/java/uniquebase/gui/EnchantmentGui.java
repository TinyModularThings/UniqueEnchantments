package uniquebase.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentGui extends GuiContainer
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("uniquebase:textures/enchantments.png");
    int slots = 0;
    
	public EnchantmentGui(ItemStack stack)
	{
		super(new EnchantmentContainer(stack));
		slots = ((EnchantmentContainer)inventorySlots).enchantments.size();
	}
	
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, 178);
        for(int i = 0;i<slots;i++)
        {
        	int xPos = x + (i % 8) * 18 + 16;
        	int yPos = y + (i / 8) * 18 + 36;
        	boolean hovered = mouseX >= xPos && mouseX <= xPos + 18 && mouseY >= yPos && mouseY <= yPos + 18; 
        	this.drawTexturedModalRect(xPos, yPos, hovered ? 196 : 176, 0, 18, 18);
        }
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String s = "Applied Enchantments";
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		drawString(fontRenderer, this.inventorySlots.getInventory().get(0).getDisplayName(), 41, 20, -1);
	}
}
