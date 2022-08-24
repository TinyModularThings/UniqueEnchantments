package uniqueapex.handler;

import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniqueapex.UEApex;
import uniqueapex.enchantments.simple.AbsoluteProtection;
import uniqueapex.enchantments.simple.BlessedBlade;
import uniqueapex.enchantments.simple.SecondLife;
import uniquebase.api.events.ItemDurabilityChangeEvent;
import uniquebase.handler.MathCache;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;

public class ApexHandler
{
	public static final ApexHandler INSTANCE = new ApexHandler();
	Map<ResourceKey<Level>, Int2LongMap> tridentMap = new Object2ObjectOpenHashMap<>();
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
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
			event.entity.level.playSound(null, event.entity.blockPosition(), UEApex.SECOND_LIFE_SOUND, SoundSource.AMBIENT, 1F, 1F);
		}
	}
	
//	@SubscribeEvent
//	public void onWorldTickEvent(LevelTickEvent event)
//	{
//		if(event.phase == Phase.START || event.side.isClient()) return;
//		Int2LongMap map = tridentMap.get(event.level.dimension());
//		if(map == null) return;
//		Level world = event.level;
//		MutableBlockPos pos = new MutableBlockPos();
//		for(Iterator<Int2LongMap.Entry> iter = Int2LongMaps.fastIterator(map);iter.hasNext();)
//		{
//			Int2LongMap.Entry entry = iter.next();
//			Entity entity = world.getEntity(entry.getIntKey());
//			if(!(entity instanceof ThrownTrident trident))
//			{
//				iter.remove();
//				continue;
//			}
//			if(world.isEmptyBlock(pos.set(entry.getLongValue())))
//			{
//				iter.remove();
//				continue;
//			}
//			BlockEntity tile = world.getBlockEntity(pos);
//			if(!(tile instanceof TickableBlockEntity)) 
//			{
//				iter.remove();
//				continue;
//			}
//			int interval = Mth.ceil(Math.sqrt(AeonsFragment.INTERVAL.get()/((world.getDifficulty().ordinal()+1)*(world.getCurrentDifficultyAt(pos).getEffectiveDifficulty()+1))));
//			if(world.getGameTime() % interval != 0)
//			{
//				continue;
//			}
//			ItemStack stack = StackUtils.getArrowStack(trident);
//			double attack = EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED) + 8;
//			int level = MiscUtil.getEnchantmentLevel(UEApex.AEONS_FRAGMENT, stack);
//			int ticks = Math.max(1, Mth.floor(-0.5+Math.sqrt(0.25+attack*AeonsFragment.TICK_SCALE.get(level))));
//			TickableBlockEntity tick = (TickableBlockEntity)tile;
//			for(int i = 0;i<ticks;i++) {
//				tick.tick();
//			}
//		}
//	}
	
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