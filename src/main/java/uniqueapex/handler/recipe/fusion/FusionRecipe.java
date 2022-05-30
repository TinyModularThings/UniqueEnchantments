package uniqueapex.handler.recipe.fusion;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import uniqueapex.UEApex;
import uniqueapex.handler.recipe.FusionContext;

public abstract class FusionRecipe implements IRecipe<FusionContext>
{
	protected ResourceLocation id;
	protected Object2IntMap<Ingredient> requestedItems;
	protected List<Object2IntMap.Entry<Enchantment>> enchantments;
	protected Enchantment output;
	protected boolean lock;
	protected int max;
	
	public FusionRecipe(ResourceLocation id, Object2IntMap<Ingredient> requestedItems, List<Object2IntMap.Entry<Enchantment>> enchantments, boolean lock, int max, Enchantment output)
	{
		this.id = id;
		this.requestedItems = requestedItems;
		this.enchantments = enchantments;
		this.lock = lock;
		this.max = max;
		this.output = output;
	}
	
	@Override
	public boolean matches(FusionContext recipe, World world)
	{
		return recipe.isValid(requestedItems, output, enchantments);
	}
	
	@Override
	public ItemStack assemble(FusionContext recipe)
	{
		return ItemStack.EMPTY;
	}
	
	public void assembleEnchantment(FusionContext recipe)
	{
		if(recipe.containsItem(requestedItems, false))
		{
			recipe.applyEnchantment(output, calculateLevel(recipe.getEnchantmentInputs(new ObjectOpenHashSet<>(Lists.transform(enchantments, Entry::getKey)))), lock, max);
		}
	}
	
	protected abstract int calculateLevel(Object2IntMap<Enchantment> list);
	
	@Override
	public boolean canCraftInDimensions(int x, int y)
	{
		return false;
	}

	@Override
	public ItemStack getResultItem()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId()
	{
		return id;
	}
	
	public Enchantment getOutput()
	{
		return output;
	}
	
	@OnlyIn(Dist.CLIENT)
	public List<Object2IntMap.Entry<Enchantment>> getEnchantments()
	{
		return Collections.unmodifiableList(enchantments);
	}
	
	@OnlyIn(Dist.CLIENT)
	public Object2IntMap<Ingredient> getRequestedItems()
	{
		return Object2IntMaps.unmodifiable(requestedItems);
	}
	
	@Override
	public IRecipeSerializer<FusionRecipe> getSerializer()
	{
		return UEApex.FUSION_SERIALIZER;
	}
	
	@Override
	public IRecipeType<FusionRecipe> getType()
	{
		return UEApex.FUSION;
	}
	
	public void writePacket(PacketBuffer buffer)
	{
		buffer.writeVarInt(requestedItems.size());
		for(Object2IntMap.Entry<Ingredient> entry : Object2IntMaps.fastIterable(requestedItems))
		{
			entry.getKey().toNetwork(buffer);
			buffer.writeVarInt(entry.getIntValue());
		}
		buffer.writeVarInt(enchantments.size());
		for(Object2IntMap.Entry<Enchantment> entry : enchantments)
		{
			buffer.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, entry.getKey());
			buffer.writeVarInt(entry.getIntValue());
		}
		buffer.writeBoolean(lock);
		buffer.writeInt(max);
		buffer.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, output);	
	}
	
	public static FusionRecipe loadRecipe(PacketBuffer buffer, ResourceLocation id, IFusionRecipeBuilder builder)
	{
		Object2IntMap<Ingredient> requestedItems = new Object2IntLinkedOpenHashMap<>();
		List<Object2IntMap.Entry<Enchantment>> enchantments = new ObjectArrayList<>();
		for(int i = 0,m=buffer.readVarInt();i<m;i++)
		{
			requestedItems.put(Ingredient.fromNetwork(buffer), buffer.readVarInt());
		}
		for(int i = 0,m=buffer.readVarInt();i<m;i++)
		{
			enchantments.add(new AbstractObject2IntMap.BasicEntry<>(buffer.readRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS), buffer.readVarInt()));
		}
		boolean lock = buffer.readBoolean();
		int max = buffer.readInt();
		Enchantment output = buffer.readRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS);
		return builder.buildRecipe(id, requestedItems, enchantments, lock, max, output);
	}
}
