package uniquebase.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class EnchantmentGui extends ContainerScreen<EnchantmentContainer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("uniquebase:textures/enchantments.png");
    int slots = 0;
    
	public EnchantmentGui(ItemStack stack, PlayerEntity player)
	{
		super(new EnchantmentContainer(stack), player.inventory, new StringTextComponent("I am error"));
		slots = ((EnchantmentContainer)menu).enchantments.size();
	}
	
	@Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks)
    {
    	this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }
	
	@Override
	protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY)
	{
        this.minecraft.getTextureManager().bind(TEXTURE);
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
	protected void renderLabels(MatrixStack stack, int mouseX, int mouseY)
	{
		ITextComponent s = new StringTextComponent("Applied Enchantments");
        this.font.draw(stack, s, this.imageWidth / 2 - this.font.width(s) / 2, 6, 4210752);
		drawString(stack, font, this.menu.inventory.getItem(0).getDisplayName(), 41, 20, -1);
	}
}
