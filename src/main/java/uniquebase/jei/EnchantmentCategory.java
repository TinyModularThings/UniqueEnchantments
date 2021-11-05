package uniquebase.jei;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

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
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
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
		EnchantedBookItem.addEnchantment(stack, new EnchantmentData(Enchantments.BLOCK_FORTUNE, 100));
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
		return I18n.get("unique.base.jei.name");
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
	public void draw(WrappedEnchantment recipe, MatrixStack matrix, double mouseX, double mouseY)
	{
		Minecraft mc = Minecraft.getInstance();
		FontRenderer font = mc.font;
		String s = TextFormatting.UNDERLINE+I18n.get(recipe.ench.getDescriptionId());
		font.draw(matrix, s, 85 - (font.width(s) / 2), 3, 0);
		font.draw(matrix, I18n.get("unique.base.jei.max_level", TextFormatting.WHITE.toString()+recipe.ench.getMaxLevel()), 3, 18, 0x1CD679);
		font.draw(matrix, I18n.get("unique.base.jei.treasure"), 3, 28, 0xBA910D);
		font.draw(matrix, I18n.get("unique.base.jei.curse"), 3, 38, 0xA11E15);
		font.draw(matrix, I18n.get("unique.base.jei.tradeable"), 3, 48, 0xC98414);
		font.draw(matrix, I18n.get("unique.base.jei.rarity", recipe.getFormatting(recipe.ench.getRarity())+I18n.get("unique.base.jei."+recipe.ench.getRarity().name().toLowerCase())), 3, 58, 0xCB19D1);
		s = recipe.getDescription();
		if((font.wordWrapHeight(s, 95) / font.lineHeight) > 7)
		{
			matrix.scale(0.5F, 0.5F, 1F);
			int i = 0;
			for(IReorderingProcessor entry : font.split(new StringTextComponent(s), 190))
			{
				font.draw(matrix, entry, 138, 144+(i*font.lineHeight), 0);
				i++;
			}
			matrix.scale(2F, 2F, 1F);
		}
		else
		{
			int i = 0;
			for(IReorderingProcessor entry : font.split(new StringTextComponent(s), 95))
			{
				font.draw(matrix, entry, 69, 72+(i*font.lineHeight), 0);
				i++;
			}
		}
		font.draw(matrix, ""+recipe.pageIndex, 28, 70, 0);
		List<IReorderingProcessor> incomp = recipe.getIncompats(font);
		int rows = MathHelper.ceil(incomp.size() / 9D);
		int start = recipe.pageIndex * 9;
		matrix.scale(0.5F, 0.5F, 1F);
		font.draw(matrix, I18n.get("unique.base.jei.incompats"), 5, 162, 0);
		for(int i = 0;i<11&&start+i<incomp.size();i++)
		{
			font.draw(matrix, incomp.get(start+i), 5, 182 + (i * font.lineHeight), 0);
		}
		mc.getTextureManager().bind(new ResourceLocation("textures/gui/container/beacon.png"));
		AbstractGui.blit(matrix, 3 + (font.width(I18n.get("unique.base.jei.treasure")) * 2), 55, 90 + (recipe.ench.isTreasureOnly() ? 0 : 22), 220, 18, 18, 256, 256);
		AbstractGui.blit(matrix, 3 + (font.width(I18n.get("unique.base.jei.curse")) * 2), 74, 90 + (recipe.ench.isCurse() ? 0 : 22), 220, 18, 18, 256, 256);
		AbstractGui.blit(matrix, 3 + (font.width(I18n.get("unique.base.jei.tradeable")) * 2), 95, 90 + (recipe.ench.isTradeable() ? 0 : 22), 220, 18, 18, 256, 256);
		matrix.scale(2F, 2F, 1F);
		recipe.left.active = recipe.pageIndex > 0;
		recipe.right.active = recipe.pageIndex < rows - 1;
		recipe.left.render(matrix, (int)mouseX, (int)mouseY, mc.getFrameTime());
		recipe.right.render(matrix, (int)mouseX, (int)mouseY, mc.getFrameTime());
	}

	@Override
	public boolean handleClick(WrappedEnchantment recipe, double mouseX, double mouseY, int mouseButton)
	{
		Minecraft mc = Minecraft.getInstance();
		if(recipe.left.isMouseOver(mouseX, mouseY) && recipe.pageIndex > 0)
		{
			recipe.left.playDownSound(mc.getSoundManager());
			recipe.pageIndex--;
			return true;
		}
		if(recipe.right.isMouseOver(mouseX, mouseY))
		{
			int rows = MathHelper.ceil(recipe.getIncompats(mc.font).size() / 9D);
			if(recipe.pageIndex < rows - 1)
			{
				recipe.right.playDownSound(mc.getSoundManager());
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
