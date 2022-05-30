package uniqueapex.handler;

import java.util.List;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
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
		if(item != Items.END_CRYSTAL && item != Items.TOTEM_OF_UNDYING) return;
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		if(world.getBlockState(pos).getBlock() != Blocks.CHEST) return;
		int beaconSize = isValidBeacon(world, pos.below());
		if(beaconSize == 0) return;
		FusionContext context = context(world, pos.below());
		if(context == null) return;
		if(!(world instanceof ServerWorld)) {
			event.setCancellationResult(ActionResultType.SUCCESS);
			event.setCanceled(true);
			return;
		}
		if(item == Items.TOTEM_OF_UNDYING)
		{
			FusionUpgradeRecipe recipe = getUpgradeRecipe(world, context);
			if(recipe == null) return;
			int xp = recipe.getRequiredXP(context);
			if(MiscUtil.getXP(event.getPlayer()) < xp) {
				event.getPlayer().displayClientMessage(new StringTextComponent("["+MiscUtil.getLvlForXP(xp)+"] levels Required").withStyle(TextFormatting.RED), true);
				return;
			}
			MiscUtil.drainExperience(event.getPlayer(), xp);
			RecipeStorage storage = RecipeStorage.get((ServerWorld)world);
			if(storage.isInUse(pos)) return;
			storage.addRecipe(new TrackedUpgradeRecipe(world, pos, recipe, context, beaconSize, getCrystals(world, pos, beaconSize)));
			if(!event.getPlayer().isCreative())
			{
				event.getItemStack().shrink(1);
			}
			event.setCancellationResult(ActionResultType.SUCCESS);
			event.setCanceled(true);
		}
		else if(item == Items.END_CRYSTAL)
		{
			FusionRecipe recipe = getFusionRecipe(world, context);
			if(recipe == null) return;
			RecipeStorage storage = RecipeStorage.get((ServerWorld)world);
			if(storage.isInUse(pos)) return;
			storage.addRecipe(new TrackedRecipe(world, pos, recipe, context, beaconSize, getCrystals(world, pos, beaconSize)));
			if(!event.getPlayer().isCreative())
			{
				event.getItemStack().shrink(1);
			}
			event.setCancellationResult(ActionResultType.SUCCESS);
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
			copy.getTag().put("fusioned", ByteNBT.ONE);
			event.setOutput(copy);
			event.setCost(level * 4);
			event.setMaterialCost(level);
		}
	}
	
	private int countApexLevels(ListNBT list)
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
		PlayerEntity player = event.player;
		if(player.level.getGameTime() % 200 == 0)
		{
			for(EquipmentSlotType slot : EquipmentSlotType.values())
			{
				ItemStack stack = player.getItemBySlot(slot);
				if(!stack.hasTag()) continue;
				CompoundNBT data = stack.getTag();
				if(!data.getBoolean("fusioned")) {
					clearApex(stack);
					continue;
				}
			}
		}
	}
	
	private void clearApex(ItemStack stack)
	{
		ListNBT list = stack.getEnchantmentTags();
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
	public void onServerTick(WorldTickEvent event)
	{
		if(event.phase == Phase.START || !(event.world instanceof ServerWorld)) return;
		RecipeStorage.get((ServerWorld)event.world).onTick();
	}
	
	@SubscribeEvent
	public void onChunkWatch(ChunkWatchEvent.Watch event)
	{
		SyncRecipePacket packet = RecipeStorage.get(event.getWorld()).getSyncPacket(event.getPos());
		if(packet != null) UEBase.NETWORKING.sendToAllChunkWatchers(event.getWorld().getChunk(event.getPos().x, event.getPos().z), packet);
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
	public void renderLastEvent(RenderWorldLastEvent event)
	{
		ClientRecipeStorage.INSTANCE.render(event.getPartialTicks());
	}
	
	public FusionUpgradeRecipe getUpgradeRecipe(World world, FusionContext context)
	{
		return world.getRecipeManager().getRecipeFor(UEApex.FUSION_UPGRADE, context, world).orElse(new DefaultFusionUpgradeRecipe(context.getLargestEnchantment()));
	}
	
	public FusionRecipe getFusionRecipe(World world, FusionContext context)
	{
		return world.getRecipeManager().getRecipeFor(UEApex.FUSION, context, world).orElse(null);
	}
	
	public FusionContext context(World world, BlockPos pos)
	{
		IItemHandler mainChest = getHandler(world.getBlockEntity(pos.relative(Direction.UP)), Direction.UP);
		if(mainChest == null) return null;
		IItemHandler[] originals = getInventories(world, pos);
		return originals == null ? null : new FusionContext(mainChest, originals);
	}
	
	public List<EnderCrystalEntity> getCrystals(World world, BlockPos pos, int size)
	{
		List<EnderCrystalEntity> crystals = new ObjectArrayList<>();
		EnderCrystalEntity entity = new EnderCrystalEntity(world, pos.getX() + 0.5D, pos.getY()+3.5D, pos.getZ() + 0.5D);
		entity.setShowBottom(false);
        world.addFreshEntity(entity);
        crystals.add(entity);
        for(int i = 0;i<4;i++)
        {
        	Direction dir = Direction.from2DDataValue(i);
    		entity = new EnderCrystalEntity(world, pos.getX() + 0.5D + dir.getStepX() * size, pos.getY()-(size-1), pos.getZ() + 0.5D + dir.getStepZ() * size);
    		entity.setShowBottom(false);
    		entity.setBeamTarget(pos.above(2));
            world.addFreshEntity(entity);
            crystals.add(entity);
        }
		return crystals;
	}
	
	public IItemHandler[] getInventories(World world, BlockPos pos)
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
	
	private IItemHandler getHandler(TileEntity tile, Direction dir)
	{
		if(tile == null || tile.getBlockState().getBlock() != Blocks.CHEST) return null;
		return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite()).orElse(null);
	}
	
	public int isValidBeacon(World world, BlockPos pos)
	{
		TileEntity tile = world.getBlockEntity(pos);
		if(tile instanceof BeaconTileEntity)
		{
			BeaconTileEntity beacon = (BeaconTileEntity)tile;
			return beacon.getLevels() >= 3 && ((BeaconMixin)beacon).getBeaconSegments().size() > 0 ? beacon.getLevels() : 0;
		}
		return 0;
	}
}
