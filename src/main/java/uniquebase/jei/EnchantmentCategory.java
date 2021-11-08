package uniquebase.jei;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnchantmentCategory implements IRecipeCategory<WrappedEnchantment>
{
	IDrawable drawable;
	IDrawable icon;
	
	public EnchantmentCategory(IGuiHelper helper)
	{
		drawable = helper.createDrawable(new ResourceLocation("uniquebase:textures/jei.png"), 4, 5, 167, 138);
		ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
		EnchantedBookItem.addEnchantment(stack, new EnchantmentData(Enchantments.FORTUNE, 100));
		icon = helper.createDrawableIngredient(stack);
	}
	
	@Override
	public ResourceLocation getUid()
	{
		return new ResourceLocation("uniquebase", "ue_enchantments");
	}

	@Override
	public String getTitle()
	{
		return I18n.format("unique.base.jei.name");
	}
	
	@Override
	public Class<? extends WrappedEnchantment> getRecipeClass()
	{
		return WrappedEnchantment.class;
	}
	
	@Override
	public IDrawable getIcon()
	{
		return icon;
	}
	
	@Override
	public IDrawable getBackground()
	{
		return drawable;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void draw(WrappedEnchantment recipe, double mouseX, double mouseY)
	{
		Minecraft mc = Minecraft.getInstance();
		FontRenderer font = mc.fontRenderer;
		String s = TextFormatting.UNDERLINE+I18n.format(recipe.ench.getName());
		font.drawString(s, 85 - (font.getStringWidth(s) / 2), 3, 0);
		font.drawString(I18n.format("unique.base.jei.max_level", TextFormatting.WHITE.toString()+recipe.ench.getMaxLevel()), 3, 18, 0x1CD679);
		font.drawString(I18n.format("unique.base.jei.treasure"), 3, 28, 0xBA910D);
		font.drawString(I18n.format("unique.base.jei.curse"), 3, 38, 0xA11E15);
		font.drawString(I18n.format("unique.base.jei.rarity", recipe.getFormatting(recipe.ench.getRarity())+I18n.format("unique.base.jei."+recipe.ench.getRarity().name().toLowerCase())), 3, 48, 0xCB19D1);
		s = recipe.getDescription();
		if((font.getWordWrappedHeight(s, 95) / font.FONT_HEIGHT) > 7)
		{
			GlStateManager.scaled(0.5D, 0.5D, 1D);
			font.drawSplitString(s, 138, 126, 190, 0);
			GlStateManager.scaled(2D, 2D, 1D);
		}
		else font.drawSplitString(s, 69, 63, 95, 0);
		font.drawString(""+recipe.pageIndex, 28, 60, 0);
		List<String> incomp = recipe.getIncompats(font);
		int start = recipe.pageIndex * 10;
		GlStateManager.scaled(0.5D, 0.5D, 1D);
		font.drawString(I18n.format("unique.base.jei.incompats"), 5, 144, 0);
		for(int i = 0;i<11&&start+i<incomp.size();i++)
		{
			font.drawString(incomp.get(start+i), 5, 164 + (i * font.FONT_HEIGHT), 0);
		}
		GlStateManager.scaled(2D, 2D, 1D);
		mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/beacon.png"));
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.scaled(0.5D, 0.5D, 1D);
		AbstractGui.blit(3 + (font.getStringWidth(I18n.format("unique.base.jei.treasure")) * 2), 55, 90 + (recipe.ench.isTreasureEnchantment() ? 0 : 22), 220, 18, 18, 256, 256);
		AbstractGui.blit(3 + (font.getStringWidth(I18n.format("unique.base.jei.curse")) * 2), 74, 90 + (recipe.ench.isCurse() ? 0 : 22), 220, 18, 18, 256, 256);
		GlStateManager.scaled(2D, 2D, 1D);
		recipe.left.active = recipe.pageIndex > 0;
		recipe.right.active = recipe.pageIndex < incomp.size() / 11;
		recipe.left.render((int)mouseX, (int)mouseY, mc.getRenderPartialTicks());
		recipe.right.render((int)mouseX, (int)mouseY, mc.getRenderPartialTicks());
	}

	@Override
	public boolean handleClick(WrappedEnchantment recipe, double mouseX, double mouseY, int mouseButton)
	{
		Minecraft mc = Minecraft.getInstance();
		if(recipe.left.isMouseOver(mouseX, mouseY) && recipe.pageIndex > 0)
		{
			recipe.left.playDownSound(mc.getSoundHandler());
			recipe.pageIndex--;
			return true;
		}
		if(recipe.right.isMouseOver(mouseX, mouseY))
		{
			if(recipe.pageIndex < recipe.getIncompats(mc.fontRenderer).size() / 11)
			{
				recipe.right.playDownSound(mc.getSoundHandler());
				recipe.pageIndex++;
				return true;
			}
		}
		return false;
	}

	@Override
	public void setRecipe(IRecipeLayout layout, WrappedEnchantment wrapper, IIngredients ingredients)
	{
		IGuiItemStackGroup guiItemStacks = layout.getItemStacks();
		guiItemStacks.init(0, true, 89, 17);
		for(int i = 0;i<6;i++)
		{
			guiItemStacks.init(1+i, true, 112 + ((i % 3) * 18), 19 + ((i / 3) * 18));
		}
		guiItemStacks.set(ingredients);
	}
	
	@Override
	public void setIngredients(WrappedEnchantment wrapper, IIngredients ingredients)
	{
		List<List<ItemStack>> list = new ObjectArrayList<>();
		for(int i = 0;i<7;i++) list.add(new ObjectArrayList<>());
		for(int i = 0,m=wrapper.validItems.size();i<m;i++)
		{
			list.get(1 + (i % 6)).add(wrapper.validItems.get(i));
		}
		for(int i = wrapper.ench.getMinLevel(),m=wrapper.ench.getMaxLevel();i<=m;i++)
		{
			ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
			EnchantedBookItem.addEnchantment(stack, new EnchantmentData(wrapper.ench, i));
			list.get(0).add(stack);
		}
		ingredients.setInputLists(VanillaTypes.ITEM, list);
	}
}
