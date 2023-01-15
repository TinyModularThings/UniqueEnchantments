package uniqueapex.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniqueapex.UEApex;
import uniqueapex.enchantments.simple.AbsoluteProtection;
import uniqueapex.enchantments.simple.Accustomed;
import uniqueapex.enchantments.simple.BlessedBlade;
import uniqueapex.enchantments.simple.SecondLife;
import uniqueapex.enchantments.unique.AeonsFragment;
import uniqueapex.handler.misc.MiningArea;
import uniqueapex.network.ApexCooldownPacket;
import uniquebase.UEBase;
import uniquebase.api.events.ItemDurabilityChangeEvent;
import uniquebase.handler.MathCache;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquebase.utils.mixin.common.tile.ChunkMixin;

public class ApexHandler
{
	public static final ApexHandler INSTANCE = new ApexHandler();
	Map<ResourceKey<Level>, Int2LongMap> tridentMap = new Object2ObjectOpenHashMap<>();
	Map<ResourceKey<Level>, List<MiningArea>> autominer = new Object2ObjectLinkedOpenHashMap<>();
	
	@SubscribeEvent
	public void onWorldTick(LevelTickEvent event)
	{
		if(event.phase == Phase.START || event.side.isClient()) return;
		List<MiningArea> list = autominer.get(event.level.dimension());
		if(list == null) return;
		list.removeIf(T -> T.mine(event.level));
		if(list.isEmpty()) autominer.remove(event.level.dimension());
	}
	
	@SubscribeEvent
	public void onRightClick(RightClickBlock event)
	{
		Player player = event.getEntity();
		if(player == null || !player.isShiftKeyDown()) return;
		int level = MiscUtil.getEnchantmentLevel(UEApex.GAIAS_FRAGMENT, player.getMainHandItem());
		if(level > 0 && player.getMainHandItem().getItem() instanceof TieredItem)
		{
			TieredItem item = (TieredItem)player.getMainHandItem().getItem();
			CompoundTag nbt = MiscUtil.getPersistentData(player);
			BlockPos pos = event.getPos();
			if(nbt.contains("first_pos")) {
				BlockPos other = BlockPos.of(nbt.getLong("first_pos"));
				if(!player.level.isClientSide()) player.displayClientMessage(Component.translatable("unique.apex.mine", pos.getX(), pos.getY(), pos.getZ(), other.getX(), other.getY(), other.getZ()), false);
				autominer.computeIfAbsent(player.level.dimension(), T -> new ArrayList<>()).add(new MiningArea(other, pos, (item.getTier().getSpeed() / 30F) * level));
				nbt.remove("first_pos");
			}
			else {
				nbt.putLong("first_pos", pos.asLong());
				if(!player.level.isClientSide()) player.displayClientMessage(Component.translatable("unique.apex.first_pos", pos.getX(), pos.getY(), pos.getZ()), false);
			}
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		System.out.println(event.getAmount());
		int level = MiscUtil.getEnchantedItem(UEApex.ABSOLUTE_PROTECTION, event.getEntity()).getIntValue();
		if(level > 0)
		{
			event.setAmount(event.getAmount() * (1F / (MathCache.LOG10.getFloat((int)(10+Math.sqrt(AbsoluteProtection.SCALE.get(level)))))));
		}
		Entity entity = event.getSource().getEntity();
		if(entity instanceof LivingEntity)
		{
			level = MiscUtil.getEnchantmentLevel(UEApex.BLESSED_BLADE, ((LivingEntity)entity).getMainHandItem());
			if(level > 0)
			{
				event.setAmount(event.getAmount() + (float)Math.pow(1+BlessedBlade.LEVEL_SCALE.get(MiscUtil.getPlayerLevel(entity, 200) * level), 0.2F));
			}
		}
		System.out.println(event.getAmount());
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START || event.side.isClient()) return;
		int level = MiscUtil.getCombinedEnchantmentLevel(UEApex.ACCUSTOMED, event.player);
		if(level > 0)
		{
			CompoundTag tag = MiscUtil.getPersistentData(event.player);
			double current = tag.getDouble(Accustomed.GAGUE_COUNTER) + (Accustomed.SCALE.get(level));
			if(current >= Accustomed.GAUGE.get()) {
				current -= Accustomed.GAUGE.get();
				int points = Mth.nextInt(event.player.getRandom(), 0, 3);
				if(points > 0) {
					for(int i = 0;i<points;i++) event.player.getCooldowns().tick();
					UEBase.NETWORKING.sendToPlayer(new ApexCooldownPacket(points), event.player);
				}
			}
			tag.putDouble(Accustomed.GAGUE_COUNTER, current);
		}
	}
	
