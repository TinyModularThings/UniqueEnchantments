package uniqueapex.handler.recipe.upgrade;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import uniqueapex.UEApex;
import uniqueapex.handler.recipe.FusionContext;
import uniquebase.handler.MathCache;

public class FusionUpgradeRecipe implements IRecipe<FusionContext>
{
	protected ResourceLocation id;
	protected Object2IntMap<Ingredient> requestedItems;
	protected Enchantment enchantment;
	protected int requiredExperience;
	protected int requiredBooks;
	
	public FusionUpgradeRecipe(ResourceLocation id, Object2IntMap<Ingredient> requestedItems, Enchantment enchantment, int requiredExperience, int requiredBooks)
	{
		this.id = id;
		this.requestedItems = requestedItems;
		this.enchantment = enchantment;
		this.requiredExperience = requiredExperience;
		this.requiredBooks = requiredBooks;
	}
	
	public static FusionUpgradeRecipe load(ResourceLocation location, PacketBuffer buffer)
	{
		Object2IntMap<Ingredient> requestedItems = new Object2IntLinkedOpenHashMap<>();
		for(int i = 0,m=buffer.readVarInt();i<m;i++)
		{
			requestedItems.put(Ingredient.fromNetwork(buffer), buffer.readVarInt());
		}
		Enchantment ench = buffer.readRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS);
		int exp = buffer.readVarInt();
		int books = buffer.readVarInt();
		return ench == null ? null : new FusionUpgradeRecipe(location, requestedItems, ench, exp, books);
	}
	
	public void writePacket(PacketBuffer buffer)
	{
		buffer.writeVarInt(requestedItems.size());
		for(Object2IntMap.Entry<Ingredient> entry : Object2IntMaps.fastIterable(requestedItems))
		{
			entry.getKey().toNetwork(buffer);
			buffer.writeVarInt(entry.getIntValue());
		}
		buffer.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, enchantment);
		buffer.writeVarInt(requiredExperience);
		buffer.writeVarInt(requiredBooks);
	}
	
	@Override
	public boolean matches(FusionContext context, World world)
	{
		if(context.getLargestEnchantment() != enchantment) return false;
		if(!context.containsItem(requestedItems, true)) return false;
		return true;
	}
	
	public int getRequiredXP(FusionContext context)
	{
		Enchantment ench = context.getLargestEnchantment();
		int levelToAchieve = context.getAchievedLevel(requiredBooks);
		float lvl = (float)levelToAchieve / (float)ench.getMaxLevel();
		return requiredExperience + (int)(UEApex.UPGRADE_MULTIPLIERS.getFloat(ench) * (lvl > 1 ? levelToAchieve * levelToAchieve : levelToAchieve) * Math.sqrt(lvl * MathCache.POW3.get(levelToAchieve) / (float)context.getEnchantability())*(11 - Math.min(ench.getRarity().getWeight(), 10)));
	}
	
	public void mergeEnchantments(FusionContext context)
	{
		context.mergeEnchantments(requiredBooks);
		context.containsItem(requestedItems, false);
	}
	
	@Override
	public ItemStack assemble(FusionContext context)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_)
	{
		return true;
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

	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		return UEApex.UPGRADE_SERIALIZER;
	}

	@Override
	public IRecipeType<?> getType()
	{
		return UEApex.FUSION_UPGRADE;
	}
}