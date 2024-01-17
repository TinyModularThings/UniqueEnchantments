package uniquee.handler;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniquebase.api.events.ItemDurabilityChangeEvent;
import uniquebase.api.events.SetItemDurabilityEvent;
import uniquebase.handler.MathCache;
import uniquebase.utils.EnchantmentContainer;
import uniquebase.utils.ICurioHelper.CurioSlot;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquebase.utils.events.EndermenLookEvent;
import uniquebase.utils.events.PiglinWearableCheckEvent;
import uniquebase.utils.mixin.common.entity.ArrowMixin;
import uniquebase.utils.mixin.common.entity.CombatTrackerMixin;
import uniquebase.utils.mixin.common.entity.PotionMixin;
import uniquebase.utils.mixin.common.item.MapDataMixin;
import uniquee.UE;
import uniquee.enchantments.complex.EnderMending;
import uniquee.enchantments.complex.Momentum;
import uniquee.enchantments.complex.PerpetualStrike;
import uniquee.enchantments.complex.SmartAss;
import uniquee.enchantments.complex.SpartanWeapon;
import uniquee.enchantments.complex.SwiftBlade;
import uniquee.enchantments.curse.ComboStar;
import uniquee.enchantments.curse.DeathsOdium;
import uniquee.enchantments.curse.PestilencesOdium;
import uniquee.enchantments.simple.AmelioratedBaneOfArthropod;
import uniquee.enchantments.simple.AmelioratedSharpness;
import uniquee.enchantments.simple.AmelioratedSmite;
import uniquee.enchantments.simple.Berserk;
import uniquee.enchantments.simple.BoneCrusher;
import uniquee.enchantments.simple.BrittlingBlade;
import uniquee.enchantments.simple.EnderEyes;
import uniquee.enchantments.simple.Range;
import uniquee.enchantments.simple.SagesBlessing;
import uniquee.enchantments.simple.Swift;
import uniquee.enchantments.simple.Vitae;
import uniquee.enchantments.unique.AlchemistsGrace;
import uniquee.enchantments.unique.AresBlessing;
import uniquee.enchantments.unique.ClimateTranquility;
import uniquee.enchantments.unique.Cloudwalker;
import uniquee.enchantments.unique.Ecological;
import uniquee.enchantments.unique.EnderMarksmen;
import uniquee.enchantments.unique.EndestReap;
import uniquee.enchantments.unique.FastFood;
import uniquee.enchantments.unique.Grimoire;
import uniquee.enchantments.unique.IcarusAegis;
import uniquee.enchantments.unique.NaturesGrace;
import uniquee.enchantments.unique.PhoenixBlessing;
import uniquee.enchantments.unique.WarriorsGrace;
import uniquee.enchantments.upgrades.AmelioratedUpgrade;
import uniquee.handler.potion.Thrombosis;

public class EntityEvents
{
	public static final EntityEvents INSTANCE = new EntityEvents();
	static final ThreadLocal<UUID> ENDER_MAN_HIT = new ThreadLocal<>();
	public static final ThreadLocal<Boolean> BREAKING = ThreadLocal.withInitial(() -> false);
	public RandomSource rand = RandomSource.create();

