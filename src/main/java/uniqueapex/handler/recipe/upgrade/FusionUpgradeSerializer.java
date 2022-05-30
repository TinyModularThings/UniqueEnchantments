package uniqueapex.handler.recipe.upgrade;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import uniquebase.UEBase;

public class FusionUpgradeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FusionUpgradeRecipe>
{
	public FusionUpgradeSerializer init()
	{
		setRegistryName("uniqueapex", "fusion_upgrade");
		return this;
	}
	
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
		return new FusionUpgradeRecipe(id, map, ench, xp, books);
	}

	@Override
	public FusionUpgradeRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer)
	{
		return FusionUpgradeRecipe.load(id, buffer);
	}

	@Override
	public void toNetwork(PacketBuffer buffer, FusionUpgradeRecipe recipe)
	{
		recipe.writePacket(buffer);
	}
}