	@SubscribeEvent
	public void onItemDamage(ItemDurabilityChangeEvent event)
	{
		ItemStack stack = event.item;
		if(stack.getMaxDamage() - stack.getDamageValue() <= 10)
		{
			int level = MiscUtil.getEnchantmentLevel(UEApex.SECOND_LIFE, stack);
			if(level <= 0) return;
			float restore = ((-1/(1+MathCache.LOG.getFloat((int)(1.25f+MiscUtil.getPlayerLevel(event.entity, 100)*SecondLife.RESTORE.getFloat()))))+1);
			stack.setDamageValue(Math.max(0, stack.getDamageValue()-Math.max(5, (int)(stack.getMaxDamage()*restore))));
			MiscUtil.replaceEnchantmentLevel(UEApex.SECOND_LIFE, stack, level-1);
			event.entity.level.playSound(null, event.entity.blockPosition(), UEApex.SECOND_LIFE_SOUND, SoundSource.AMBIENT, 1F, 1F);
		}
	}
	
	@SubscribeEvent
	public void onWorldTickEvent(LevelTickEvent event)
	{
		if(event.phase == Phase.START || event.side.isClient()) return;
		Int2LongMap map = tridentMap.get(event.level.dimension());
		if(map == null) return;
		Level world = event.level;
		MutableBlockPos pos = new MutableBlockPos();
		for(Iterator<Int2LongMap.Entry> iter = Int2LongMaps.fastIterator(map);iter.hasNext();)
		{
			Int2LongMap.Entry entry = iter.next();
			Entity entity = world.getEntity(entry.getIntKey());
			if(!(entity instanceof ThrownTrident trident))
			{
				iter.remove();
				continue;
			}
			if(world.isEmptyBlock(pos.set(entry.getLongValue())))
			{
				iter.remove();
				continue;
			}
			LevelChunk chunk = world.getChunkAt(pos);
			if(chunk == null)
			{
				iter.remove();
				continue;
			}
			TickingBlockEntity tile = ((ChunkMixin)chunk).getTickerMap().get(pos);
			if(tile == null || tile.isRemoved())
			{
				iter.remove();
				continue;
			}
			int interval = Mth.ceil(Math.sqrt(AeonsFragment.INTERVAL.get()/((world.getDifficulty().ordinal()+1)*(world.getCurrentDifficultyAt(pos).getEffectiveDifficulty()+1))));
			if(world.getGameTime() % interval != 0)
			{
				continue;
			}
			ItemStack stack = StackUtils.getArrowStack(trident);
			double attack = EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED) + 8;
			int level = MiscUtil.getEnchantmentLevel(UEApex.AEONS_FRAGMENT, stack);
			int ticks = Math.max(1, Mth.floor(-0.5+Math.sqrt(0.25+attack*AeonsFragment.TICK_SCALE.get(level))));
			for(int i = 0;i<ticks;i++) {
				tile.tick();
			}
		}
	}
	
	@SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
	public void onBreak(BreakEvent event)
	{
		Player player = event.getPlayer();
		if(player == null) return;
		int level = MiscUtil.getEnchantmentLevel(UEApex.PICKAXE_404, player.getMainHandItem());
		if(level > 0)
		{
			event.setCanceled(false);
			event.setExpToDrop(0);
		}
	}
	
	@SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
	public void onBreakSpeed(BreakSpeed event)
	{
		Player player = event.getEntity();
		if(player == null) return;
		int level = MiscUtil.getEnchantmentLevel(UEApex.PICKAXE_404, player.getMainHandItem());
		if(level > 0)
		{
			event.setCanceled(false);
			event.setNewSpeed(1000000F);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void canHarvest(HarvestCheck event)
	{
		Player player = event.getEntity();
		if(player == null) return;
		int level = MiscUtil.getEnchantmentLevel(UEApex.PICKAXE_404, player.getMainHandItem());
		if(level > 0)
		{
			event.setCanHarvest(false);
		}
	}
	
	@SubscribeEvent
	public void onUnbreakableBreak(LeftClickBlock event)
	{
		Player player = event.getEntity();
		if(player == null) return;
		int level = MiscUtil.getEnchantmentLevel(UEApex.PICKAXE_404, player.getMainHandItem());
		if(level > 0 && event.getLevel().getBlockState(event.getPos()).getDestroySpeed(event.getLevel(), event.getPos()) < 0F)
		{
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
			player.swing(InteractionHand.MAIN_HAND);
			event.getLevel().setBlockAndUpdate(event.getPos(), Blocks.AIR.defaultBlockState());
		}
	}
	
	@SubscribeEvent
	public void onArrowHit(ProjectileImpactEvent event)
	{
		HitResult result = event.getRayTraceResult();
		if(!(result instanceof BlockHitResult hitResult) || result.getType() == Type.MISS || !(event.getProjectile() instanceof AbstractArrow arrow)) return;
		if(event.getProjectile().level.isClientSide) return;
		ItemStack stack = StackUtils.getArrowStack(arrow);
		if(stack.getItem() != Items.TRIDENT || MiscUtil.getEnchantmentLevel(UEApex.AEONS_FRAGMENT, stack) <= 0) return;
		tridentMap.computeIfAbsent(arrow.level.dimension(), T -> new Int2LongLinkedOpenHashMap()).put(arrow.getId(), hitResult.getBlockPos().asLong());
	}
}