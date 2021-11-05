package uniquebase.jei;

import java.util.Collections;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnchantmentWrapper implements IRecipeWrapper
{
	WrappedEnchantment enchantment;
	int pageIndex = 0;
	GuiButtonExt left = new GuiButtonExt(0, 1, 59, 10, 10, "<");
	GuiButtonExt right = new GuiButtonExt(0, 54, 59, 10, 10, ">");
	
	public EnchantmentWrapper(WrappedEnchantment enchantment)
	{
		this.enchantment = enchantment;
	}
	
	@Override
	public void getIngredients(IIngredients ingridients)
	{
		List<List<ItemStack>> list = new ObjectArrayList<>();
		for(int i = 0;i<7;i++) list.add(new ObjectArrayList<>());
		for(int i = 0,m=enchantment.validItems.size();i<m;i++)
		{
			list.get(1 + (i % 6)).add(enchantment.validItems.get(i));
		}
		for(int i = enchantment.ench.getMinLevel(),m=enchantment.ench.getMaxLevel();i<=m;i++)
		{
			ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
			ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchantment.ench, i));
			list.get(0).add(stack);
		}
		ingridients.setInputLists(VanillaTypes.ITEM, list);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		FontRenderer font = minecraft.fontRenderer;
		String s = TextFormatting.UNDERLINE+I18n.format(enchantment.ench.getName());
		font.drawString(s, 85 - (font.getStringWidth(s) / 2), 3, 0);
		font.drawString(I18n.format("unique.base.jei.max_level", TextFormatting.WHITE.toString()+enchantment.ench.getMaxLevel()), 3, 18, 0x1CD679);
		font.drawString(I18n.format("unique.base.jei.treasure"), 3, 28, 0xBA910D);
		font.drawString(I18n.format("unique.base.jei.curse"), 3, 38, 0xA11E15);
		font.drawString(I18n.format("unique.base.jei.rarity", getFormatting(enchantment.ench.getRarity())+I18n.format("unique.base.jei."+enchantment.ench.getRarity().name().toLowerCase())), 3, 48, 0xCB19D1);
		s = getDescription();
		if((font.getWordWrappedHeight(s, 95) / font.FONT_HEIGHT) > 7)
		{
			GlStateManager.scale(0.5D, 0.5D, 1D);
			font.drawSplitString(s, 138, 126, 190, 0);
			GlStateManager.scale(2D, 2D, 1D);
		}
		else font.drawSplitString(s, 69, 63, 95, 0);
		font.drawString(""+pageIndex, 28, 60, 0);
		List<String> incomp = getIncompats(font);
		int rows = MathHelper.ceil(incomp.size() / 11D);
		int start = pageIndex * 11;
		GlStateManager.scale(0.5D, 0.5D, 1D);
		font.drawString(I18n.format("unique.base.jei.incompats"), 5, 144, 0);
		for(int i = 0;i<11&&start+i<incomp.size();i++)
		{
			font.drawString(incomp.get(start+i), 5, 164 + (i * font.FONT_HEIGHT), 0);
		}
		GlStateManager.scale(2D, 2D, 1D);
		minecraft.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/beacon.png"));
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.scale(0.5D, 0.5D, 1D);
		Gui.drawModalRectWithCustomSizedTexture(3 + (font.getStringWidth(I18n.format("unique.base.jei.treasure")) * 2), 55, 90 + (enchantment.ench.isTreasureEnchantment() ? 0 : 22), 220, 18, 18, 256, 256);
		Gui.drawModalRectWithCustomSizedTexture(3 + (font.getStringWidth(I18n.format("unique.base.jei.curse")) * 2), 74, 90 + (enchantment.ench.isCurse() ? 0 : 22), 220, 18, 18, 256, 256);
		GlStateManager.scale(2D, 2D, 1D);
		left.enabled = pageIndex > 0;
		right.enabled = pageIndex < rows - 1;
		left.drawButton(minecraft, mouseX, mouseY, minecraft.getRenderPartialTicks());
		right.drawButton(minecraft, mouseX, mouseY, minecraft.getRenderPartialTicks());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton)
	{
		if(left.mousePressed(minecraft, mouseX, mouseY) && pageIndex > 0)
		{
			left.playPressSound(minecraft.getSoundHandler());
			pageIndex--;
			return true;
		}
		if(right.mousePressed(minecraft, mouseX, mouseY))
		{
			int rows = MathHelper.ceil(getIncompats(minecraft.fontRenderer).size() / 11D);
			if(pageIndex < rows - 1)
			{
				right.playPressSound(minecraft.getSoundHandler());
				pageIndex++;
				return true;
			}
		}
		return false;
	}
	
	private List<String> getIncompats(FontRenderer font)
	{
		List<String> list = new ObjectArrayList<>();
		if(enchantment.incompats.isEmpty())
		{
			list.addAll(font.listFormattedStringToWidth("- "+I18n.format("unique.base.jei.no.incompat"), 122));
		}
		else
		{
			for(Enchantment ench : enchantment.incompats)
			{
				list.addAll(font.listFormattedStringToWidth("- "+I18n.format(ench.getName()), 122));
			}
		}
		return list;
	}
	
	private String getDescription()
	{
		String s = I18n.format("enchantment."+enchantment.ench.getRegistryName().getNamespace()+"."+enchantment.ench.getRegistryName().getPath()+".desc");
		if(s.startsWith("enchantment.")) return I18n.format("unique.base.jei.no.description");
		return s;
	}
	
	private TextFormatting getFormatting(Rarity rarity)
	{
		switch(rarity)
		{
			case COMMON: return TextFormatting.WHITE;
			case RARE: return TextFormatting.AQUA;
			case UNCOMMON: return TextFormatting.YELLOW;
			case VERY_RARE: return TextFormatting.LIGHT_PURPLE;
			default: return TextFormatting.OBFUSCATED;
		}
	}
	
	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY)
	{
		return Collections.emptyList();
	}
}
