package uniquebase.jei;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;

public class FilterCategory implements IRecipeCategory<FilterEntry>
{
	RecipeType<FilterEntry> id;

	IDrawable drawable;
	IDrawable icon;
	
	public FilterCategory(IGuiHelper helper, RecipeType<FilterEntry> id)
	{
		this.id = id;
		drawable = helper.createDrawable(new ResourceLocation("uniquebase:textures/inventory.png"), 4, 4, 169, 122);
		ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
		EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(Enchantments.BLOCK_FORTUNE, 100));
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
	}
	@Override
	public IDrawable getBackground()
	{
		return drawable;
	}

	@Override
	public IDrawable getIcon()
	{
		return icon;
	}

	@Override
	public RecipeType<FilterEntry> getRecipeType()
	{
		return id;
	}

	@Override
	public Component getTitle()
	{
		return Component.literal("Enchantment Filter Info");
	}
	
	@Override
	public void draw(FilterEntry recipe, IRecipeSlotsView view, PoseStack stack, double mouseX, double mouseY)
	{
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		List<FormattedCharSequence> split = font.split(recipe.getDescription(), 165);
		for(int i = 0;i<Math.min(2, split.size());i++) {
			font.draw(stack, split.get(i), 4F, 25F+(i*font.lineHeight), 4210752);
		}
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, FilterEntry entry, IFocusGroup group)
	{
		builder.addSlot(RecipeIngredientRole.INPUT, 76, 3).addItemStacks(entry.getDisplay());
		List<ItemStack>[] item = entry.getItems();
		for(int i = 0;i<item.length;i++) {
			int x = i%9;
			int y = i/9;
			builder.addSlot(RecipeIngredientRole.CATALYST, 4+(x*18), 49+(y*18)).addItemStacks(item[i]);
		}
	}
	
}
