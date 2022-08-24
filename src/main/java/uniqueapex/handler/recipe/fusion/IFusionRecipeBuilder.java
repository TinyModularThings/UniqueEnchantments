package uniqueapex.handler.recipe.fusion;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;

public interface IFusionRecipeBuilder
{
	public FusionRecipe buildRecipe(ResourceLocation id, Object2IntMap<Ingredient> requestedItems, List<Object2IntMap.Entry<Enchantment>> enchantments, boolean lock, int max, Enchantment output);
}
