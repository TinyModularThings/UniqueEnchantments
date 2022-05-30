package uniqueapex.handler.recipe.fusion;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import uniqueapex.handler.recipe.fusion.FusionRecipes.AverageFusionRecipe;
import uniqueapex.handler.recipe.fusion.FusionRecipes.MaxFusionRecipe;
import uniqueapex.handler.recipe.fusion.FusionRecipes.MinFusionRecipe;
import uniqueapex.handler.recipe.fusion.FusionRecipes.MulFusionRecipe;
import uniqueapex.handler.recipe.fusion.FusionRecipes.SumFusionRecipe;
import uniquebase.UEBase;

public class FusionRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FusionRecipe>
{
	public FusionRecipeSerializer init()
	{
		setRegistryName("uniqueapex", "fusion");
		return this;
	}
	
	@Override
	public FusionRecipe fromJson(ResourceLocation id, JsonObject object)
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
		List<Object2IntMap.Entry<Enchantment>> requestedBooks = new ObjectArrayList<>();
		array = object.getAsJsonArray("enchantments");
		for(int i = 0;i<array.size();i++)
		{
			JsonObject obj = array.get(i).getAsJsonObject();
			Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(obj.get("type").getAsString()));
			if(ench == null)
			{
				UEBase.LOGGER.info("Expected Input Enchantment got nothing: "+obj.get("type"));			
				return null;
			}
			requestedBooks.add(new AbstractObject2IntMap.BasicEntry<>(ench, obj.has("level") ? Math.max(obj.get("level").getAsInt(), 0) : 1));
		}
		Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(object.get("output").getAsString()));
		if(ench == null)
		{
			UEBase.LOGGER.info("Expected Output Enchantment got nothing: "+object.get("output"));			
			return null;
		}
		boolean lock = object.has("lock") ? object.get("lock").getAsBoolean() : false;
		String mode = object.get("output_mode").getAsString();
		int max = object.has("max_enchantments") ? object.get("max_enchantments").getAsInt() : Integer.MAX_VALUE;
		if(mode.equalsIgnoreCase("min")) return new MinFusionRecipe(id, map, requestedBooks, lock, max, ench);
		else if(mode.equalsIgnoreCase("average")) return new AverageFusionRecipe(id, map, requestedBooks, lock, max, ench);
		else if(mode.equalsIgnoreCase("max")) return new MaxFusionRecipe(id, map, requestedBooks, lock, max, ench);
		else if(mode.equalsIgnoreCase("sum")) return new SumFusionRecipe(id, map, requestedBooks, lock, max, ench);
		else if(mode.equalsIgnoreCase("mul")) return new MulFusionRecipe(id, map, requestedBooks, lock, max, ench);
		return null;
	}

	@Override
	public FusionRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer)
	{
		switch(buffer.readVarInt())
		{
			case 0: return FusionRecipe.loadRecipe(buffer, id, AverageFusionRecipe::new);
			case 1: return FusionRecipe.loadRecipe(buffer, id, MinFusionRecipe::new);
			case 2: return FusionRecipe.loadRecipe(buffer, id, MaxFusionRecipe::new);
			case 3: return FusionRecipe.loadRecipe(buffer, id, SumFusionRecipe::new);
			case 4: return FusionRecipe.loadRecipe(buffer, id, MulFusionRecipe::new);
			default: return null;
		}
	}

	@Override
	public void toNetwork(PacketBuffer buffer, FusionRecipe recipe)
	{
		recipe.writePacket(buffer);
	}
}