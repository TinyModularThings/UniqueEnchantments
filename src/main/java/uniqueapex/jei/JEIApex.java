package uniqueapex.jei;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.common.plugins.vanilla.anvil.AnvilRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import uniqueapex.UEApex;
import uniqueapex.handler.recipe.fusion.FusionRecipe;
import uniquebase.utils.StackUtils;

@JeiPlugin
public class JEIApex implements IModPlugin
{
	public static final RecipeType<FusionRecipe> REGISTRY = RecipeType.create("unique_apex", "fusion", FusionRecipe.class);
	
	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation("uniqueapex", "core");
	}
		
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		registration.addRecipes(REGISTRY, Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(UEApex.FUSION));
		List<IJeiAnvilRecipe> recipes = new ObjectArrayList<>();		
		ItemStack stack = new ItemStack(Items.NETHER_STAR);
		StackUtils.addTooltip(stack, Component.translatable("unique.apex.removal"));
		recipes.add(new AnvilRecipe(ObjectLists.singleton(new ItemStack(Items.NETHER_STAR)), ObjectLists.singleton(new ItemStack(Items.RABBIT_FOOT)), ObjectLists.singleton(stack)));
		registration.addRecipes(RecipeTypes.ANVIL, recipes);
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		registry.addRecipeCategories(new ApexCategory(registry.getJeiHelpers().getGuiHelper(), REGISTRY));
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
	{
		registration.addRecipeCatalyst(new ItemStack(Items.BEACON), REGISTRY);
	}
}