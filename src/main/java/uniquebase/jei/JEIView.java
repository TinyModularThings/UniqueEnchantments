package uniquebase.jei;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import uniquebase.utils.MiscUtil;

@JEIPlugin
public class JEIView implements IModPlugin
{
	@Override
	public void register(IModRegistry registry)
	{
		registry.handleRecipes(WrappedEnchantment.class, EnchantmentWrapper::new, "ue_enchantments");
		List<WrappedEnchantment> enchantments = new ObjectArrayList<>();
		for(Enchantment ench : ForgeRegistries.ENCHANTMENTS)
		{
			if(MiscUtil.isDisabled(ench)) continue;
			enchantments.add(new WrappedEnchantment(ench));
		}
		enchantments.sort(null);
		registry.addRecipes(enchantments, "ue_enchantments");
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		registry.addRecipeCategories(new EnchantmentCategory(registry.getJeiHelpers().getGuiHelper()));
	}
}