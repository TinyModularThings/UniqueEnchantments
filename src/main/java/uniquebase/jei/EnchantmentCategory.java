package uniquebase.jei;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnchantmentCategory implements IRecipeCategory<WrappedEnchantment>
{
	RecipeType<WrappedEnchantment> id;

	IDrawable drawable;
	IDrawable icon;
	
	public EnchantmentCategory(IGuiHelper helper, RecipeType<WrappedEnchantment> id)
	{
		this.id = id;
		drawable = helper.createDrawable(new ResourceLocation("uniquebase:textures/jei.png"), 4, 5, 167, 138);
		ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
		EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(Enchantments.BLOCK_FORTUNE, 100));
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
	}
	
	@Override
	public RecipeType<WrappedEnchantment> getRecipeType()
	{
		return id;
	}
	
	@Override
	public Component getTitle()
	{
		return Component.translatable("unique.base.jei.name");
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
	public void draw(WrappedEnchantment recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrix, double mouseX, double mouseY)
	{
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		String s = ChatFormatting.UNDERLINE+I18n.get(recipe.ench.getDescriptionId());
		font.draw(matrix, s, 85 - (font.width(s) / 2), 3, 0);
		font.draw(matrix, I18n.get("unique.base.jei.max_level", ChatFormatting.WHITE.toString()+recipe.ench.getMaxLevel()), 3, 18, 0x1CD679);
		font.draw(matrix, I18n.get("unique.base.jei.treasure"), 3, 28, 0xBA910D);
		font.draw(matrix, I18n.get("unique.base.jei.curse"), 3, 38, 0xA11E15);
		font.draw(matrix, I18n.get("unique.base.jei.tradeable"), 3, 48, 0xC98414);
		font.draw(matrix, I18n.get("unique.base.jei.rarity", recipe.getFormatting(recipe.ench.getRarity())+I18n.get("unique.base.jei."+recipe.ench.getRarity().name().toLowerCase())), 3, 58, 0xCB19D1);
		s = recipe.getDescription();
		if((font.wordWrapHeight(s, 95) / font.lineHeight) > 7)
		{
			matrix.scale(0.5F, 0.5F, 1F);
			int i = 0;
			for(FormattedCharSequence entry : font.split(Component.literal(s), 190))
			{
				font.draw(matrix, entry, 138, 144+(i*font.lineHeight), 0);
				i++;
			}
			matrix.scale(2F, 2F, 1F);
		}
		else
		{
			int i = 0;
			for(FormattedCharSequence entry : font.split(Component.literal(s), 95))
			{
				font.draw(matrix, entry, 69, 72+(i*font.lineHeight), 0);
				i++;
			}
		}
		font.draw(matrix, ""+recipe.pageIndex, 28, 70, 0);
		List<FormattedCharSequence> incomp = recipe.getIncompats(font);
		int start = recipe.pageIndex * 9;
		matrix.scale(0.5F, 0.5F, 1F);
		font.draw(matrix, I18n.get("unique.base.jei.incompats"), 5, 162, 0);
		for(int i = 0;i<10&&start+i<incomp.size();i++)
		{
			font.draw(matrix, incomp.get(start+i), 5, 182 + (i * font.lineHeight), 0);
		}
		RenderSystem.setShaderTexture(0, new ResourceLocation("textures/gui/container/beacon.png"));
		Gui.blit(matrix, 3 + (font.width(I18n.get("unique.base.jei.treasure")) * 2), 55, 90 + (recipe.ench.isTreasureOnly() ? 0 : 22), 220, 18, 18, 256, 256);
		Gui.blit(matrix, 3 + (font.width(I18n.get("unique.base.jei.curse")) * 2), 74, 90 + (recipe.ench.isCurse() ? 0 : 22), 220, 18, 18, 256, 256);
		Gui.blit(matrix, 3 + (font.width(I18n.get("unique.base.jei.tradeable")) * 2), 95, 90 + (recipe.ench.isTradeable() ? 0 : 22), 220, 18, 18, 256, 256);
		matrix.scale(2F, 2F, 1F);
		recipe.left.active = recipe.pageIndex > 0;
		recipe.right.active = recipe.pageIndex < incomp.size() / 10;
		recipe.left.render(matrix, (int)mouseX, (int)mouseY, mc.getFrameTime());
		recipe.right.render(matrix, (int)mouseX, (int)mouseY, mc.getFrameTime());
	}
	
	@Override
	public boolean handleInput(WrappedEnchantment recipe, double mouseX, double mouseY, Key input)
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
			if(recipe.pageIndex < recipe.getIncompats(mc.font).size() / 10	)
			{
				recipe.right.playDownSound(mc.getSoundManager());
				recipe.pageIndex++;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder layout, WrappedEnchantment recipe, IFocusGroup focus)
	{
		List<List<ItemStack>> list = new ObjectArrayList<>();
		for(int i = 0;i<7;i++) list.add(new ObjectArrayList<>());
		for(int i = 0,m=recipe.validItems.size();i<m;i++)
		{
			list.get(1 + (i % 6)).add(recipe.validItems.get(i));
		}
		for(int i = recipe.ench.getMinLevel(),m=recipe.ench.getMaxLevel();i<=m;i++)
		{
			ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
			EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(recipe.ench, i));
			list.get(0).add(stack);
		}
		layout.addSlot(RecipeIngredientRole.INPUT, 90, 18).addItemStacks(list.get(0));
		for(int i = 0;i<6;i++)
		{
			layout.addSlot(RecipeIngredientRole.INPUT, 113 + ((i % 3) * 18), 20 + ((i / 3) * 18)).addItemStacks(list.get(1+i));
		}
	}
}
