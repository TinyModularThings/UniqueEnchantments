package uniquee.handler;

import java.util.List;

import com.mojang.serialization.Codec;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.server.ServerLifecycleHooks;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquee.UE;
import uniquee.enchantments.unique.IfritsGrace;
import uniquee.enchantments.unique.MidasBlessing;

public class LootModifier implements IGlobalLootModifier
{
	public static final Codec<LootModifier> CODEC = Codec.unit(LootModifier::new);
	
	@Override
	public Codec<? extends IGlobalLootModifier> codec()
	{
		return CODEC;
	}
	
	@Override
	public ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
	{
		if(context.hasParam(LootContextParams.TOOL) && context.hasParam(LootContextParams.BLOCK_STATE))
		{
			ItemStack stack = ItemStack.EMPTY;
			if(context.hasParam(LootContextParams.THIS_ENTITY))
			{
				Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
				if(entity instanceof LivingEntity) stack = ((LivingEntity)entity).getMainHandItem();
			}
			BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
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
						Container inventory = new SimpleContainer(toBurn);
						ItemStack result = ItemStack.EMPTY;
						for(Recipe<Container> recipe : ServerLifecycleHooks.getCurrentServer().getRecipeManager().getAllRecipesFor(RecipeType.SMELTING))
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
						stored -= Mth.ceil((smelted * (ore ? 5 : 1) * IfritsGrace.BASE_CONSUMTION.get() * extra) / level);
						StackUtils.setInt(stack, IfritsGrace.LAVA_COUNT, Math.max(0, stored));
					}
				}
			}
		}
		if(BuiltInLootTables.PIGLIN_BARTERING.equals(context.getQueriedLootTableId()) && context.getRandom().nextInt(200) < 1)
		{
			generatedLoot.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(UE.MIDAS_BLESSING, Mth.nextInt(context.getRandom(), 2, 3))));
		}
		return generatedLoot;
	}
}
