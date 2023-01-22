package uniqueapex.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import uniqueapex.UEApex;
import uniqueapex.handler.recipe.fusion.FusionRecipe;

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