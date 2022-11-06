package uniquee.handler;

import java.util.List;

import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquee.UE;
import uniquee.enchantments.unique.IfritsGrace;
import uniquee.enchantments.unique.MidasBlessing;

public class LootModifier implements IGlobalLootModifier
{
	@Override
	public List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context)
	{
		if(context.hasParam(LootParameters.TOOL) && context.hasParam(LootParameters.BLOCK_STATE))
		{
			ItemStack stack = ItemStack.EMPTY;
			if(context.hasParam(LootParameters.THIS_ENTITY))
			{
				Entity entity = context.getParamOrNull(LootParameters.THIS_ENTITY);
				if(entity instanceof LivingEntity) stack = ((LivingEntity)entity).getMainHandItem();
			}
			BlockState state = context.getParamOrNull(LootParameters.BLOCK_STATE);
			Object2IntMap<Enchantment> enchs = MiscUtil.getEnchantments(stack);
			int midas = enchs.getInt(UE.MIDAS_BLESSING);
			if(midas > 0)
			{
				int gold = StackUtils.getInt(stack, MidasBlessing.GOLD_COUNTER, 0);
				if(gold > 0 && MidasBlessing.IS_GEM.test(state))
				{
					gold -= (int)(Math.pow(MidasBlessing.GOLD_COST.getAsDouble(midas), 2)/midas);
					StackUtils.setInt(stack, MidasBlessing.GOLD_COUNTER, Math.max(0, gold));
					int multiplier = 1 + midas;
					List<ItemStack> newDrops = new ObjectArrayList<ItemStack>();
					for(ItemStack drop : generatedLoot)
					{
						StackUtils.growStack(drop, drop.getCount() * multiplier, newDrops);
					}
					generatedLoot.clear();
					generatedLoot.addAll(newDrops);
				}
			}
			int level = enchs.getInt(UE.IFRIDS_GRACE);
			if(level > 0)
			{
				int stored = StackUtils.getInt(stack, IfritsGrace.LAVA_COUNT, 0);
				if(stored > 0)
				{
					double extra = (Math.pow(midas, 2D)/level)+1F;
					boolean ore = StackUtils.isOre(state);
					int smelted = 0;
					List<ItemStack> stacks = generatedLoot;
					for(int i = 0,m=stacks.size();i<m;i++)
					{
						ItemStack toBurn = stacks.get(i).copy();
						toBurn.setCount(1);
						IInventory inventory = new Inventory(toBurn);
						ItemStack result = ItemStack.EMPTY;
						for(IRecipe<IInventory> recipe : ServerLifecycleHooks.getCurrentServer().getRecipeManager().getAllRecipesFor(IRecipeType.SMELTING))
						{
							if(recipe.matches(inventory, null)) result = recipe.assemble(inventory);
						}
						if(result.isEmpty()) continue;
						result.setCount(result.getCount() * stacks.get(i).getCount());
						stacks.set(i, result);
						smelted++;
					}
					if(smelted > 0)
					{
						stored -= MathHelper.ceil((smelted * (ore ? 5 : 1) * IfritsGrace.BASE_CONSUMTION.get() * extra) / level);
						StackUtils.setInt(stack, IfritsGrace.LAVA_COUNT, Math.max(0, stored));
					}
				}
			}
		}
		if(LootTables.PIGLIN_BARTERING.equals(context.getQueriedLootTableId()) && context.getRandom().nextInt(200) < 1)
		{
			generatedLoot.add(EnchantedBookItem.createForEnchantment(new EnchantmentData(UE.MIDAS_BLESSING, MathHelper.nextInt(context.getRandom(), 2, 3))));
		}
		return generatedLoot;
	}
	
	public static class Serializer extends GlobalLootModifierSerializer<LootModifier>
	{
		public static final Serializer INSTANCE = new Serializer();
		
		public Serializer()
		{
			setRegistryName("ue_loot");
		}
		
		@Override
		public LootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition)
		{
			return new LootModifier();
		}

		@Override
		public JsonObject write(LootModifier instance)
		{
			return new JsonObject();
		}
	}
}
