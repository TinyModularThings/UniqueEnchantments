package uniqueapex.jei;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import uniqueapex.handler.recipe.fusion.FusionRecipe;

public class ApexCategory implements IRecipeCategory<FusionRecipe>
{
	RecipeType<FusionRecipe> id;
	IDrawable drawable;
	IDrawable icon;
	
	public ApexCategory(IGuiHelper helper, RecipeType<FusionRecipe> id)
	{
		this.id = id;
		drawable = helper.createDrawable(new ResourceLocation("uniqueapex:textures/jei.png"), 4, 5, 167, 100);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.BEACON));
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
	public RecipeType<FusionRecipe> getRecipeType()
	{
		return id;
	}

	@Override
	public Component getTitle()
	{
		return Component.translatable("unique.apex.jei.name");
	}
	
	@Override
	public void draw(FusionRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrix, double mouseX, double mouseY)
	{
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		font.draw(matrix, I18n.get("unique.apex.catalyst"), 2, 0, 0);
		font.draw(matrix, I18n.get("unique.apex.bases"), 104, 0, 0);
		List<FormattedCharSequence> lines = font.split(Component.translatable("unique.apex.desc"), 170);
		for(int i = 0;i<lines.size();i++)
		{
			font.draw(matrix, lines.get(i), 0, 65+(i*font.lineHeight), 0);
		}
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder layout, FusionRecipe recipe, IFocusGroup focus)
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
			items.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, entry.getIntValue())));
			for(int i = entry.getIntValue()+1;i<ench.getMaxLevel();i++) {
				items.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, i)));
			}
		}
		for(int i = 0;i<9;i++)
		{
			layout.addSlot(RecipeIngredientRole.INPUT, 3 + ((i % 3) * 18), 10 + ((i / 3) * 18)).addItemStacks(list.get(i));
		}
		for(int i = 0;i<9;i++)
		{
			layout.addSlot(RecipeIngredientRole.INPUT, 105 + ((i % 3) * 18), 10 + ((i / 3) * 18)).addItemStacks(list.get(i+9));
		}
		Enchantment ench = recipe.getOutput();
		List<ItemStack> outputs = new ObjectArrayList<>();
		for(int i = Math.max(1, ench.getMinLevel()),m=Math.max(2, ench.getMaxLevel());i<=m;i++)
		{
			outputs.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, i)));
		}
		layout.addSlot(RecipeIngredientRole.OUTPUT, 72, 28).addItemStacks(outputs);
	}
	
	private ItemStack copyWithSize(ItemStack stack, int size)
	{
		ItemStack newStack = stack.copy();
		newStack.setCount(size);
		return newStack;
	}
}