	@SubscribeEvent
	public void onEndermenLookEvent(EndermenLookEvent event)
	{
		if(MiscUtil.getEnchantmentLevel(UE.ENDER_EYES, event.getEntity().getItemBySlot(EquipmentSlot.HEAD)) > 0)
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPotionApplied(MobEffectEvent.Added event)
	{
		MobEffectInstance instance = event.getEffectInstance();
		if(instance.getEffect().getCategory() == MobEffectCategory.HARMFUL)
		{
			int points = UE.PESTILENCE_UPGRADE.getCombinedPoints(event.getEntity());
			if(points > 0)
			{
				((PotionMixin)instance).setPotionDuration((int)(instance.getDuration() * (100/(100+Math.pow(points, 0.25)))));
			}
		}
	}
	

	@SubscribeEvent
	public void onPotionApplicable(MobEffectEvent.Applicable event) {
		LivingEntity ent = event.getEntity();
		if(event.getEffectInstance().getEffect() == MobEffects.MOVEMENT_SLOWDOWN && MiscUtil.isTranscendent(ent, event.getEntity().getMainHandItem(), UE.CLIMATE_TRANQUILITY) && ent.level.getBiome(ent.blockPosition()).containsTag(Tags.Biomes.IS_COLD)) 
		{
			event.setResult(Result.DENY);
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onLivingTick(LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if(entity.getHealth() < (entity.getMaxHealth()*Berserk.TRANSCENDED_HEALTH.get())) return;
		ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
		if(MiscUtil.getEnchantmentLevel(UE.BERSERKER, stack) > 0 && MiscUtil.isTranscendent(entity, stack, UE.BERSERKER))
		{
			entity.setHealth(entity.getMaxHealth()*0.5F);
		}
	}
	
	@SubscribeEvent
	public void onPiglinCheck(PiglinWearableCheckEvent event)
	{
		if(MiscUtil.getEnchantedItem(UE.TREASURERS_EYES, event.getEntity()).getIntValue() > 0)
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START) return;
		Player player = event.player;
		EnchantmentContainer container = new EnchantmentContainer(player);
		if(event.side.isServer())
		{
			if(player.level.getGameTime() % 300 == 0 && player.getHealth() < player.getMaxHealth()) 
			{
				int level = container.getEnchantment(UE.NATURES_GRACE, EquipmentSlot.CHEST);
				if(level > 0 && player.getCombatTracker().getKiller() == null) 
				{
					int value = StackUtils.hasBlockCount(player.level, player.blockPosition(), (int)Math.sqrt(player.experienceLevel), NaturesGrace.FLOWERS);
					if(value >= 4) player.heal((float)Math.log10(1+NaturesGrace.HEALING.getAsDouble(value*level)));
				}
			}
			if(player.level.getGameTime() % 400 == 0)
			{
				ItemStack stack = player.getMainHandItem();
				if(MiscUtil.isTranscendent(player, stack, UE.FAST_FOOD))
				{
					FoodData food = player.getFoodData();
					int stored = StackUtils.getInt(stack, FastFood.FASTFOOD, 0);
					int num = Math.min(stored, 20-food.getFoodLevel());
					if(num > 0)
					{
						food.eat(num, num);
						StackUtils.setInt(stack, FastFood.FASTFOOD, stored-num);
						 
					}
				}
				if(StackUtils.hasBlockCount(player.level, player.blockPosition(), 1, Ecological.STATES) > 0) {
					EquipmentSlot[] slots = MiscUtil.getEquipmentSlotsFor(UE.ECOLOGICAL);
					for(int i = 0;i<slots.length;i++)
					{
						ItemStack equipStack = player.getItemBySlot(slots[i]); 
						int level = container.getEnchantment(UE.ECOLOGICAL, slots[i]);
						if(level > 0 && equipStack.isDamaged())
						{
							equipStack.hurtAndBreak((int) Ecological.AMOUNT_SCALE.get(-level), player, MiscUtil.get(slots[i]));
						}
					}
				}
				EnderMending.shareXP(player, container);				
			}
			if(player.level.getGameTime() % 100 == 0)
			{
				for(Int2ObjectMap.Entry<ItemStack> entry : container.getEnchantedItems(UE.ENDER_MENDING))
				{
					ItemStack stack = entry.getValue();
					if(stack.isDamaged())
					{
						int stored = StackUtils.getInt(stack, EnderMending.ENDER_TAG, 0);
						if(stored > 0)
						{
							int toRemove = Math.min(stack.getDamageValue(), stored);
							stack.setDamageValue(stack.getDamageValue() - toRemove);
							StackUtils.setInt(stack, EnderMending.ENDER_TAG, stored - toRemove);
						}
					}
				}
				int level = container.getEnchantment(UE.ENDEST_REAP, EquipmentSlot.MAINHAND);
				if(level > 0)
				{
					StackUtils.setInt(player.getMainHandItem(), EndestReap.REAP_STORAGE, player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getInt(EndestReap.REAP_STORAGE));
				}

				level = UE.PHOENIX_UPGRADE.getCombinedPoints(player);
				if(level > 0 && !player.getCombatTracker().isInCombat())
				{
					player.heal((float)(Math.log(1+level)/10));
				}
				level = container.getEnchantment(UE.TREASURERS_EYES, EquipmentSlot.HEAD);
				if(level > 0) {
					player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600));
				}
			}
			if(player.level.getGameTime() % 30 == 0)
			{
				ClimateTranquility.onClimate(player, container);
			}
			if(player.level.getGameTime() % 10 == 0)
			{
				Object2IntMap.Entry<ItemStack> result = MiscUtil.getEquipment(player, UE.ICARUS_AEGIS, CurioSlot.BACK);
				if(result.getIntValue() > 0)
				{
					result.getKey().getTag().putBoolean(IcarusAegis.FLYING_TAG, player.isFallFlying());
				}
				int level = container.getEnchantment(UE.TREASURERS_EYES, EquipmentSlot.HEAD);
				if(level > 0)
				{
					LivingEntity hit = MiscUtil.targetEntities(LivingEntity.class, player, 64, EnderEyes::isValidEntity);
					if(hit != null) hit.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
				}
			}
			if(player.level.getGameTime() % 50 == 0)
			{
				int level = container.getCombinedEnchantment(UE.PESTILENCES_ODIUM);
				if(level > 0)
				{
					List<LivingEntity> living = player.level.getEntitiesOfClass(LivingEntity.class, new AABB(player.blockPosition()).inflate(PestilencesOdium.RADIUS.get()));
					for(int i = 0,m=living.size();i<m;i++)
					{
						LivingEntity entity = living.get(i);
						if(!(entity.getAbsorptionAmount() > 1) && entity != player)
						{
							entity.addEffect(new MobEffectInstance(UE.PESTILENCES_ODIUM_POTION, 200, level));
						}
					}
				}
			}
			if(player.level.getGameTime() % 20 == 0)
			{
				Object2IntMap.Entry<EquipmentSlot> level = container.getEnchantedItem(UE.SAGES_BLESSING);
				if(level.getIntValue() > 0 && !MiscUtil.isTranscendent(player, player.getItemBySlot(level.getKey()), UE.SAGES_BLESSING))
				{
					player.causeFoodExhaustion(0.01F * level.getIntValue());
				}
			}
			CompoundTag data = event.player.getPersistentData();
			if(data.contains(DeathsOdium.CURSE_DAMAGE) && data.getLong(DeathsOdium.CURSE_TIMER) < event.player.level.getGameTime())
			{
				int total = (int) Math.pow(data.getFloat(DeathsOdium.CURSE_DAMAGE) * rand.nextDouble(), 0.25d);
				if(total > 0)
				{
					for(int i = 0;i<total;i++) 
					{
						DeathsOdium.applyStackBonus(event.player);
					}
				}	
				data.remove(DeathsOdium.CURSE_DAMAGE);
			}
			int level = container.getEnchantment(UE.ENDER_EYES, EquipmentSlot.HEAD);
			if(level > 0)
			{
				LivingEntity hit = MiscUtil.targetEntities(LivingEntity.class, player, 64, EnderEyes::isValidEntity);
				if(hit != null) hit.addEffect(new MobEffectInstance(UE.INTERCEPTION, 300));
			}
		}
		int level = container.getEnchantment(UE.CLOUD_WALKER, EquipmentSlot.FEET);
		if(level > 0)
		{
			CompoundTag nbt = player.getPersistentData();
			if(player.isShiftKeyDown() && !nbt.getBoolean(Cloudwalker.TRIGGER) && (!player.isOnGround() || nbt.getBoolean(Cloudwalker.ENABLED)))
			{
				nbt.putBoolean(Cloudwalker.ENABLED, !nbt.getBoolean(Cloudwalker.ENABLED));
				nbt.putBoolean(Cloudwalker.TRIGGER, true);
			}
			else if(!player.isShiftKeyDown())
			{
				nbt.putBoolean(Cloudwalker.TRIGGER, false);
			}
			ItemStack stack = player.getItemBySlot(EquipmentSlot.FEET);
			if(nbt.getBoolean(Cloudwalker.ENABLED))
			{
				int value = StackUtils.getInt(stack, Cloudwalker.TIMER, Cloudwalker.TICKS.get(level) * 5);
				if(value <= 0)
				{
					nbt.putBoolean(Cloudwalker.ENABLED, false);
					return;
				}
				Vec3 vec = player.getDeltaMovement();
				player.setDeltaMovement(vec.x, 0D, vec.z);
				player.causeFallDamage(player.fallDistance, 1F, DamageSource.FALL);
				player.fallDistance = 0F;
				player.setOnGround(true);
				if(!player.isCreative())
				{
					boolean levi = player.hasEffect(MobEffects.LEVITATION);
					int leviLevel = levi ? player.getEffect(MobEffects.LEVITATION).getAmplifier()+1 : 0;
					StackUtils.setInt(stack, Cloudwalker.TIMER, value-Math.max(1, 5 - (leviLevel * 2)));
					int time = Math.max(1, (int)Math.pow(20 * (Math.sqrt(level) / (leviLevel+1)), Cloudwalker.TRANSCENDED_EXPONENT.get()));
					if(player.level.getGameTime() % time == 0)
					{
						stack.hurtAndBreak(1, player, MiscUtil.get(EquipmentSlot.FEET));
					}
				}
			}
			else
			{
				StackUtils.setInt(stack, Cloudwalker.TIMER, Cloudwalker.TICKS.get(level) * 5);
			}
		}
		level = container.getEnchantment(UE.SWIFT, EquipmentSlot.LEGS);
		if(level > 0 && player.onClimbable() && player.zza != 0F && player.getDeltaMovement().y() > 0 && player.getDeltaMovement().y() <= 0.2 && !player.isShiftKeyDown())
		{
			player.setDeltaMovement(player.getDeltaMovement().add(0, 0.05 * level, 0));
		}
	}
	
