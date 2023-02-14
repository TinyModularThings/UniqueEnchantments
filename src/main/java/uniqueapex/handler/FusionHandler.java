package uniqueapex.handler;

import java.util.List;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import uniqueapex.UEApex;
import uniqueapex.enchantments.ApexEnchantment;
import uniqueapex.handler.recipe.FusionContext;
import uniqueapex.handler.recipe.fusion.FusionRecipe;
import uniqueapex.handler.recipe.upgrade.DefaultFusionUpgradeRecipe;
import uniqueapex.handler.recipe.upgrade.FusionUpgradeRecipe;
import uniqueapex.handler.structure.ClientRecipeStorage;
import uniqueapex.handler.structure.RecipeAnimator;
import uniqueapex.handler.structure.RecipeStorage;
import uniqueapex.handler.structure.TrackedRecipe;
import uniqueapex.handler.structure.TrackedUpgradeRecipe;
import uniqueapex.network.SyncRecipePacket;
import uniquebase.UEBase;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.mixin.common.tile.BeaconMixin;

public class FusionHandler
{
	public static final FusionHandler INSTANCE = new FusionHandler();
	Long2ObjectMap<List<RecipeAnimator>> animations = new Long2ObjectLinkedOpenHashMap<>();
	
	@SubscribeEvent
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getFace() == Direction.DOWN) return;
		Item item = event.getItemStack().getItem();
		if(!UEApex.CRAFTING.contains(item) && !UEApex.UPGRADING.contains(item)) return;
		Level world = event.getLevel();
		BlockPos pos = event.getPos();
		if(getHandler(world.getBlockEntity(pos), Direction.UP) == null) return;
		int beaconSize = isValidBeacon(world, pos.below());
		if(beaconSize == 0) return;
		FusionContext context = context(world, pos.below());
		if(context == null) return;
		if(!(world instanceof ServerLevel)) {
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
			return;
		}
		if(UEApex.UPGRADING.contains(item))
		{
			FusionUpgradeRecipe recipe = getUpgradeRecipe(world, context);
			if(recipe == null) return;
			int xp = recipe.getRequiredXP(context);
			if(MiscUtil.getXP(event.getEntity()) < xp && !event.getEntity().isCreative()) {
				event.getEntity().displayClientMessage(Component.literal("["+MiscUtil.getLvlForXP(xp)+"] levels Required").withStyle(ChatFormatting.RED), true);
				return;
			}
			MiscUtil.drainExperience(event.getEntity(), xp);
			RecipeStorage storage = RecipeStorage.get((ServerLevel)world);
			if(storage.isInUse(pos)) return;
			storage.addRecipe(new TrackedUpgradeRecipe(world, pos, recipe, context, beaconSize, getCrystals(world, pos, beaconSize)));
			if(!event.getEntity().isCreative())
			{
				event.getItemStack().shrink(1);
			}
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
		else if(UEApex.CRAFTING.contains(item))
		{
			FusionRecipe recipe = getFusionRecipe(world, context);
			if(recipe == null) return;
			RecipeStorage storage = RecipeStorage.get((ServerLevel)world);
			if(storage.isInUse(pos)) return;
			storage.addRecipe(new TrackedRecipe(world, pos, recipe, context, beaconSize, getCrystals(world, pos, beaconSize)));
			if(!event.getEntity().isCreative())
			{
				event.getItemStack().shrink(1);
			}
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onAnvilEvent(AnvilUpdateEvent event)
	{	
		if(event.getLeft().hasTag() && event.getLeft().getTag().getByte("fusioned") == 2 && event.getRight().getItem() == Items.RABBIT_FOOT) {
			int level = Math.min(countApexLevels(event.getLeft().getEnchantmentTags()), event.getRight().getMaxStackSize());
			if(event.getRight().getCount() < level) return;
			ItemStack copy = event.getLeft().copy();
			copy.getTag().put("fusioned", ByteTag.ONE);
			event.setOutput(copy);
			event.setCost(level * 4);
			event.setMaterialCost(level);
		}
	}
	
	private int countApexLevels(ListTag list)
	{
		int result = 0;
		for(int i = 0,m=list.size();i<m;i++)
		{
			Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(list.getCompound(i).getString("id")));
			if(ench instanceof ApexEnchantment)
			{
				result += list.getCompound(i).getInt("lvl");
			}
		}
		return result;
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START) return;
		Player player = event.player;
		if(player.level.getGameTime() % 200 == 0)
		{
			for(EquipmentSlot slot : EquipmentSlot.values())
			{
				ItemStack stack = player.getItemBySlot(slot);
				if(!stack.hasTag()) continue;
				CompoundTag data = stack.getTag();
				if(!data.getBoolean("fusioned")) {
					clearApex(stack);
					continue;
				}
			}
		}
	}
	
	private void clearApex(ItemStack stack)
	{
		ListTag list = stack.getEnchantmentTags();
		for(int i = 0,m=list.size();i<m;i++)
		{
			Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(list.getCompound(i).getString("id")));
			if(ench instanceof ApexEnchantment)
			{
				list.remove(i--);
				m--;
			}
		}
	}
	
	@SubscribeEvent
	public void onServerTick(LevelTickEvent event)
	{
		if(event.phase == Phase.START || !(event.level instanceof ServerLevel server)) return;
		RecipeStorage.get(server).onTick();
	}
	
	@SubscribeEvent
	public void onChunkWatch(ChunkWatchEvent.Watch event)
	{
		SyncRecipePacket packet = RecipeStorage.get(event.getLevel()).getSyncPacket(event.getPos());
		if(packet != null) UEBase.NETWORKING.sendToAllChunkWatchers(event.getLevel().getChunk(event.getPos().x, event.getPos().z), packet);
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onClientTick(ClientTickEvent event)
	{
		if(event.phase == Phase.START) return;
		ClientRecipeStorage.INSTANCE.onTick(Minecraft.getInstance().level);
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void renderLastEvent(RenderLevelStageEvent event)
	{
		if(event.getStage() != Stage.AFTER_WEATHER) return;
		ClientRecipeStorage.INSTANCE.render(event.getPartialTick());
	}
	
	public FusionUpgradeRecipe getUpgradeRecipe(Level world, FusionContext context)
	{
		Enchantment ench = context.getLargestEnchantment();
		return world.getRecipeManager().getRecipeFor(UEApex.FUSION_UPGRADE, context, world).orElse(ench == null ? null : new DefaultFusionUpgradeRecipe(ench));
	}
	
	public FusionRecipe getFusionRecipe(Level world, FusionContext context)
	{
		return world.getRecipeManager().getRecipeFor(UEApex.FUSION, context, world).orElse(null);
	}
	
	public FusionContext context(Level world, BlockPos pos)
	{
		IItemHandler mainChest = getHandler(world.getBlockEntity(pos.relative(Direction.UP)), Direction.UP);
		if(mainChest == null) return null;
		IItemHandler[] originals = getInventories(world, pos);
		return originals == null ? null : new FusionContext(mainChest, originals);
	}
	
	public List<EndCrystal> getCrystals(Level world, BlockPos pos, int size)
	{
		List<EndCrystal> crystals = new ObjectArrayList<>();
		EndCrystal entity = new EndCrystal(world, pos.getX() + 0.5D, pos.getY()+3.5D, pos.getZ() + 0.5D);
		entity.setShowBottom(false);
        world.addFreshEntity(entity);
        crystals.add(entity);
        for(int i = 0;i<4;i++)
        {
        	Direction dir = Direction.from2DDataValue(i);
    		entity = new EndCrystal(world, pos.getX() + 0.5D + dir.getStepX() * size, pos.getY()-(size-1), pos.getZ() + 0.5D + dir.getStepZ() * size);
    		entity.setShowBottom(false);
    		entity.setBeamTarget(pos.above(2));
            world.addFreshEntity(entity);
            crystals.add(entity);
        }
		return crystals;
	}
	
	public IItemHandler[] getInventories(Level world, BlockPos pos)
	{
		IItemHandler[] directions = new IItemHandler[4];
		for(int i = 0;i<4;i++)
		{
			IItemHandler handler = getHandler(world.getBlockEntity(pos.relative(Direction.from2DDataValue(i))), Direction.from2DDataValue(i));
			if(handler == null) return null;
			directions[i] = handler;
		}
		return directions;
	}
	
	private IItemHandler getHandler(BlockEntity tile, Direction dir)
	{
		return tile == null ? null : tile.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite()).orElse(null);
	}
	
	public int isValidBeacon(Level world, BlockPos pos)
	{
		BlockEntity tile = world.getBlockEntity(pos);
		if(tile instanceof BeaconMixin beacon)
		{
			return beacon.getBeaconLevel() >= 3 && ((BeaconMixin)beacon).getBeaconSegments().size() > 0 ? beacon.getBeaconLevel() : 0;
		}
		return 0;
	}
}
