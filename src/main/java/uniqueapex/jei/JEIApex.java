package uniqueapex.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import uniqueapex.UEApex;

@JeiPlugin
public class JEIApex implements IModPlugin
{
	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation("uniqueapex", "core");
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		registration.addRecipes(Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(UEApex.FUSION), new ResourceLocation("unique_apex", "fusion"));
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		registry.addRecipeCategories(new ApexCategory(registry.getJeiHelpers().getGuiHelper()));
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
	{
		registration.addRecipeCatalyst(new ItemStack(Items.BEACON), new ResourceLocation("unique_apex", "fusion"));
	}
}