	@SubscribeEvent
	public void onXPPickup(PickupXp event)
	{
		Player player = event.getEntity();
		if(player == null) return;
		List<ItemStack> all = new ObjectArrayList<>();
		List<ItemStack> ender = new ObjectArrayList<>();
		EquipmentSlot[] slots = MiscUtil.getEquipmentSlotsFor(UE.ENDER_MENDING);
		for(int i = 0;i<slots.length;i++)
		{
			ItemStack stack = player.getItemBySlot(slots[i]);
			if(stack.isEmpty()) continue;
			int level = MiscUtil.getEnchantmentLevel(UE.ENDER_MENDING, stack);
			if(level > 0)
			{
				all.add(stack);
				ender.add(stack);
			}
			else if(stack.isDamaged() && MiscUtil.getEnchantmentLevel(Enchantments.MENDING, stack) > 0)
			{
				all.add(stack);
			}
		}
		if(ender.size() <= 0) return;
		ExperienceOrb orb = event.getOrb();
		int xp = orb.value;
		int totalXP = xp * 2;
		int usedXP = 0;
		usedXP += StackUtils.evenDistribute(totalXP, orb.level.random, all, (stack, i) -> {
			int used = Math.min(i, stack.getDamageValue());
            stack.setDamageValue(stack.getDamageValue() - used);
            return used;
		});
		if(usedXP >= totalXP)
		{
			orb.value = 0;
			player.take(orb, 1);
			orb.remove(RemovalReason.DISCARDED);
			event.setCanceled(true);
			return;
		}
		usedXP += StackUtils.evenDistribute(totalXP - usedXP, orb.level.random, ender, (stack, i) -> {
			int max = Mth.ceil(EnderMending.LIMIT.get() * Math.pow(EnderMending.LIMIT_MULTIPLIER.get(), Math.sqrt(MiscUtil.getEnchantmentLevel(UE.ENDER_MENDING, stack))));
			int stored = StackUtils.getInt(stack, EnderMending.ENDER_TAG, 0);
			int left = Math.min(i, max - stored);
			StackUtils.setInt(stack, EnderMending.ENDER_TAG, stored + left);
			return left;
		});
		int left = (totalXP - usedXP) / 2;
		player.take(event.getOrb(), 1);
		event.getOrb().remove(RemovalReason.DISCARDED);
		event.setCanceled(true);
		if(left > 0) player.giveExperiencePoints(left);
	}
	
	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		if(event.getEntity() == null)
		{
			return;
		}
		Player player = event.getEntity();
		ItemStack held = player.getMainHandItem();
		Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(held);
		int level = ench.getInt(UE.MOMENTUM);
		if(level > 0)
		{
			event.setNewSpeed(event.getNewSpeed() * (float)Momentum.calculateBoost(player, level));
		}
		level = ench.getInt(UE.RANGE);
		if(level > 0)
		{
			double value = MiscUtil.getBaseAttribute(player, ForgeMod.REACH_DISTANCE.get());
			if(value * value < event.getPosition().orElse(BlockPos.ZERO).distToCenterSqr(player.position()))
			{
				event.setNewSpeed(event.getNewSpeed() * (1+Range.REDUCTION.getFloat(level)));
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockBreak(BreakEvent event)
	{
		if(event.getPlayer() == null)
		{
			return;
		}
		Player player = event.getPlayer();
		ItemStack held = player.getMainHandItem();
		Object2IntMap<Enchantment> enchs = MiscUtil.getEnchantments(held);
		int level = enchs.getInt(UE.ALCHEMISTS_GRACE);
		if(level > 0)
		{
			AlchemistsGrace.applyToEntity(event.getPlayer(), true, event.getState().getDestroySpeed(event.getLevel(), event.getPos()));
		}
		level = enchs.getInt(UE.SMART_ASS);
		if(level > 0)
		{
			if(!BREAKING.get() && SmartAss.VALID_STATES.test(event.getState()))
			{
				Block block = event.getState().getBlock();
				int limit = SmartAss.RANGE.get(level);
				Level world = (Level)event.getLevel();
				BlockState lastState = null;
				BlockPos lastPos = null;
				for(int i = 1;i<limit;i++)
				{
					BlockPos pos = event.getPos().above(i);
					BlockState state = world.getBlockState(pos);
					if(state.getBlock() != block)
					{
						continue;
					}
					lastState = state;
					lastPos = pos;
				}
				BREAKING.set(true);
				if(lastState != null && MiscUtil.harvestBlock(event, lastState, lastPos))
				{
					BREAKING.set(false);
					event.setCanceled(true);
					return;
				}
				BREAKING.set(false);
			}
		}
		level = enchs.getInt(UE.SAGES_BLESSING);
		if(level > 0)
		{
			double stacks = 1;
			for(EquipmentSlot slot : MiscUtil.getSlotsFor(UE.SAGES_BLESSING))
			{
				stacks += StackUtils.getInt(player.getItemBySlot(slot), SagesBlessing.SAGES_XP, 0);
			}
			double val = Math.pow(event.getLevel().getRandom().nextInt(enchs.getInt(Enchantments.BLOCK_FORTUNE)+1)+1, 2);
			double form = Math.pow(1+SagesBlessing.XP_BOOST.get(level*stacks*val), 0.1);
			event.setExpToDrop((int) (event.getExpToDrop() * (MiscUtil.isTranscendent(player, held, UE.SAGES_BLESSING) ? Math.pow(form, SagesBlessing.TRANSCENDED_BOOST.get()) : form)));
		}
	}
	
	@SubscribeEvent
	public void onItemClick(RightClickItem event)
	{
		ItemStack stack = event.getItemStack();
		if(!event.getLevel().isClientSide && stack.getItem() instanceof MapItem && MiscUtil.getEnchantmentLevel(UE.ENDER_LIBRARIAN, stack) > 0)
		{
			Level world = event.getLevel();
			MapItemSavedData data = MapItem.getSavedData(stack, world);
			if(data == null || !data.dimension.location().equals(world.dimension().location()))
			{
				return;
			}
			int x = data.x;
			int z = data.z;
			BlockPos position = null;
			CompoundTag nbt = stack.getTag();
			boolean isBanner = false;
	        if (nbt != null)
	        {
	        	//Have to do it that way because Mine-craft decorations are rotated and its annoying to math that out properly.
	            ListTag list = nbt.getList("Decorations", 10);
	            for(int i = 0,m=list.size();i<m;i++)
	            {
	                CompoundTag nbtData = list.getCompound(i);
	                if(nbtData.getString("id").equalsIgnoreCase("+"))
	                {
	                	position = new BlockPos(nbtData.getInt("x") - 20, 255, nbtData.getInt("z") - 20);
	                }
	            }
	            if(position == null)
	            {
					List<MapBanner> banner = new ObjectArrayList<>(((MapDataMixin)data).getBanners().values());
					if(banner.size() > 0)
					{
						position = banner.get(world.random.nextInt(banner.size())).getPos();
						isBanner = true;
					}
	            }
	        }
	        if(position != null)
	        {
				BlockPos pos = event.getLevel().getHeightmapPos(Types.MOTION_BLOCKING, position);
				int d = 0,c = 0;
            	while(d++ < 256) 
            	posLoop: {
            		if(world.getBlockState(pos).getBlock() == Blocks.AIR) {
            			if(c > 0) {
                			BlockPos pos1 = new BlockPos(pos.getX(), pos.getY()+d, pos.getZ());
                			pos = pos1;
                			break posLoop;
                		}
            			c++;
            		}
            	}
				event.getEntity().teleportTo(pos.getX() + 0.5F, Math.max(isBanner ? 0 : world.getSeaLevel(), pos.getY() + 1), pos.getZ() + 0.5F);
				world.playSound(null, event.getEntity().blockPosition(), UE.ENDER_LIBRARIAN_SOUND, SoundSource.AMBIENT, 100F, 2F);
	        }
	        else
	        {
		        int limit = 64 * (1 << data.scale) * 2;
		        int xOffset = (int)((event.getLevel().random.nextDouble() - 0.5D) * limit);
		        int zOffset = (int)((event.getLevel().random.nextDouble() - 0.5D) * limit);
				BlockPos pos = event.getLevel().getHeightmapPos(Types.MOTION_BLOCKING, new BlockPos(x + xOffset, 255, z + zOffset));
				event.getEntity().teleportTo(pos.getX() + 0.5F, Math.max(world.getSeaLevel(), pos.getY() + 1), pos.getZ() + 0.5F);
				world.playSound(null, event.getEntity().blockPosition(), UE.ENDER_LIBRARIAN_SOUND, SoundSource.AMBIENT, 100F, 2F);
	        }
	        if(stack.getCount() <= 1) MiscUtil.decreaseEnchantmentLevel(UE.ENDER_LIBRARIAN, stack);
	        else 
	        {
	        	ItemStack newStack = stack.split(1);
	        	MiscUtil.decreaseEnchantmentLevel(UE.ENDER_LIBRARIAN, newStack);
	        	if(!event.getEntity().getInventory().add(newStack))
	        	{
	        		event.getEntity().drop(newStack, false);
	        	}
	        }
		}
	}
	
	@SubscribeEvent
	public void onItemUseStart(LivingEntityUseItemEvent.Start event) {
		if(event.getDuration() <= 10) return;
		if(MiscUtil.getEnchantedItem(UE.COMBO_STAR, event.getEntity()).getIntValue() > 0)
		{
			int counter = MiscUtil.getPersistentData(event.getEntity()).getInt(ComboStar.COMBO_NAME);
			event.setDuration(Math.max(10, (int)(event.getDuration() / MathCache.LOG10.get((int) (10+ComboStar.COUNTER_MULTIPLIER.get(counter))))));
		}
	}
	
	@SubscribeEvent
	public void onEntityHeal(LivingHealEvent event)
	{
		LivingEntity entity = event.getEntity();
		if(entity.hasEffect(UE.THROMBOSIS)) {
			MobEffectInstance eff = entity.getEffect(UE.THROMBOSIS);
			double a = ((Thrombosis)eff.getEffect()).getChance() * (eff.getAmplifier()+1);
			if(a >= 1D || rand.nextDouble() < a) {
				event.setAmount(0);
			}
		}
		if(MiscUtil.isTranscendent(entity, entity.getItemBySlot(EquipmentSlot.CHEST), UE.BERSERKER) && entity.getHealth() > (entity.getMaxHealth()*Berserk.TRANSCENDED_HEALTH.get()-0.25F) && MiscUtil.getEnchantmentLevel(UE.BERSERKER, entity.getItemBySlot(EquipmentSlot.CHEST)) > 0)
		{
			event.setAmount(0F);
		}
	}
	
	@SubscribeEvent
	public void onDurabilitySet(SetItemDurabilityEvent event) {
		int oldDur = event.getDurability();
		ItemStack stack = event.getItem();
		int level = MiscUtil.getEnchantmentLevel(UE.GRIMOIRE, stack);
		if(level > 0) {
			int enchantability = stack.getEnchantmentValue();
			int totalLevel = MiscUtil.getItemLevel(stack);
			int newDur = (int)Math.ceil((oldDur+Grimoire.FLAT_SCALING.get(totalLevel))*Math.sqrt((100+(totalLevel+enchantability)*Grimoire.LEVEL_SCALING.get(level))/100));
			event.setDurability(newDur);
		}
	}
	
	@SubscribeEvent
	public void onKnockback(LivingKnockBackEvent event) 
	{
		int level = MiscUtil.getEnchantmentLevel(UE.MOMENTUM, event.getEntity().getItemInHand(InteractionHand.MAIN_HAND));
		if(level > 0) 
		{
			event.setStrength(event.getStrength() * (float)Momentum.calculateBoost(event.getEntity(), level));
		}
	}
	
	@SubscribeEvent
	public void onEntityHit(LivingAttackEvent event)
	{
		AlchemistsGrace.applyToEntity(event.getSource().getEntity(), false, 1.5F);
	}
	
	@SubscribeEvent
	public void onEntityAttack(LivingHurtEvent event)
	{
		LivingEntity target = event.getEntity();
		Entity entity = event.getSource().getEntity();
		
		if(entity instanceof LivingEntity base)
		{
			ItemStack stack = event.getSource().getDirectEntity() instanceof ThrownTrident ? ((ArrowMixin)event.getSource().getDirectEntity()).getArrowItem() : base.getMainHandItem();
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
			
			int level = enchantments.getInt(UE.SWIFT_BLADE);
			if(level > 0)
			{
				if(MiscUtil.isTranscendent(base, stack, UE.SWIFT_BLADE) ) {
					event.setAmount( (event.getAmount() + (float) Math.min(target.getAttribute(Attributes.ATTACK_SPEED) != null ? target.getAttributeValue(Attributes.ATTACK_SPEED) : 0, SwiftBlade.ATTACK_SPEED_CAP.get())));
				}
				event.setAmount(event.getAmount() * (float) Math.log(2+Math.pow(1+5*target.getDeltaMovement().horizontalDistance(),-2)*level));
			}
			level = enchantments.getInt(UE.FOCUS_IMPACT);
			if(level > 0)
			{
				AttributeInstance attr = base.getAttribute(Attributes.ATTACK_SPEED);
				if(attr != null)
				{
					double val = MiscUtil.getAttackSpeed(base);
					event.setAmount(event.getAmount() * (float) ((-(level/(8f+level)))*Math.pow(val-1.2f,1f/3f)+1f));
				}
			}
			level = enchantments.getInt(UE.SPARTAN_WEAPON);
			if(level > 0 && base.getOffhandItem().getItem() instanceof ShieldItem)
			{
				AttributeInstance atkSpd = base.getAttribute(Attributes.ATTACK_SPEED);
				AttributeInstance atkDmg = base.getAttribute(Attributes.ATTACK_DAMAGE);
				event.getSource().bypassMagic().bypassInvul();
				event.setAmount((float)(event.getAmount() * (1D + Math.pow((SpartanWeapon.EXTRA_DAMAGE.getFloat(level)*atkDmg.getValue())/atkSpd.getValue(), 0.06))));
			}
			level = enchantments.getInt(UE.BRITTLING_BLADE);
			if(level > 0) 
			{
				double expo = MiscUtil.isTranscendent(base, stack, UE.BRITTLING_BLADE) ? BrittlingBlade.DURABILITY_EXPONENT.get() * BrittlingBlade.TRANSCENDED_EXPONENT_SCALING.get() : BrittlingBlade.DURABILITY_EXPONENT.get();
				event.setAmount((float) (event.getAmount() * Math.log(Math.E + BrittlingBlade.DAMAGE_SCALING.get(level*Math.pow(1-(stack.getDamageValue()/stack.getMaxDamage()), expo)))));
			}
			level = MiscUtil.getEnchantedItem(UE.CLIMATE_TRANQUILITY, base).getIntValue();
			if(level > 0)
			{
				Holder<Biome> effects = base.level.getBiome(base.blockPosition());
				boolean hasHot = effects.containsTag(Tags.Biomes.IS_HOT) || effects.containsTag(BiomeTags.IS_NETHER);
				boolean hasCold = effects.containsTag(Tags.Biomes.IS_COLD);
				if(hasHot && !hasCold)
				{
					target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ClimateTranquility.SLOW_TIME.get() * level, level));
				}
				else if(hasCold && !hasHot)
				{
					target.setSecondsOnFire(level * ClimateTranquility.BURN_TIME.get());
				}
			}
			if(event.getEntity() instanceof AbstractSkeleton)
			{
				level = enchantments.getInt(UE.BONE_CRUSH);
				if(level > 0 && BoneCrusher.isNotArmored((AbstractSkeleton)event.getEntity()))
				{
					event.setAmount((float)(event.getAmount() * (Math.log10(10F+BoneCrusher.BONUS_DAMAGE.getFloat(level)))));
				}
			}
			level = MiscUtil.getEnchantmentLevel(UE.BERSERKER, base.getItemBySlot(EquipmentSlot.CHEST));
			if(level > 0)
			{
				event.setAmount(event.getAmount() * (float)(1D + (Math.pow(1-(Berserk.MIN_HEALTH.getMax(base.getHealth(), 1D)/base.getMaxHealth()), 1D/level) * Berserk.PERCENTUAL_DAMAGE.get())));
			}
			level = enchantments.getInt(UE.RANGE);
			if(level > 0 && base instanceof Player player)
			{
				double value = MiscUtil.getBaseAttribute(player, ForgeMod.ATTACK_RANGE.get());
				if(value * value < new BlockPos(target.position()).distToCenterSqr(player.position()))
				{
					event.setAmount(event.getAmount() * (1F + Range.REDUCTION.getFloat(level)));
				}
			}
			level = MiscUtil.getCombinedEnchantmentLevel(UE.COMBO_STAR, base);
			if(level > 0)
			{
				event.setAmount((float)(event.getAmount()*Math.pow(ComboStar.DAMAGE_LOSS.get(), Math.sqrt(level))));
			}
			level = MiscUtil.getEnchantmentLevel(UE.ENDER_EYES, target.getItemBySlot(EquipmentSlot.HEAD));
			if(level > 0 && EnderEyes.AFFECTED_ENTITIES.contains(base.getType()) && MiscUtil.isTranscendent(target, target.getItemBySlot(EquipmentSlot.HEAD), UE.ENDER_EYES) && rand.nextDouble() < EnderEyes.TRANSCENDED_CHANCE.get()) {
				base.hurt(DamageSource.OUT_OF_WORLD, (float) Math.sqrt(base.getMaxHealth()));
			}
			level = enchantments.getInt(UE.MOMENTUM);
			if(level > 0) 
			{
				event.setAmount(event.getAmount() * (float)Momentum.calculateBoost(entity, level));
			}
			level = enchantments.getInt(UE.PERPETUAL_STRIKE);
			if(level > 0 && !(event.getSource().getDirectEntity() instanceof ThrownTrident))
			{
				int count = StackUtils.getInt(stack, PerpetualStrike.HIT_COUNT, 0);
				int lastEntity = StackUtils.getInt(stack, PerpetualStrike.HIT_ID, 0);
				int mercy = StackUtils.getInt(stack, "mercy", PerpetualStrike.TRANSCENDED_MERCY.get());
				int mercyReset = PerpetualStrike.TRANSCENDED_MERCY.get();
				if(MiscUtil.isTranscendent(base, stack, UE.PERPETUAL_STRIKE))
				{
					if(lastEntity != target.getId())
					{
						if(mercy-- > 0)
						{
							count = 0;
							mercy = mercyReset;
						}
					}
					else
					{
						mercy = mercyReset;
					}
					StackUtils.setInt(stack, "mercy", mercy);
				}
				else if(lastEntity != target.getId())
				{
					count = 0;
				}
				StackUtils.setInt(stack, PerpetualStrike.HIT_COUNT, Math.min(count+1, PerpetualStrike.HIT_CAP.get(level)));
				if(rand.nextInt(100) <= count) {
					target.addEffect(new MobEffectInstance(UE.THROMBOSIS, 100*level, level-1));
				}
				double damage = Math.pow((target.getHealth()*PerpetualStrike.PER_HIT_LEVEL.get(count))/MiscUtil.getAttackSpeed(base, 1D), 0.25);
				double multiplier = PerpetualStrike.SCALING_STATE.get() ? 1 + Math.pow(count * PerpetualStrike.MULTIPLIER.get(), 2)/20 : Math.log10(10+damage*count*PerpetualStrike.MULTIPLIER.get());
				MiscUtil.doNewDamageInstance(target, UE.PERPETUAL_STRIKE_DAMAGE, (float)(((event.getAmount()+damage)*multiplier)-event.getAmount()));
				StackUtils.setInt(stack, PerpetualStrike.HIT_ID, target.getId());
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void damage(BreakSpeed event) {
		//TODO DELETE ME OR FUCKING USE ME!
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		LivingEntity target = event.getEntity();
		Entity entity = event.getSource().getEntity();
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			ItemStack stack = event.getSource().getDirectEntity() instanceof ThrownTrident trident ? ((ArrowMixin)trident).getArrowItem() : base.getMainHandItem();
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);

			int level = enchantments.getInt(UE.ADV_SHARPNESS);
			if(level > 0 && MiscUtil.isTranscendent(base, stack, UE.ADV_SHARPNESS))
			{
				if(stack.getItem() instanceof TieredItem)
				{
					event.setAmount(event.getAmount() + (float)Math.sqrt(rand.nextDouble() * AmelioratedSharpness.TRANSCENDED_DAMAGE_MULTIPLIER.get() * (((TieredItem)stack.getItem()).getTier().getAttackDamageBonus() + EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED) * (base.hasEffect(MobEffects.DAMAGE_BOOST) ? 2 : 1))));
				}
				else
				{
					event.setAmount(event.getAmount() + (float)Math.sqrt(rand.nextDouble() * AmelioratedSharpness.TRANSCENDED_DAMAGE_MULTIPLIER.get() * EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED) * (base.hasEffect(MobEffects.DAMAGE_BOOST) ? 2 : 1)));
				}
			}
			level = enchantments.getInt(UE.ADV_SMITE);
			if(target.getMobType() == MobType.UNDEAD && level > 0 && MiscUtil.isTranscendent(base, stack, UE.ADV_SMITE))
			{
				MiscUtil.doNewDamageInstance(target, DamageSource.MAGIC.bypassMagic().bypassInvul(), (float)(Math.pow(target.getHealth(), AmelioratedSmite.TRANSCENDED_DAMAGE_EXPONENT.get())));
			}
			level = enchantments.getInt(UE.ADV_BANE_OF_ARTHROPODS);
			if(target.getMobType() == MobType.ARTHROPOD && level > 0 && MiscUtil.isTranscendent(base, stack, UE.ADV_BANE_OF_ARTHROPODS))
			{
				MiscUtil.doNewDamageInstance(target, DamageSource.MAGIC.bypassMagic().bypassInvul(), (float)(Math.pow(target.getHealth(), AmelioratedBaneOfArthropod.TRANSCENDED_DAMAGE_EXPONENT.get())));
			}
			level = UE.DEATHS_UPGRADE.getPoints(stack);
			if(level > 0)
			{
				MiscUtil.doNewDamageInstance(target, DamageSource.MAGIC, (float) (target.getHealth()*Math.log10(1+level)/100));
			}
			level = enchantments.getInt(UE.ENDEST_REAP);
			if(level > 0)
			{
				if(rand.nextDouble() > Math.pow(0.9d, level)) 
				{
					target.addEffect(new MobEffectInstance(UE.THROMBOSIS, 100*level, level-1));
				}
				MiscUtil.doNewDamageInstance(target, DamageSource.MAGIC.bypassMagic(), (EndestReap.BONUS_DAMAGE_LEVEL.getFloat(level) + (float)Math.log(1+ EndestReap.REAP_MULTIPLIER.getFloat(level * base.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getInt(EndestReap.REAP_STORAGE)))));
			}
			level = UE.PROTECTION_UPGRADE.getCombinedPoints(target);
			if(level > 0) 
			{
				float val = (float) Math.log10(1+level);
				event.setAmount(event.getAmount() > val ? event.getAmount() - val : event.getAmount() * (1-(val/100)));
			}
			level = enchantments.getInt(UE.COMBO_STAR);
			if(level > 0) 
			{
				CompoundTag nbt = MiscUtil.getPersistentData(event.getEntity());
				int combo = nbt.getInt(ComboStar.COMBO_NAME);
				double crit = Math.log10(10+Math.sqrt(level*combo));
				event.setAmount(event.getAmount() * (float)Math.pow(crit, -0.5D));
			}
			if(target.hasEffect(UE.RESILIENCE)) 
			{
				MobEffectInstance mei = target.getEffect(UE.RESILIENCE);
				event.setAmount((float) (event.getAmount() * Math.pow(0.99, mei.getAmplifier()+1)));
				((PotionMixin)mei).setPotionAmplifier(Math.max(mei.getAmplifier()-1, 0));
			}
		}
		if(event.getSource() == DamageSource.FLY_INTO_WALL)
		{
			Object2IntMap.Entry<ItemStack> result = MiscUtil.getEquipment(target, UE.ICARUS_AEGIS, CurioSlot.BACK);
			if(result.getIntValue() > 0)
			{
				int feathers = StackUtils.getInt(result.getKey(), IcarusAegis.FEATHER_TAG, 0);
				int consume = (int)Math.ceil((double)IcarusAegis.BASE_CONSUMPTION.get() / result.getIntValue());
				if(feathers >= consume)
				{
					feathers -= consume;
					StackUtils.setInt(result.getKey(), IcarusAegis.FEATHER_TAG, feathers);
					event.setCanceled(true);
					return;
				}
			}
		} else if((event.getSource() == DamageSource.IN_FIRE || event.getSource() == DamageSource.ON_FIRE || event.getSource() == DamageSource.LAVA || event.getSource() == DamageSource.HOT_FLOOR) && (MiscUtil.getEnchantmentLevel(UE.CLIMATE_TRANQUILITY, target.getMainHandItem()) > 0 && MiscUtil.isTranscendent(target, target.getMainHandItem(), UE.CLIMATE_TRANQUILITY))) {
			event.setAmount(event.getAmount() * ClimateTranquility.TRANSCENDED_BURN_DAMAGE.getFloat());
		}
		if(event.getAmount() >= event.getEntity().getHealth())
		{
			DamageSource source = event.getSource();
			
			if(!source.isMagic())
			{
				aresBlessingLabel: 
				{
					ItemStack stack = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
					int level = MiscUtil.getEnchantmentLevel(UE.ARES_BLESSING, stack);
					if(level > 0 && stack.isDamageableItem())
					{
						if(!MiscUtil.isTranscendent(target, stack, UE.ARES_BLESSING))
						{
							if(source == DamageSource.FALL) break aresBlessingLabel;
						} 
						else 
						{
							target.addEffect(new MobEffectInstance(UE.RESILIENCE, 100*level, 1));
						}
						float damage = event.getAmount();
						stack.hurtAndBreak((int)(damage * AresBlessing.BASE_DAMAGE.get() / MathCache.LOG10.get(level+1)), event.getEntity(), MiscUtil.get(EquipmentSlot.CHEST));
						event.setCanceled(true);
						return;
					}
				}
			}
			Object2IntMap.Entry<ItemStack> slot = MiscUtil.getEquipment(event.getEntity(), UE.PHOENIX_BLESSING, CurioSlot.HAND);
			if(slot.getIntValue() > 0)
			{
				LivingEntity living = event.getEntity();
				living.heal(living.getMaxHealth());
				living.removeAllEffects();
				if(living instanceof Player)
				{
					((Player)living).getFoodData().eat(Short.MAX_VALUE, 1F);
				}
				living.getPersistentData().putLong(DeathsOdium.CURSE_TIMER, living.getCommandSenderWorld().getGameTime() + DeathsOdium.DELAY.get());
                living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1));
                living.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                living.level.broadcastEntityEvent(living, (byte)35);
                slot.getKey().shrink(1);
				event.setCanceled(true);
	            for(LivingEntity entry : living.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, new AABB(living.blockPosition()).inflate(PhoenixBlessing.RANGE.getAsDouble(slot.getIntValue()))))
	            {
	            	if(entry == living) continue;
	            	entry.setSecondsOnFire(600000);
	            }
			}
		}
		if(entity instanceof Player)
		{
			CompoundTag compound = entity.getPersistentData();
			if(compound.getLong(DeathsOdium.CURSE_TIMER) >= entity.level.getGameTime())
			{
				compound.putFloat(DeathsOdium.CURSE_DAMAGE, compound.getFloat(DeathsOdium.CURSE_DAMAGE)+event.getAmount());
			}
		}
	}
	
	@SubscribeEvent
	public void onCrit(CriticalHitEvent event)
	{
		if(event.getEntity() == null) return;
		ItemStack stack = event.getEntity().getMainHandItem();
		Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(stack);
		int level = ench.getInt(UE.COMBO_STAR);
		if(level > 0)
		{
			CompoundTag nbt = MiscUtil.getPersistentData(event.getEntity());
			if(event.isVanillaCritical()) nbt.putInt(ComboStar.COMBO_NAME, Math.min(nbt.getInt(ComboStar.COMBO_NAME)+1, Math.max(100, event.getEntity().experienceLevel)));
			else {
				int num = nbt.getInt(ComboStar.COMBO_NAME);
				stack.setDamageValue(stack.getDamageValue() - num);
				nbt.remove(ComboStar.COMBO_NAME);
			}
			int combo = nbt.getInt(ComboStar.COMBO_NAME);
			double crit = Math.log10(10+Math.sqrt(level*combo));
			event.setDamageModifier((float)(event.getDamageModifier() * crit));
		}
		level = ench.getInt(UE.FOCUS_IMPACT);
		if(level > 0 && event.getEntity().getDeltaMovement().length() == 0.0d) 
		{
			event.setResult(Result.ALLOW);
		}
	}
	
	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event)
	{
		Object2IntMap.Entry<ItemStack> result = MiscUtil.getEquipment(event.getEntity(), UE.ICARUS_AEGIS, CurioSlot.BACK);
		ItemStack stack = result.getKey();
		int level = result.getIntValue();
		if(level > 0 && stack.getTag().getBoolean(IcarusAegis.FLYING_TAG) && event.getDistance() > 3F)
		{
			int feathers = StackUtils.getInt(stack, IcarusAegis.FEATHER_TAG, 0);
			int consume = (int)(IcarusAegis.BASE_CONSUMPTION.get() / MathCache.LOG.get(2 + level));
			if(feathers >= consume)
			{
				feathers -= consume;
				StackUtils.setInt(stack, IcarusAegis.FEATHER_TAG, feathers);
				event.setCanceled(true);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityKilled(LivingDeathEvent event)
	{
		Entity entity = event.getSource().getEntity();
		LivingEntity deadEntity = event.getEntity();
		if(entity instanceof LivingEntity base)
		{
			ItemStack stack = event.getSource().getDirectEntity() instanceof ThrownTrident trident ? ((ArrowMixin)trident).getArrowItem() : base.getMainHandItem();
			int level = MiscUtil.getEnchantmentLevel(UE.WARRIORS_GRACE, base.getMainHandItem());
			if(level > 0)
			{
				int amount = Math.min(stack.getDamageValue(), Mth.ceil(Math.sqrt(event.getEntity().getMaxHealth() * level) * WarriorsGrace.DURABILITY_GAIN.get()));
				if(amount > 0)
				{
					stack.hurtAndBreak(-amount, base, MiscUtil.get(EquipmentSlot.MAINHAND));
				}
			}
			Entity killed = event.getEntity();
			if(killed != null && base instanceof Player)
			{
				int amount = EndestReap.isValid(killed);
				if(amount > 0)
				{
					level = MiscUtil.getEnchantmentLevel(UE.ENDEST_REAP, base.getMainHandItem());
					if(level > 0)
					{
						CompoundTag nbt = MiscUtil.getPersistentData(entity);
						nbt.putInt(EndestReap.REAP_STORAGE, Math.min(nbt.getInt(EndestReap.REAP_STORAGE)+amount, MiscUtil.isTranscendent(base, base.getMainHandItem(), UE.ENDEST_REAP) ? Integer.MAX_VALUE : ((Player)base).experienceLevel));
						StackUtils.setInt(base.getMainHandItem(), EndestReap.REAP_STORAGE, nbt.getInt(EndestReap.REAP_STORAGE));
					}
				}
			}
			if(!(deadEntity instanceof Player) && rand.nextFloat() < 0.025f) {
				Set<Enchantment> ench = new ObjectOpenHashSet<>();
				for (ItemStack item : deadEntity.getAllSlots()) {
					ench.addAll(MiscUtil.getEnchantments(item).keySet());
					if(ench.size() >= 10) {
						MiscUtil.spawnDrops(deadEntity, UE.GRIMOIRE, rand.nextInt(2));
						break;
					}
				}
			}
		}
		CompoundTag nbt = MiscUtil.getPersistentData(event.getEntity());
		if(!nbt.getBoolean(DeathsOdium.CURSE_DISABLED))
		{
			if(nbt.getBoolean(DeathsOdium.CURSE_RESET))
			{
				nbt.remove(DeathsOdium.CURSE_RESET);
				nbt.remove(DeathsOdium.CURSE_STORAGE);
				nbt.putBoolean(DeathsOdium.CURSE_DISABLED, true);
				for(EquipmentSlot slot : EquipmentSlot.values())
				{
					ItemStack stack = event.getEntity().getItemBySlot(slot);
					if(MiscUtil.getEnchantmentLevel(UE.DEATHS_ODIUM, stack) > 0)
					{
						stack.getTag().remove(DeathsOdium.CURSE_STORAGE);
					}
				}
				deadEntity.getAttribute(Attributes.MAX_HEALTH).removeModifier(DeathsOdium.REMOVE_UUID);
			}
			else
			{
				LivingEntity ent = event.getEntity();
				if(DeathsOdium.applyStackBonus(ent))
				{
					nbt.putInt(DeathsOdium.CURSE_STORAGE, (nbt.getInt(DeathsOdium.CURSE_STORAGE)+1));
				}
			}
		}
		if(deadEntity instanceof Witch && rand.nextInt(100) < 2)
		{
			List<CombatEntry> entries = ((CombatTrackerMixin)deadEntity.getCombatTracker()).getCombatEntries();
			boolean valid = true;
			for(int i = 0,m=entries.size();i<m;i++)
			{
				CombatEntry entry = entries.get(i);
				if(!entry.getSource().isMagic()){
					valid = false;
					break;
				}
			}
			if(valid) MiscUtil.spawnDrops(deadEntity, UE.ALCHEMISTS_GRACE, Mth.nextInt(rand, 4, 10));
		}
		if(!(deadEntity instanceof Player) && rand.nextInt(100) < 1)
		{
			if(deadEntity.hasEffect(MobEffects.ABSORPTION))
			{
				MiscUtil.spawnDrops(deadEntity, UE.PHOENIX_BLESSING, 1);
			}
			if(entity instanceof Player player)
			{
				float damageDealt = 0F;
				float maxHealth = player.getMaxHealth();
				List<CombatEntry> entries = ((CombatTrackerMixin)player.getCombatTracker()).getCombatEntries();
				for(int i = 0,m=entries.size();i<m;i++)
				{
					CombatEntry entry = entries.get(i);
					if(entry.isCombatRelated() && entry.getSource().getEntity() == deadEntity)
					{
						damageDealt += entry.getDamage();
						if(damageDealt / maxHealth >= 0.75)
						{
							MiscUtil.spawnDrops(deadEntity, UE.ARES_BLESSING, 1);
							break;
						}
					}
				}
				if(deadEntity.hasEffect(MobEffects.LEVITATION) || player.hasEffect(MobEffects.LEVITATION))
				{
					MiscUtil.spawnDrops(deadEntity, UE.CLOUD_WALKER, Mth.nextInt(rand, 1, 2));
				}
			}
		}
		if(deadEntity instanceof Shulker && entity instanceof Shulker && rand.nextInt(100) < 3)
		{
			MiscUtil.spawnDrops(deadEntity, UE.ENDEST_REAP, Mth.nextInt(rand, 2, 4));
		}
	}
	
	@SubscribeEvent
	public void onRespawn(PlayerEvent.Clone event)
	{
		CompoundTag nbt = MiscUtil.getPersistentData(event.getEntity());
		if(nbt.getBoolean(DeathsOdium.CURSE_DISABLED)) return;
		float f = nbt.getFloat(DeathsOdium.CURSE_STORAGE);
		if(f != 0F)
		{
			event.getEntity().getAttribute(Attributes.MAX_HEALTH).addTransientModifier(new AttributeModifier(DeathsOdium.REMOVE_UUID, "odiums_curse", Math.pow(0.95,f)-1, Operation.MULTIPLY_TOTAL));
		}
	}
	
	@SubscribeEvent
	public void onEaten(LivingEntityUseItemEvent.Finish event)
	{
		if(event.getItem().getItem() == Items.COOKIE && MiscUtil.getEnchantmentLevel(UE.DEATHS_ODIUM, event.getItem()) > 0)
		{
			MiscUtil.getPersistentData(event.getEntity()).putBoolean(DeathsOdium.CURSE_RESET, true);
			
			if(!event.getEntity().hurt(UE.COOKIE, Integer.MAX_VALUE/2)) 
			{
				event.getEntity().kill();
			}
		}
	}
	
	@SubscribeEvent
	public void onItemDamaged(ItemDurabilityChangeEvent event)
	{
		if(event.damageDone <= 0) return;
		int points = UE.GRIMOIRES_UPGRADE.getPoints(event.item);
		if(points > 0 && event.entity.getRandom().nextDouble() < (1-Math.pow(0.9, Math.pow(points, 0.125))))
		{
			event.item.hurt(-event.damageDone, event.entity.getRandom(), null);
		}
		int level = MiscUtil.getEnchantmentLevel(UE.GRIMOIRE, event.item);
		if(level > 0 && MiscUtil.isTranscendent(event.entity, event.item, UE.GRIMOIRE) && event.entity.getRandom().nextFloat() >= 100F/(100F+MiscUtil.getItemLevel(event.item))) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onXPDrop(LivingExperienceDropEvent event)
	{
		Player player = event.getAttackingPlayer();
		if(player == null) return;
		int level = MiscUtil.getCombinedEnchantmentLevel(UE.SAGES_BLESSING, player);
		if(level > 0)
		{
			double stacks = 1;
			for(EquipmentSlot slot : MiscUtil.getSlotsFor(UE.SAGES_BLESSING))
			{
				stacks += StackUtils.getInt(player.getItemBySlot(slot), SagesBlessing.SAGES_XP, 0);
			}
			ItemStack itemStack = event.getAttackingPlayer().getItemBySlot(EquipmentSlot.MAINHAND);
			double val = Math.pow(player.level.random.nextInt(MiscUtil.getEnchantmentLevel(Enchantments.MOB_LOOTING, itemStack)+1)+1, 2);
			double form = Math.pow(1+SagesBlessing.XP_BOOST.get(level*stacks*val), 0.1);
			event.setDroppedExperience((int) (event.getDroppedExperience() * (MiscUtil.isTranscendent(player, itemStack, UE.SAGES_BLESSING) ? Math.pow(form, SagesBlessing.TRANSCENDED_BOOST.get()) : form)));
		}
	}
	
	@SubscribeEvent
	public void onLootingLevel(LootingLevelEvent event)
	{
		if(event.getDamageSource() == null) return;
		Entity entity = event.getDamageSource().getEntity();
		if(entity instanceof LivingEntity && event.getEntity() instanceof AbstractSkeleton)
		{
			int level = MiscUtil.getEnchantmentLevel(UE.BONE_CRUSH, ((LivingEntity)entity).getMainHandItem());
			if(level > 0 && BoneCrusher.isNotArmored((AbstractSkeleton)event.getEntity()))
			{
				event.setLootingLevel((event.getLootingLevel() + 1) + level);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDrops(LivingDropsEvent event)
	{
		Entity entity = event.getSource().getEntity();
		if(entity instanceof Player)
		{
			
			Player base = (Player)entity;
			ItemStack stack = base.getMainHandItem();
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
			int level = enchantments.getInt(UE.FAST_FOOD);
			if(event.getEntity() instanceof Animal && level > 0)
			{
				int stored = StackUtils.getInt(stack, EnderMending.ENDER_TAG, 0);
				int looting = base.level.random.nextInt(1+MiscUtil.getEnchantmentLevel(Enchantments.MOB_LOOTING, base.getMainHandItem()));
				int burning = event.getEntity().isOnFire() ? 2 : 1;
				int num = FastFood.NURISHMENT.get(level+looting) * burning;
				FoodData food = base.getFoodData();
				if(food.getFoodLevel() >= 20 && MiscUtil.isTranscendent(entity, base.getMainHandItem(), UE.FAST_FOOD))
				{
					StackUtils.setInt(stack, FastFood.FASTFOOD, stored+num);
				}
				else 
				{
					food.eat(num, FastFood.SATURATION.getFloat(level+looting) * burning);
					StackUtils.setInt(stack, FastFood.FASTFOOD, stored);
				}
				event.setCanceled(true);
			}
			
			level = enchantments.getInt(UE.BONE_CRUSH);
			if(level > 0 && event.getEntity() instanceof AbstractSkeleton && BoneCrusher.isNotArmored((AbstractSkeleton)event.getEntity()))
			{
				for(ItemEntity drop : event.getDrops())
				{
					if(!(drop.getItem().getItem() instanceof ArmorItem) && rand.nextDouble() < BoneCrusher.TRANSCENDED_CHANCE.get())
					{
						event.getEntity().spawnAtLocation(drop.getItem().copy());
					}
				}
			}
		}
	}
	
	
	@SubscribeEvent
	public void onArrowHit(ProjectileImpactEvent event)
	{
		HitResult result = event.getRayTraceResult();
		if(!(result instanceof EntityHitResult hit) || !(event.getEntity() instanceof AbstractArrow arrow))
		{
			return;
		}
		Entity shooter = arrow.getOwner();
		AlchemistsGrace.applyToEntity(shooter, false, 1.5F);
		if(shooter instanceof Player player)
		{
			Object2IntMap.Entry<EquipmentSlot> slot = MiscUtil.getEnchantedItem(UE.ENDERMARKSMEN, player);
			if(slot.getIntValue() > 0 && !arrow.getPersistentData().contains(EnderMarksmen.DID_ALREADY_HIT))
			{
				arrow.getPersistentData().putBoolean(EnderMarksmen.DID_ALREADY_HIT, true);
				int level = slot.getIntValue();
				ItemStack stack = player.getItemBySlot(slot.getKey());
				arrow.pickup = Pickup.DISALLOWED;
				player.addItem(StackUtils.getArrowStack(arrow));
				int needed = Math.min(Mth.floor(MathCache.LOG_ADD.get(level)*EnderMarksmen.EXTRA_DURABILITY.get()), stack.getDamageValue());
				if(needed > 0)
				{
					stack.hurtAndBreak(-needed, player, MiscUtil.get(slot.getKey()));
				}
				Entity entity = hit.getEntity();
				if(entity instanceof EnderMan)
				{
					ENDER_MAN_HIT.set(entity.getUUID());
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEndermenTeleport(EntityTeleportEvent event)
	{
		UUID id = ENDER_MAN_HIT.get();
		if(event.getEntity().getUUID().equals(id))
		{
			ENDER_MAN_HIT.set(null);
			event.setCanceled(true);
			return;
		}
		Entity entity = event.getEntity();
		if(entity instanceof LivingEntity living && living.hasEffect(UE.INTERCEPTION))
		{
			event.setCanceled(true);
			return;
		}
	}
	
	@SubscribeEvent
	public void onEquippementSwapped(LivingEquipmentChangeEvent event)
	{
		LivingEntity entity = event.getEntity();
		AttributeMap attribute = entity.getAttributes();
		Multimap<Attribute, AttributeModifier> mods = createModifiersFromStack(event.getFrom(), entity, event.getSlot(), true);
		if(!mods.isEmpty())
		{
			attribute.removeAttributeModifiers(mods);
		}		
		mods = createModifiersFromStack(event.getTo(), entity, event.getSlot(), false);
		if(!mods.isEmpty())
		{
			attribute.addTransientAttributeModifiers(mods);
		}		
	}
	
	//TODO when Speiger isn't to lazy, implement a more optimized deletion function...
	private Multimap<Attribute, AttributeModifier> createModifiersFromStack(ItemStack stack, LivingEntity living, EquipmentSlot slot, boolean remove)
	{
		Multimap<Attribute, AttributeModifier> mods = HashMultimap.create();
		//Optimization. After 3 Enchantment's its sure that on average you have more then 1 full iteration. So now we fully iterate once over it since hash-code would be a faster check.
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		int level = enchantments.getInt(UE.VITAE);
		if(level > 0 && MiscUtil.getSlotsFor(UE.VITAE).contains(slot))
		{
			mods.put(Attributes.MAX_HEALTH, new AttributeModifier(Vitae.HEALTH_MOD.getId(slot), "Vitae", Vitae.ALT_FORMULA.get() ? Vitae.BASE_BOOST.get()+Vitae.SCALE_BOOST.get(level) : Math.log(1+ level*(Vitae.BASE_BOOST.get()+Vitae.SCALE_BOOST.get(MiscUtil.getPlayerLevel(living, 200)))), Operation.ADDITION));
		}
		level = enchantments.getInt(UE.SWIFT);
		if(level > 0)
		{
			int totalLevel = MiscUtil.getCombinedEnchantmentLevel(UE.SWIFT, living);
			mods.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(Swift.SPEED_MOD, "Swift Boost", Math.sqrt(totalLevel/100), Operation.MULTIPLY_TOTAL));
		}
		level = enchantments.getInt(UE.RANGE);
		if(level > 0 && MiscUtil.getSlotsFor(UE.RANGE).contains(slot))
		{
			float num = Range.RANGE.getAsFloat(level);
			mods.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(Range.RANGE_MOD, "Reach Boost", num, Operation.ADDITION));
			mods.put(ForgeMod.ATTACK_RANGE.get(), new AttributeModifier(Range.RANGE_MOD, "Combat Range Boost", num*Range.COMBAT.get(), Operation.ADDITION));
		}
		level = enchantments.getInt(UE.DEATHS_ODIUM);
		if(level > 0 && MiscUtil.getSlotsFor(UE.DEATHS_ODIUM).contains(slot))
		{
			float value = StackUtils.getFloat(stack, DeathsOdium.CURSE_STORAGE, 0);
			if(!MiscUtil.getPersistentData(living).getBoolean(DeathsOdium.CURSE_DISABLED))
			{
				mods.put(Attributes.MAX_HEALTH, new AttributeModifier(DeathsOdium.GENERAL_MOD.getId(slot), "Death Odiums Restore", value/100f, Operation.MULTIPLY_TOTAL));
			}
		}
		level = UE.AMELIORATED_UPGRADE.isValid(stack) ? UE.AMELIORATED_UPGRADE.getPoints(stack) : 0;
		if(level > 0 && UE.AMELIORATED_UPGRADE.isValidSlot(slot))
		{
			mods.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(AmelioratedUpgrade.DAMAGE_ID, "Ameliorated Upgrade", Math.pow(level, 0.125), Operation.ADDITION));
		}
		if(MiscUtil.getEnchantedItem(UE.COMBO_STAR, living).getIntValue() > 0)
		{
			int counter = MiscUtil.getPersistentData(living).getInt(ComboStar.COMBO_NAME);
			mods.put(Attributes.ATTACK_SPEED, new AttributeModifier(ComboStar.SPEED_EFFECT, "Combo Star Speed", MathCache.LOG10.get((int)ComboStar.COUNTER_MULTIPLIER.get(10+counter)), Operation.MULTIPLY_BASE));
		}
		return mods;
	}
}
