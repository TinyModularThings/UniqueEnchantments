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
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.TieredItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniqueapex.UEApex;
import uniqueapex.enchantments.simple.AbsoluteProtection;
import uniqueapex.enchantments.simple.BlessedBlade;
import uniqueapex.enchantments.simple.SecondLife;
import uniqueapex.enchantments.unique.AeonsFragment;
import uniqueapex.handler.misc.MiningArea;
import uniquebase.api.events.ItemDurabilityChangeEvent;
import uniquebase.handler.MathCache;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;

public class ApexHandler
{
	public static final ApexHandler INSTANCE = new ApexHandler();
	Map<RegistryKey<World>, Int2LongMap> tridentMap = new Object2ObjectOpenHashMap<>();
	Map<RegistryKey<World>, List<MiningArea>> autominer = new Object2ObjectLinkedOpenHashMap<>();
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event)
	{
		if(event.phase == Phase.START || event.side.isClient()) return;
		List<MiningArea> list = autominer.get(event.world.dimension());
		if(list == null) return;
		list.removeIf(T -> T.mine(event.world));
		if(list.isEmpty()) autominer.remove(event.world.dimension());
	}
	
	@SubscribeEvent
	public void onRightClick(RightClickBlock event)
	{
		PlayerEntity player = event.getPlayer();
		if(player == null || !player.isShiftKeyDown()) return;
		int level = MiscUtil.getEnchantmentLevel(UEApex.GAIAS_FRAGMENT, player.getMainHandItem());
		if(level > 0 && player.getMainHandItem().getItem() instanceof TieredItem)
		{
			TieredItem item = (TieredItem)player.getMainHandItem().getItem();
			CompoundNBT nbt = MiscUtil.getPersistentData(player);
			BlockPos pos = event.getPos();
			if(nbt.contains("first_pos")) {
				BlockPos other = BlockPos.of(nbt.getLong("first_pos"));
				if(!player.level.isClientSide()) player.displayClientMessage(new TranslationTextComponent("unique.apex.mine", pos.getX(), pos.getY(), pos.getZ(), other.getX(), other.getY(), other.getZ()), false);
				autominer.computeIfAbsent(player.level.dimension(), T -> new ArrayList<>()).add(new MiningArea(other, pos, (item.getTier().getSpeed() / 30F) * level));
				nbt.remove("first_pos");
			}
			else {
				nbt.putLong("first_pos", pos.asLong());
				if(!player.level.isClientSide()) player.displayClientMessage(new TranslationTextComponent("unique.apex.first_pos", pos.getX(), pos.getY(), pos.getZ()), false);
			}
			event.setCanceled(true);
			event.setCancellationResult(ActionResultType.SUCCESS);
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		int level = MiscUtil.getEnchantedItem(UEApex.ABSOLUTE_PROTECTION, event.getEntityLiving()).getIntValue();
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
	}
	
	@SubscribeEvent
	public void onItemDamage(ItemDurabilityChangeEvent event)
	{
		ItemStack stack = event.item;
		if(stack.getMaxDamage() - stack.getDamageValue() <= 10)
		{
			int level = MiscUtil.getEnchantmentLevel(UEApex.SECOND_LIFE, stack);
			if(level <= 0) return;
			float restore = ((-1/(1+MathCache.LOG.getFloat((int)(1+MiscUtil.getPlayerLevel(event.entity, 100)*SecondLife.RESTORE.getFloat()))))+1);
			stack.setDamageValue(Math.max(0, stack.getDamageValue()-Math.max(5, (int)(stack.getMaxDamage()*restore))));
			MiscUtil.replaceEnchantmentLevel(UEApex.SECOND_LIFE, stack, level-1);
			event.entity.level.playSound(null, event.entity.blockPosition(), UEApex.SECOND_LIFE_SOUND, SoundCategory.AMBIENT, 1F, 1F);
		}
	}
	
	@SubscribeEvent
	public void onWorldTickEvent(WorldTickEvent event)
	{
		if(event.phase == Phase.START || event.side.isClient()) return;
		Int2LongMap map = tridentMap.get(event.world.dimension());
		if(map == null) return;
		World world = event.world;
		Mutable pos = new Mutable();
		for(Iterator<Int2LongMap.Entry> iter = Int2LongMaps.fastIterator(map);iter.hasNext();)
		{
			Int2LongMap.Entry entry = iter.next();
			Entity entity = world.getEntity(entry.getIntKey());
			if(!(entity instanceof TridentEntity))
			{
				iter.remove();
				continue;
			}
			if(world.isEmptyBlock(pos.set(entry.getLongValue())))
			{
				iter.remove();
				continue;
			}
			TileEntity tile = world.getBlockEntity(pos);
			if(!(tile instanceof ITickableTileEntity)) 
			{
				iter.remove();
				continue;
			}
			int interval = MathHelper.ceil(Math.sqrt(AeonsFragment.INTERVAL.get()/((world.getDifficulty().ordinal()+1)*(world.getCurrentDifficultyAt(pos).getEffectiveDifficulty()+1))));
			if(world.getGameTime() % interval != 0)
			{
				continue;
			}
			ItemStack stack = StackUtils.getArrowStack((TridentEntity)entity);
			double attack = EnchantmentHelper.getDamageBonus(stack, CreatureAttribute.UNDEFINED) + 8;
			int level = MiscUtil.getEnchantmentLevel(UEApex.AEONS_FRAGMENT, stack);
			int ticks = Math.max(1, MathHelper.floor(-0.5+Math.sqrt(0.25+attack*AeonsFragment.TICK_SCALE.get(level))));
			ITickableTileEntity tick = (ITickableTileEntity)tile;
			for(int i = 0;i<ticks;i++) {
				tick.tick();
			}
		}
	}
	
	@SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
	public void onBreak(BreakEvent event)
	{
		PlayerEntity player = event.getPlayer();
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
		PlayerEntity player = event.getPlayer();
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
		PlayerEntity player = event.getPlayer();
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
		PlayerEntity player = event.getPlayer();
		if(player == null) return;
		int level = MiscUtil.getEnchantmentLevel(UEApex.PICKAXE_404, player.getMainHandItem());
		if(level > 0 && event.getWorld().getBlockState(event.getPos()).getDestroySpeed(event.getWorld(), event.getPos()) < 0F)
		{
			event.setCancellationResult(ActionResultType.SUCCESS);
			event.setCanceled(true);
			player.swing(Hand.MAIN_HAND);
			event.getWorld().setBlockAndUpdate(event.getPos(), Blocks.AIR.defaultBlockState());
		}
	}
	
	@SubscribeEvent
	public void onArrowHit(ProjectileImpactEvent.Arrow event)
	{
		RayTraceResult result = event.getRayTraceResult();
		if(!(result instanceof BlockRayTraceResult) || result.getType() == Type.MISS) return;
		if(event.getArrow().level.isClientSide) return;
		ItemStack stack = StackUtils.getArrowStack(event.getArrow());
		if(stack.getItem() != Items.TRIDENT || MiscUtil.getEnchantmentLevel(UEApex.AEONS_FRAGMENT, stack) <= 0) return;
		tridentMap.computeIfAbsent(event.getArrow().level.dimension(), T -> new Int2LongLinkedOpenHashMap()).put(event.getArrow().getId(), ((BlockRayTraceResult)result).getBlockPos().asLong());
	}
}