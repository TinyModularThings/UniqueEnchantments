package uniqueapex.handler;

import java.util.Iterator;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniqueapex.UEApex;
import uniqueapex.enchantments.simple.AbsoluteProtection;
import uniqueapex.enchantments.simple.BlessedBlade;
import uniqueapex.enchantments.simple.SecondLife;
import uniqueapex.enchantments.unique.AeonsFragment;
import uniquebase.api.events.ItemDurabilityChangeEvent;
import uniquebase.handler.MathCache;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;

public class ApexHandler
{
	public static final ApexHandler INSTANCE = new ApexHandler();
	Map<RegistryKey<World>, Int2LongMap> tridentMap = new Object2ObjectOpenHashMap<>();
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		int level = MiscUtil.getEnchantedItem(UEApex.ABSOLUTE_PROTECTION, event.getEntityLiving()).getIntValue();
		if(level > 0)
		{
			event.setAmount(event.getAmount() * (1F / (MathCache.LOG10.getFloat((int)(1000+AbsoluteProtection.SCALE.get(level*level))) - 2)));
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