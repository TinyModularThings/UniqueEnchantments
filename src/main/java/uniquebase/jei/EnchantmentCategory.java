package uniquebase.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentCategory implements IRecipeCategory<EnchantmentWrapper>
{
	IDrawable drawable;
	IDrawable icon;
	
	public EnchantmentCategory(IGuiHelper helper)
	{
		drawable = helper.createDrawable(new ResourceLocation("uniquebase:textures/jei.png"), 4, 5, 167, 128);
		ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
		ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(Enchantments.FORTUNE, 100));
		icon = helper.createDrawableIngredient(stack);
	}
	
	@Override
	public String getUid()
	{
		return "ue_enchantments";
	}

	@Override
	public String getTitle()
	{
		return I18n.format("unique.base.jei.name");
	}

	@Override
	public String getModName()
	{
		return "Unique Enchantments";
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
	public void setRecipe(IRecipeLayout layout, EnchantmentWrapper wrapper, IIngredients ingredients)
	{
		IGuiItemStackGroup guiItemStacks = layout.getItemStacks();
		guiItemStacks.init(0, true, 89, 17);
		for(int i = 0;i<6;i++)
		{
			guiItemStacks.init(1+i, true, 112 + ((i % 3) * 18), 19 + ((i / 3) * 18));
		}
		guiItemStacks.set(ingredients);
	}
}
