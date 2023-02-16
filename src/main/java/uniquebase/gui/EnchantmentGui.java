package uniquebase.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class EnchantmentGui extends AbstractContainerScreen<EnchantmentContainer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("uniquebase:textures/enchantments.png");
    Screen parent;
    Button up;
    Button down;
    Button applied;
    Button applicable;
    
	public EnchantmentGui(ItemStack stack, Player player, Screen parent)
	{
		super(new EnchantmentContainer(stack), player.getInventory(), Component.literal("I am error"));
		this.parent = parent;
	}
	
	@Override
	protected void init()
	{
		clearWidgets();
		super.init();
		int x = getGuiLeft();
		int y = getGuiTop();
		down = addRenderableWidget(new ExtendedButton(x+15, y+35, 14, 14, Component.literal("+"), this::down));
		up = addRenderableWidget(new ExtendedButton(x+147, y+35, 14, 14, Component.literal("-"), this::up));
		applied = addRenderableWidget(new ExtendedButton(x+30, y+35, 55, 14, Component.literal("Applied"), this::applied));
		applicable = addRenderableWidget(new ExtendedButton(x+86, y+35, 60, 14, Component.literal("Applicable"), this::applicable));
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
        int slots = menu.getInvSlots();
        this.up.active = menu.getOffset() > 0;
        this.down.active = menu.canMoveDown();
        this.applicable.active = menu.isPresent();
        this.applied.active = !menu.isPresent();
        for(int i = 0;i<slots;i++)
        {
        	int xPos = x + (i % 8) * 18 + 16;
        	int yPos = y + (i / 8) * 18 + 51;
        	boolean hovered = mouseX >= xPos && mouseX <= xPos + 18 && mouseY >= yPos && mouseY <= yPos + 18; 
        	this.blit(stack, xPos, yPos, hovered ? 196 : 176, 0, 18, 18);
        }
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
	{
		menu.scroll(-((int)scroll));
		return true;
	}
	
	private void up(Button button)
	{
		menu.scroll(-1);
	}
	
	private void down(Button button)
	{
		menu.scroll(1);
	}
	
	private void applied(Button button)
	{
		menu.toggle(true);
	}
	
	private void applicable(Button button)
	{
		menu.toggle(false);
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
