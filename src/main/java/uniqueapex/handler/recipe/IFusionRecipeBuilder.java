package uniqueapex.handler.recipe;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface IFusionRecipeBuilder
{
	public FusionRecipe buildRecipe(ResourceLocation id, Object2IntMap<Ingredient> requestedItems, List<Object2IntMap.Entry<Enchantment>> enchantments, boolean lock, int max, Enchantment output);
}
