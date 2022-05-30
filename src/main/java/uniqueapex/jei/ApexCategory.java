package uniqueapex.jei;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import uniqueapex.handler.recipe.fusion.FusionRecipe;

public class ApexCategory implements IRecipeCategory<FusionRecipe>
{
	IDrawable drawable;
	IDrawable icon;
	
	public ApexCategory(IGuiHelper helper)
	{
		drawable = helper.createDrawable(new ResourceLocation("uniqueapex:textures/jei.png"), 4, 5, 167, 100);
		icon = helper.createDrawableIngredient(new ItemStack(Items.BEACON));
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
	public Class<? extends FusionRecipe> getRecipeClass()
	{
		return FusionRecipe.class;
	}

	@Override
	public String getTitle()
	{
		return I18n.get("unique.apex.jei.name");
	}

	@Override
	public ResourceLocation getUid()
	{
		return new ResourceLocation("unique_apex", "fusion");
	}
	
	@Override
	public void draw(FusionRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY)
	{
		Minecraft mc = Minecraft.getInstance();
		FontRenderer font = mc.font;
		font.draw(matrixStack, I18n.get("unique.apex.catalyst"), 2, 0, 0);
		font.draw(matrixStack, I18n.get("unique.apex.bases"), 104, 0, 0);
		List<IReorderingProcessor> lines = font.split(new TranslationTextComponent("unique.apex.desc"), 170);
		for(int i = 0;i<lines.size();i++)
		{
			font.draw(matrixStack, lines.get(i), 0, 65+(i*font.lineHeight), 0);
		}
	}
	
	@Override
	public void setIngredients(FusionRecipe recipe, IIngredients ingredients)
	{
		List<List<ItemStack>> list = new ObjectArrayList<>();
		for(int i = 0;i<18;i++) list.add(new ObjectArrayList<>());
		int index = 0;
		for(Object2IntMap.Entry<Ingredient> entry : Object2IntMaps.fastIterable(recipe.getRequestedItems()))
		{
			if(index >= 9) break;
			List<ItemStack> items = list.get(index++);
			int size = entry.getIntValue();
			for(ItemStack stack : entry.getKey().getItems())
			{
				items.add(copyWithSize(stack, size));
			}
		}
		index = 9;
		for(Object2IntMap.Entry<Enchantment> entry : recipe.getEnchantments())
		{
			if(index >= 18) break;
			List<ItemStack> items = list.get(index++);
			Enchantment ench = entry.getKey();
			items.add(EnchantedBookItem.createForEnchantment(new EnchantmentData(ench, entry.getIntValue())));
			for(int i = entry.getIntValue()+1;i<ench.getMaxLevel();i++) {
				items.add(EnchantedBookItem.createForEnchantment(new EnchantmentData(ench, i)));
			}
		}
		ingredients.setInputLists(VanillaTypes.ITEM, list);
		ingredients.setOutput(VanillaTypes.ITEM, EnchantedBookItem.createForEnchantment(new EnchantmentData(recipe.getOutput(), 1)));
	}
	
	private ItemStack copyWithSize(ItemStack stack, int size)
	{
		ItemStack newStack = stack.copy();
		newStack.setCount(size);
		return newStack;
	}
	
	@Override
	public void setRecipe(IRecipeLayout layout, FusionRecipe recipe, IIngredients ingredients)
	{
		IGuiItemStackGroup guiItemStacks = layout.getItemStacks();
		for(int i = 0;i<9;i++)
		{
			guiItemStacks.init(i, true, 2 + ((i % 3) * 18), 9 + ((i / 3) * 18));
		}
		for(int i = 0;i<9;i++)
		{
			guiItemStacks.init(9+i, true, 104 + ((i % 3) * 18), 9 + ((i / 3) * 18));
		}
		guiItemStacks.init(18, false, 71, 27);
		guiItemStacks.set(ingredients);
	}
}