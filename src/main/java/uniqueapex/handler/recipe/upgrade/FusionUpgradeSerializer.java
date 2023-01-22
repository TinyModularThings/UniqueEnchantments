package uniqueapex.handler.recipe.upgrade;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.UEBase;

public class FusionUpgradeSerializer implements RecipeSerializer<FusionUpgradeRecipe>
{
	@Override
	public FusionUpgradeRecipe fromJson(ResourceLocation id, JsonObject object)
	{
		Object2IntMap<Ingredient> map = new Object2IntLinkedOpenHashMap<>();
		JsonArray array = object.getAsJsonArray("ingredients");
		for(int i = 0;i<array.size();i++)
		{
			JsonObject obj = array.get(i).getAsJsonObject();
			Ingredient input = Ingredient.fromJson(obj.get("input"));
			if(input == null) {
				UEBase.LOGGER.info("Expected Ingriedient got nothing: "+obj.get("input"));
				return null;
			}
			map.put(input, obj.has("amount") ? Math.max(1, obj.get("amount").getAsInt()) : 1);
		}
		Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(object.get("target").getAsString()));
		if(ench == null)
		{
			UEBase.LOGGER.info("Expected Target Enchantment got nothing: "+object.get("target"));
			return null;
		}
		int xp = object.has("xp") ? object.get("xp").getAsInt() : 0;
		int books = object.has("books") ? object.get("books").getAsInt() : 4;
		int lvl = object.has("maxLevel") ? object.get("maxLevel").getAsInt() : 10;
		return new FusionUpgradeRecipe(id, map, ench, xp, books, lvl);
	}

	@Override
	public FusionUpgradeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
	{
		return FusionUpgradeRecipe.load(id, buffer);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FusionUpgradeRecipe recipe)
	{
		recipe.writePacket(buffer);
	}
}