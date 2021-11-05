package uniquebase.jei;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

@JeiPlugin
public class JEIView implements IModPlugin
{
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		List<WrappedEnchantment> enchantments = new ObjectArrayList<>();
		for(Enchantment ench : ForgeRegistries.ENCHANTMENTS) enchantments.add(new WrappedEnchantment(ench));
		enchantments.sort(null);
		registration.addRecipes(enchantments, new ResourceLocation("uniquebase", "ue_enchantments"));
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		registry.addRecipeCategories(new EnchantmentCategory(registry.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation("uniquebase", "core");
	}
}