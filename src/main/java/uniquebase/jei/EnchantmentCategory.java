package uniquebase.jei;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants.Key;
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
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStack.TooltipPart;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uniquebase.utils.StackUtils;

public class EnchantmentCategory implements IRecipeCategory<WrappedEnchantment>
{
	RecipeType<WrappedEnchantment> id;

	IDrawable drawable;
	IDrawable icon;
	
	public EnchantmentCategory(IGuiHelper helper, RecipeType<WrappedEnchantment> id)
	{
		this.id = id;
		drawable = helper.createDrawable(new ResourceLocation("uniquebase:textures/jei_all_slots_1.png"), 4, 5, 167, 120);
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
		font.draw(matrix, Component.translatable("unique.base.jei.incompatible"), 13, 40, 4210752);
		font.draw(matrix, Component.translatable("unique.base.jei.applicatble"), 101, 40, 4210752);
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
	
	private void addExtraData(ItemStack stack, WrappedEnchantment recipe)
	{
		stack.addTagElement("HideFlags", IntTag.valueOf(TooltipPart.ENCHANTMENTS.getMask()));
		List<Component> components = new ObjectArrayList<>();
		components.add(Component.translatable("unique.base.jei.max_level", Component.literal(Integer.toString(recipe.ench.getMaxLevel())).withStyle(ChatFormatting.WHITE)).withStyle(createColor(0xFFAA00)));
		components.add(Component.translatable("unique.base.jei.treasure", Component.translatable(recipe.ench.isTreasureOnly() ? "unique.base.jei.yes" : "unique.base.jei.no").withStyle(ChatFormatting.WHITE)).withStyle(createColor(0xBA910D)));
		components.add(Component.translatable("unique.base.jei.curse", Component.translatable(recipe.ench.isCurse() ? "unique.base.jei.yes" : "unique.base.jei.no").withStyle(ChatFormatting.WHITE)).withStyle(createColor(0xA11E15)));
		components.add(Component.translatable("unique.base.jei.tradeable", Component.translatable(recipe.ench.isTradeable() ? "unique.base.jei.yes" : "unique.base.jei.no").withStyle(ChatFormatting.WHITE)).withStyle(createColor(0xC98414)));
		components.add(Component.translatable("unique.base.jei.rarity", Component.translatable("unique.base.jei."+recipe.ench.getRarity().name().toLowerCase()).withStyle(recipe.getFormatting(recipe.ench.getRarity()))).withStyle(createColor(0xCB19D1)));
		components.add(Component.translatable("unique.base.jei.discover", Component.translatable(recipe.ench.isDiscoverable() ? "unique.base.jei.yes" : "unique.base.jei.no").withStyle(ChatFormatting.WHITE)).withStyle(createColor(0xBA910D)));
		components.add(Component.translatable("unique.base.jei.books", Component.translatable(recipe.ench.isAllowedOnBooks() ? "unique.base.jei.yes" : "unique.base.jei.no").withStyle(ChatFormatting.WHITE)).withStyle(createColor(0xBA910D)));
		StackUtils.addTooltips(stack, components);
	}
	
	private Style createColor(int color) {
		return Style.EMPTY.withColor(color).withItalic(false);
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder layout, WrappedEnchantment recipe, IFocusGroup focus)
	{
		List<List<ItemStack>> list = new ObjectArrayList<>();
		for(int i = 0;i<25;i++) list.add(new ObjectArrayList<>());
		for(int i = 0,m=recipe.validItems.size();i<m;i++)
		{
			list.get(1 + (i % 12)).add(recipe.validItems.get(i));
		}
		for(int i = 0,m=recipe.incompats.size();i<m;i++) {
			list.get(13 + (i % 12)).add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(recipe.incompats.get(i), 1)));

		}
		for(int i = recipe.ench.getMinLevel(),m=recipe.ench.getMaxLevel();i<=m;i++)
		{
			ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
			EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(recipe.ench, i));		
			addExtraData(stack, recipe);
			list.get(0).add(stack);
		}
		layout.addSlot(RecipeIngredientRole.INPUT, 76, 17).addItemStacks(list.get(0));
		for(int i = 0;i<12;i++)
		{
			layout.addSlot(RecipeIngredientRole.INPUT, 102 + ((i % 3) * 18), 49 + ((i / 3) * 18)).addItemStacks(list.get(1+i));
		}
		for(int i = 0;i<12;i++)
		{
			layout.addSlot(RecipeIngredientRole.INPUT, 14 + ((i % 3) * 18), 49 + ((i / 3) * 18)).addItemStacks(list.get(13+i));
		}
	}
}
