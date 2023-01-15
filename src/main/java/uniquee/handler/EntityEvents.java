package uniquee.handler;

import java.util.List;
import java.util.Map.Entry;
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
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
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
import net.minecraft.world.level.block.entity.BannerPattern;
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
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
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
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.UEBase;
import uniquebase.api.events.ItemDurabilityChangeEvent;
import uniquebase.handler.MathCache;
import uniquebase.networking.EntityPacket;
import uniquebase.utils.EnchantmentContainer;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquebase.utils.events.EndermenLookEvent;
import uniquebase.utils.events.PiglinWearableCheckEvent;
import uniquebase.utils.mixin.common.InteractionManagerMixin;
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
import uniquee.enchantments.simple.EnderEyes;
import uniquee.enchantments.simple.FocusedImpact;
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
	static final ThreadLocal<UUID> ENDER_MEN_TELEPORT = new ThreadLocal<>();
	public static final ThreadLocal<Boolean> BREAKING = ThreadLocal.withInitial(() -> false);
	public RandomSource rand = RandomSource.create();
	
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinLevelEvent event)
	{
		Entity entity = event.getEntity();
		if(entity instanceof ItemEntity)
		{
			if(MiscUtil.getEnchantmentLevel(UE.GRIMOIRE, ((ItemEntity)entity).getItem()) > 0)
			{
				entity.setInvulnerable(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onEndermenLookEvent(EndermenLookEvent event)
	{
		if(MiscUtil.getEnchantmentLevel(UE.ENDER_EYES, event.getEntity().getItemBySlot(EquipmentSlot.HEAD)) > 0)
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onAnvilRepair(AnvilUpdateEvent event)
	{
		if(MiscUtil.getEnchantmentLevel(UE.GRIMOIRE, event.getLeft()) > 0)
		{
			event.setCost(Integer.MAX_VALUE);
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
				((PotionMixin)instance).setPotionDuration((int)(instance.getDuration() * (1F/MathCache.LOG10.getFloat(10+points))));
			}
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
			if(player.getHealth() < player.getMaxHealth())
			{
				int level = container.getEnchantment(UE.NATURES_GRACE, EquipmentSlot.CHEST);
				if(level > 0 && player.level.getGameTime() % Math.max((int)(NaturesGrace.DELAY.get() / MathCache.LOG101.get(level)), 1) == 0)
				{
					if(player.getCombatTracker().getKiller() == null)
					{
						int value = StackUtils.hasBlockCount(player.level, player.blockPosition(), 24, NaturesGrace.FLOWERS);
						if(value >= 4) player.heal((float)Math.log(7.39D + Math.pow((float)Math.sqrt(value), MathCache.LOG.get(level+1))));
					}
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
			}
			if(player.level.getGameTime() % 1200 == 0)
			{
				EquipmentSlot[] slots = MiscUtil.getEquipmentSlotsFor(UE.GRIMOIRE);
				for(int i = 0;i<slots.length;i++)
				{
					int level = container.getEnchantment(UE.GRIMOIRE, slots[i]);
					if(level > 0)
					{
						if(Grimoire.applyGrimore(player.getItemBySlot(slots[i]), level, player))
						{
							player.level.playSound(null, player.blockPosition(), UE.GRIMOIRE_SOUND, SoundSource.AMBIENT, 1F, 1F);
						}
					}
					else
					{
						ItemStack stack = player.getItemBySlot(slots[i]);
						if(stack.hasTag() && stack.getTag().contains(Grimoire.GRIMOIRE_STORAGE))
						{
							CompoundTag tag = stack.getTag();
							tag.put("Enchantments", tag.get(Grimoire.GRIMOIRE_STORAGE));
							tag.remove(Grimoire.GRIMOIRE_STORAGE);
							String id = ForgeRegistries.ENCHANTMENTS.getKey(UE.GRIMOIRE).toString();
							ListTag list = tag.getList("Enchantments", 10);
							for(int j = 0,m=list.size();j<m;j++) {
								CompoundTag ench = list.getCompound(j);
								if(id.equals(ench.getString("id"))) {
									list.remove(j--);
									break;
								}
							}
						}
					}
				}
			}
			if(player.level.getGameTime() % 30 == 0)
			{
				ClimateTranquility.onClimate(player, container);
			}
			if(player.level.getGameTime() % 10 == 0)
			{
				int level = container.getEnchantment(UE.ICARUS_AEGIS, EquipmentSlot.CHEST);
				if(level > 0)
				{
					player.getItemBySlot(EquipmentSlot.CHEST).getTag().putBoolean(IcarusAegis.FLYING_TAG, player.isFallFlying());
				}
			}
			if(player.level.getGameTime() % 40 == 0)
			{
				int level = container.getCombinedEnchantment(UE.PESTILENCES_ODIUM);
				if(level > 0)
				{
					List<LivingEntity> living = player.level.getEntitiesOfClass(LivingEntity.class, new AABB(player.blockPosition()).inflate(PestilencesOdium.RADIUS.get()));
					for(int i = 0,m=living.size();i<m;i++)
					{
						LivingEntity entity = living.get(i);
						if(entity.getActiveEffects().isEmpty() && entity != player)
						{
							entity.addEffect(new MobEffectInstance(UE.PESTILENCES_ODIUM_POTION, 200, level));
						}
					}
				}
			}
			if(player.level.getGameTime() % 20 == 0)
			{
				Object2IntMap.Entry<EquipmentSlot> level = container.getEnchantedItem(UE.SAGES_BLESSING);
				if(level.getIntValue() > 0)
				{
					player.causeFoodExhaustion(0.01F * level.getIntValue());
				}
				int points = UE.PHOENIX_UPGRADE.getCombinedPoints(player);
				if(points > 0)
				{
					player.heal((float)(Math.sqrt(points))*0.01F);
				}
			}
			CompoundTag data = event.player.getPersistentData();
			if(data.contains(DeathsOdium.CURSE_DAMAGE) && data.getLong(DeathsOdium.CURSE_TIMER) < event.player.level.getGameTime())
			{
				int total = (int) Math.pow(data.getFloat(DeathsOdium.CURSE_DAMAGE) * rand.nextDouble(), 0.25d);
				if(total > 0)
				{
					AttributeInstance instance = event.player.getAttribute(Attributes.MAX_HEALTH);
					AttributeModifier mod = instance.getModifier(DeathsOdium.REMOVE_UUID);
					if(mod != null)
					{
						double newValue = Math.max(0D, mod.getAmount() - total);
						instance.removeModifier(mod);
						if(newValue > 0)
						{
							instance.addTransientModifier(new AttributeModifier(DeathsOdium.REMOVE_UUID, "odiums_curse", newValue, Operation.ADDITION));
						}
					}
				}	
				data.remove(DeathsOdium.CURSE_DAMAGE);
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
				player.setDeltaMovement(vec.x, player.getAbilities().flying ? 0.15D : 0D, vec.z);
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
			player.setDeltaMovement(player.getDeltaMovement().add(0, Swift.SPEED_BONUS.getAsDouble(level) * 3D, 0));
		}
		Boolean cache = null;
		//Reflection is slower then direct call. But Twice the Iteration & Double IsEmpty Check is slower then Reflection.
		EquipmentSlot[] slots = MiscUtil.getEquipmentSlotsFor(UE.ECOLOGICAL);
		for(int i = 0;i<slots.length;i++)
		{
			ItemStack equipStack = player.getItemBySlot(slots[i]); 
			level = container.getEnchantment(UE.ECOLOGICAL, slots[i]);
			if(level > 0 && equipStack.isDamaged() && player.level.getGameTime() % Math.max(1, (int)(Ecological.SPEED.get() / Math.log10(10.0D + (player.experienceLevel*level) / Ecological.SPEED_SCALE.get()))) == 0)
			{
				if((cache == null ? cache = StackUtils.hasBlockCount(player.level, player.blockPosition(), 1, Ecological.STATES) > 0 : cache.booleanValue()))
				{
					equipStack.hurtAndBreak(-1, player, MiscUtil.get(slots[i]));
				}
			}
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
		if(level > 0 && isMining(player))
		{
			CompoundTag nbt = player.getPersistentData();
			long worldTime = player.level.getGameTime();
			long time = nbt.getLong(Momentum.LAST_MINE);
			double count = nbt.getDouble(Momentum.COUNT);
			if(worldTime - time > Momentum.MAX_DELAY.get() || worldTime < time)
			{
				count = 0;
				nbt.putDouble(Momentum.COUNT, count);
			}
			double flat = Math.log(1 + Momentum.SPEED.get(count));
			double percent = Math.log10(10+(Momentum.SPEED_MULTIPLIER.get(count))/100);
			event.setNewSpeed((float)((event.getNewSpeed() + flat) * percent));
			nbt.putLong(Momentum.LAST_MINE, worldTime);
		}
		level = ench.getInt(UE.RANGE);
		if(level > 0)
		{
			double value = MiscUtil.getBaseAttribute(player, ForgeMod.REACH_DISTANCE.get());
			if(value * value < event.getPosition().orElse(BlockPos.ZERO).distToCenterSqr(player.position()))
			{
				event.setNewSpeed(event.getNewSpeed() * Range.REDUCTION.getLogDevided(level+1));
			}
		}
	}
	
	public boolean isMining(Player player)
	{
		return !(player instanceof ServerPlayer) || ((InteractionManagerMixin)((ServerPlayer)player).gameMode).isMiningBlock();
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
			level += (event.getLevel().getRandom().nextInt(enchs.getInt(Enchantments.BLOCK_FORTUNE)+1)+1);
			event.setExpToDrop((int)(event.getExpToDrop() + event.getExpToDrop() * SagesBlessing.XP_BOOST.get(level)));
		}
		level = enchs.getInt(UE.MOMENTUM);
		if(level > 0)
		{
			double cap = Momentum.CAP.get() * Math.pow(Momentum.CAP_MULTIPLIER.get(level), 2);
			double extra = Math.ceil(Math.pow(event.getState().getDestroySpeed(event.getLevel(), event.getPos())+1, 1+(level/100)));
			CompoundTag nbt = event.getPlayer().getPersistentData();
			nbt.putDouble(Momentum.COUNT, Math.min(nbt.getDouble(Momentum.COUNT) + extra, cap));
			if(!player.level.isClientSide) UEBase.NETWORKING.sendToPlayer(new EntityPacket(player.getId(), nbt), player);
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
	        stack.shrink(1);
		}
	}
	
//	@SubscribeEvent
//	public void onItemUseTick(LivingEntityUseItemEvent.Tick event)
//	{
//		if(event.getDuration() <= 10) return;
//		if(MiscUtil.getEnchantedItem(UE.COMBO_STAR, event.getEntity()).getIntValue() > 0)
//		{
//			int counter = MiscUtil.getPersistentData(event.getEntity()).getInt(ComboStar.COMBO_NAME);
//			event.setDuration(Math.max(10, (int)(event.getDuration() / MathCache.LOG10.get((int) (10+ComboStar.COUNTER_MULTIPLIER.get(counter))))));
//		}
//	}
	
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
	public void onEntityHit(LivingAttackEvent event)
	{
		AlchemistsGrace.applyToEntity(event.getSource().getEntity(), false, 1.5F);
	}
	
	@SubscribeEvent
	public void onEntityAttack(LivingHurtEvent event)
	{
		LivingEntity target = event.getEntity();
		Entity entity = event.getSource().getEntity();
		
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(base.getMainHandItem());
			int level = enchantments.getInt(UE.SWIFT_BLADE);
			if(level > 0)
			{
				AttributeInstance attr = base.getAttribute(Attributes.ATTACK_SPEED);
				if(attr != null)
				{
					event.setAmount(event.getAmount() * (float)Math.log(1.719d + Math.pow((level+0.1)/2, Math.sqrt(attr.getValue()/1.2))));
				}
			}
			level = enchantments.getInt(UE.FOCUS_IMPACT);
			if(level > 0)
			{
				AttributeInstance attr = base.getAttribute(Attributes.ATTACK_SPEED);
				if(attr != null)
				{
					double val = Math.max(attr.getValue(), 0.01);
					event.setAmount(event.getAmount() * (float)Math.log(1.719d + Math.pow((level+1.1D)*0.5D, Math.sqrt(1.2D/val))));
				}
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
				level = MiscUtil.getEnchantmentLevel(UE.BONE_CRUSH, base.getMainHandItem());
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
			level = enchantments.getInt(UE.PERPETUAL_STRIKE);
			if(level > 0)
			{
				ItemStack held = base.getMainHandItem();
				int count = StackUtils.getInt(held, PerpetualStrike.HIT_COUNT, 0);
				int lastEntity = StackUtils.getInt(held, PerpetualStrike.HIT_ID, 0);
				int mercy = StackUtils.getInt(held, "mercy", PerpetualStrike.TRANSCENDED_MERCY.get());
				int mercyReset = PerpetualStrike.TRANSCENDED_MERCY.get();
				if(MiscUtil.isTranscendent(base, held, UE.PERPETUAL_STRIKE))
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
					StackUtils.setInt(held, "mercy", mercy);
				}
				else if(lastEntity != target.getId())
				{
					count = 0;
				}
				StackUtils.setInt(held, PerpetualStrike.HIT_COUNT, count+1);
				if(rand.nextInt(100) <= count) {
					target.addEffect(new MobEffectInstance(UE.THROMBOSIS, 100*level, level-1));
				}
				double damage = target.getHealth() * (Math.pow((count * PerpetualStrike.PER_HIT.get() * PerpetualStrike.PER_HIT_LEVEL.get())+1, 0.25)/(100*MiscUtil.getAttackSpeed(base, 1D)));
				double multiplier = PerpetualStrike.SCALING_STATE.get() ? 1 + Math.pow(count * PerpetualStrike.MULTIPLIER.get(), 2)/20 : Math.log10(10+damage*count*PerpetualStrike.MULTIPLIER.get());
				event.setAmount((float)((event.getAmount()+damage)*multiplier));
				StackUtils.setInt(held, PerpetualStrike.HIT_ID, target.getId());
			}
			level = MiscUtil.getEnchantmentLevel(UE.ENDER_EYES, target.getItemBySlot(EquipmentSlot.HEAD));
			if(level > 0 && EnderEyes.AFFECTED_ENTITIES.contains(base.getType()) && MiscUtil.isTranscendent(target, target.getItemBySlot(EquipmentSlot.HEAD), UE.ENDER_EYES) && rand.nextDouble() < EnderEyes.TRANSCENDED_CHANCE.get()) {
				base.kill();
			}
			level = MiscUtil.getCombinedEnchantmentLevel(UE.COMBO_STAR, base);
			if(level > 0)
			{
				event.setAmount((float)(event.getAmount()*Math.pow(ComboStar.DAMAGE_LOSS.get(), Math.sqrt(level))));
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		LivingEntity target = event.getEntity();
		Entity entity = event.getSource().getEntity();
		
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			ItemStack stack = base.getMainHandItem();
			int level = UE.DEATHS_UPGRADE.getPoints(stack);
			if(level > 0)
			{
				event.setAmount(event.getAmount() + (target.getHealth() * (MathCache.SQRT_EXTRA_SPECIAL.getFloat(level)/100)));
			}
			level = MiscUtil.getEnchantmentLevel(UE.SPARTAN_WEAPON, stack);
			if(level > 0 && base.getOffhandItem().getItem() instanceof ShieldItem)
			{
				AttributeInstance attr = base.getAttribute(Attributes.ATTACK_SPEED);
				if(attr != null)
				{
					event.setAmount((float)(event.getAmount() * (1D + SpartanWeapon.EXTRA_DAMAGE.getFloat()*Math.log((event.getAmount()*event.getAmount())/attr.getValue())*level)));
				}			
			}
			level = MiscUtil.getEnchantmentLevel(UE.ENDEST_REAP, stack);
			if(level > 0)
			{
				if(rand.nextDouble() > Math.pow(0.9d, level)) 
				{
					target.addEffect(new MobEffectInstance(UE.THROMBOSIS, 100*level, level-1));
				}
				event.setAmount(event.getAmount() + (EndestReap.BONUS_DAMAGE_LEVEL.getFloat(level) + (float)Math.sqrt(EndestReap.REAP_MULTIPLIER.getFloat(level * base.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getInt(EndestReap.REAP_STORAGE)))));
			}
			level = MiscUtil.getEnchantmentLevel(UE.ADV_SHARPNESS, stack);
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
			level = MiscUtil.getEnchantmentLevel(UE.ADV_SMITE, stack);
			if(level > 0 && MiscUtil.isTranscendent(base, stack, UE.ADV_SMITE))
			{
				event.setAmount(event.getAmount() + (float)(Math.pow(target.getHealth(), AmelioratedSmite.TRANSCENDED_DAMAGE_EXPONENT.get())));
			}
			level = MiscUtil.getEnchantmentLevel(UE.ADV_BANE_OF_ARTHROPODS, stack);
			if(level > 0 && MiscUtil.isTranscendent(base, stack, UE.ADV_BANE_OF_ARTHROPODS))
			{
				event.setAmount(event.getAmount() + (float)(Math.pow(target.getHealth(), AmelioratedBaneOfArthropod.TRANSCENDED_DAMAGE_EXPONENT.get())));
			}
		}
		if(event.getSource() == DamageSource.FLY_INTO_WALL)
		{
			ItemStack stack = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
			int level = MiscUtil.getEnchantmentLevel(UE.ICARUS_AEGIS, stack);
			if(level > 0)
			{
				int feathers = StackUtils.getInt(stack, IcarusAegis.FEATHER_TAG, 0);
				int consume = (int)Math.ceil((double)IcarusAegis.BASE_CONSUMPTION.get() / level);
				if(feathers >= consume)
				{
					feathers -= consume;
					StackUtils.setInt(stack, IcarusAegis.FEATHER_TAG, feathers);
					event.setCanceled(true);
					return;
				}
			}
		}
		if(event.getAmount() >= event.getEntity().getHealth())
		{
			DamageSource source = event.getSource();
			if(!source.isMagic())
			{
				ItemStack stack = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
				if(!MiscUtil.isTranscendent(entity, stack, UE.ARES_BLESSING))
				{
					if(source == DamageSource.FALL) return;
				}
				int level = MiscUtil.getEnchantmentLevel(UE.ARES_BLESSING, stack);
				if(level > 0 && stack.isDamageableItem())
				{
					float damage = event.getAmount();
					stack.hurtAndBreak((int)(damage * AresBlessing.BASE_DAMAGE.get() / MathCache.LOG.get(level+1)), event.getEntity(), MiscUtil.get(EquipmentSlot.CHEST));
					event.setCanceled(true);
					return;
				}	
			}
			Object2IntMap.Entry<EquipmentSlot> slot = MiscUtil.getEnchantedItem(UE.PHOENIX_BLESSING, event.getEntity());
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
                event.getEntity().getItemBySlot(slot.getKey()).shrink(1);
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
		System.out.println("hello");
		int level = MiscUtil.getCombinedEnchantmentLevel(UE.COMBO_STAR, event.getEntity());
		if(level > 0)
		{
			CompoundTag nbt = MiscUtil.getPersistentData(event.getEntity());
			if(event.isVanillaCritical()) nbt.putInt(ComboStar.COMBO_NAME, Math.min(nbt.getInt(ComboStar.COMBO_NAME)+1, 100));
			else nbt.remove(ComboStar.COMBO_NAME);
			
			int combo = nbt.getInt(ComboStar.COMBO_NAME);
			
			double damage = Math.pow(ComboStar.DAMAGE_LOSS.get(), Math.sqrt(level));
			double crit = ComboStar.CRIT_DAMAGE.get(1D/damage) * 1+ComboStar.COUNTER_MULTIPLIER.get(0.01*combo);
			event.setDamageModifier((float) (event.getDamageModifier()*crit));
		}
	}
	
	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event)
	{
		ItemStack stack = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
		int level = MiscUtil.getEnchantmentLevel(UE.ICARUS_AEGIS, stack);
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
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			int level = MiscUtil.getEnchantmentLevel(UE.WARRIORS_GRACE, base.getMainHandItem());
			if(level > 0)
			{
				ItemStack stack = base.getMainHandItem();
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
				for (ItemStack stack : deadEntity.getAllSlots()) {
					ench.addAll(MiscUtil.getEnchantments(stack).keySet());
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
			int maxLevel = MiscUtil.getCombinedEnchantmentLevel(UE.DEATHS_ODIUM, event.getEntity());
			if(maxLevel > 0)
			{
				int lowest = Integer.MAX_VALUE;
				EquipmentSlot lowestSlot = null;
				int max = maxLevel+10;
				for(EquipmentSlot slot : EquipmentSlot.values())
				{
					ItemStack stack = event.getEntity().getItemBySlot(slot);
					if(MiscUtil.getEnchantmentLevel(UE.DEATHS_ODIUM, stack) > 0)
					{
						int value = StackUtils.getInt(stack, DeathsOdium.CURSE_COUNTER, 0);
						int newValue = Math.min(value + 1, max);
						if(value == newValue) continue;
						if(lowest > value)
						{
							lowest = value;
							lowestSlot = slot;
						}
					}
				}
				if(lowestSlot != null) {
					ItemStack stack = event.getEntity().getItemBySlot(lowestSlot);
					StackUtils.setInt(stack, DeathsOdium.CURSE_COUNTER, lowest+1);
					StackUtils.setFloat(stack, DeathsOdium.CURSE_STORAGE, StackUtils.getFloat(stack, DeathsOdium.CURSE_STORAGE, 0F) + ((float)Math.sqrt(event.getEntity().getMaxHealth()) * 0.3F * rand.nextFloat()));
				}
				nbt.putInt(DeathsOdium.CURSE_STORAGE, (nbt.getInt(DeathsOdium.CURSE_STORAGE)+1));
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
					return;
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
		CompoundTag nbt = event.getEntity().serializeNBT();
		float f = MiscUtil.getPersistentData(event.getEntity()).getFloat(DeathsOdium.CURSE_STORAGE);
		if(f != 0F)
		{
			event.getEntity().getAttribute(Attributes.MAX_HEALTH).addTransientModifier(new AttributeModifier(DeathsOdium.REMOVE_UUID, "odiums_curse", nbt.getBoolean(DeathsOdium.CURSE_DISABLED) ? 0 : Math.pow(0.95,f)-1, Operation.MULTIPLY_TOTAL));
		}
	}
	
	@SubscribeEvent
	public void onEaten(LivingEntityUseItemEvent.Finish event)
	{
		if(event.getItem().getItem() == Items.COOKIE && MiscUtil.getEnchantmentLevel(UE.DEATHS_ODIUM, event.getItem()) > 0)
		{
			event.getEntity().getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).putBoolean(DeathsOdium.CURSE_RESET, true);
			event.getEntity().kill();
		}
	}
	
	@SubscribeEvent
	public void onItemDamaged(ItemDurabilityChangeEvent event)
	{
		if(event.damageDone <= 0) return;
		int points = UE.GRIMOIRES_UPGRADE.getPoints(event.item);
		if(points > 0 && event.entity.getRandom().nextDouble() < (Math.sqrt(points)*0.01))
		{
			event.item.hurt(-event.damageDone, event.entity.getRandom(), null);
		}
	}
	
	@SubscribeEvent
	public void onXPDrop(LivingExperienceDropEvent event)
	{
		if(event.getAttackingPlayer() == null)
		{
			return;
		}
		int level = MiscUtil.getCombinedEnchantmentLevel(UE.SAGES_BLESSING, event.getAttackingPlayer());
		if(level > 0)
		{
			level += (event.getAttackingPlayer().level.random.nextInt(MiscUtil.getEnchantmentLevel(Enchantments.MOB_LOOTING, event.getAttackingPlayer().getItemBySlot(EquipmentSlot.MAINHAND))+1));
			double num = (event.getDroppedExperience() + event.getDroppedExperience() * (SagesBlessing.XP_BOOST.get(level)));
			event.setDroppedExperience((int) (MiscUtil.isTranscendent(event.getAttackingPlayer(), ItemStack.EMPTY, UE.SAGES_BLESSING) ? Math.pow(num, SagesBlessing.TRANSCENDED_BOOST.get()) : num));
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
		if(shooter instanceof Player)
		{
			Player player = (Player)shooter;
			Object2IntMap.Entry<EquipmentSlot> slot = MiscUtil.getEnchantedItem(UE.ENDERMARKSMEN, player);
			if(slot.getIntValue() > 0)
			{
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
					ENDER_MEN_TELEPORT.set(entity.getUUID());
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEndermenTeleport(EntityTeleportEvent event)
	{
		UUID id = ENDER_MEN_TELEPORT.get();
		if(event.getEntity().getUUID().equals(id))
		{
			ENDER_MEN_TELEPORT.set(null);
			event.setCanceled(true);
		}
		Entity entity = event.getEntity();
		if(entity instanceof EnderMan living && living.getCommandSenderWorld().getNearestPlayer(TargetingConditions.forNonCombat().selector(EnderEyes.getPlayerFilter(living)).range(MiscUtil.getAttribute(living, Attributes.FOLLOW_RANGE, 16D)), living) != null)
		{
        	event.setCanceled(true);
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
	
	private Multimap<Attribute, AttributeModifier> createModifiersFromStack(ItemStack stack, LivingEntity living, EquipmentSlot slot, boolean remove)
	{
		Multimap<Attribute, AttributeModifier> mods = HashMultimap.create();
		//Optimization. After 3 Enchantment's its sure that on average you have more then 1 full iteration. So now we fully iterate once over it since hash-code would be a faster check.
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		int level = enchantments.getInt(UE.VITAE);
		if(level > 0 && MiscUtil.getSlotsFor(UE.VITAE).contains(slot))
		{
			mods.put(Attributes.MAX_HEALTH, new AttributeModifier(Vitae.HEALTH_MOD.getId(slot), "Vitae", Math.log(1+ level*(Vitae.BASE_BOOST.get()+MiscUtil.getPlayerLevel(living, 200))), Operation.ADDITION));
		}
		level = enchantments.getInt(UE.SWIFT);
		if(level > 0 && MiscUtil.getSlotsFor(UE.SWIFT).contains(slot))
		{
			mods.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(Swift.SPEED_MOD, "Swift Boost", Swift.SPEED_BONUS.getAsDouble(level), Operation.MULTIPLY_TOTAL));
		}
		level = enchantments.getInt(UE.RANGE);
		if(level > 0 && MiscUtil.getSlotsFor(UE.RANGE).contains(slot))
		{
			mods.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(Range.RANGE_MOD, "Range Boost", Range.RANGE.getAsFloat(level), Operation.ADDITION));
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
		level = enchantments.getInt(UE.FOCUS_IMPACT);
		if(level > 0 && MiscUtil.getSlotsFor(UE.FOCUS_IMPACT).contains(slot) && (remove || MiscUtil.isTranscendent(living, stack, UE.FOCUS_IMPACT)))
		{
			mods.put(Attributes.ATTACK_SPEED, new AttributeModifier(FocusedImpact.IMPACT_MOD, "Focus Impact", FocusedImpact.TRANSCENDED_ATTACK_SPEED_MULTIPLIER.get()-1, Operation.MULTIPLY_TOTAL));
		}
		level = enchantments.getInt(UE.SWIFT_BLADE);
		if(level > 0 && MiscUtil.getSlotsFor(UE.SWIFT_BLADE).contains(slot) && (remove || MiscUtil.isTranscendent(living, stack, UE.SWIFT_BLADE)))
		{
			mods.put(Attributes.ATTACK_SPEED, new AttributeModifier(SwiftBlade.SWIFT_MOD, "Swift Blade", SwiftBlade.TRANSCENDED_ATTACK_SPEED_MULTIPLIER.get()-1, Operation.MULTIPLY_TOTAL));
		}
		level = UE.AMELIORATED_UPGRADE.isValid(stack) ? UE.AMELIORATED_UPGRADE.getPoints(stack) : 0;
		if(level > 0 && UE.AMELIORATED_UPGRADE.isValidSlot(slot))
		{
			mods.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(AmelioratedUpgrade.DAMAGE_ID, "Ameliorated Upgrade", MathCache.SQRT_EXTRA_SPECIAL.get(level), Operation.ADDITION));
		}
		if(MiscUtil.getEnchantedItem(UE.COMBO_STAR, living).getIntValue() > 0)
		{
			int counter = MiscUtil.getPersistentData(living).getInt(ComboStar.COMBO_NAME);
			mods.put(Attributes.ATTACK_SPEED, new AttributeModifier(ComboStar.SPEED_EFFECT, "Combo Star Speed", MathCache.LOG10.get((int)ComboStar.COUNTER_MULTIPLIER.get(10+counter)), Operation.MULTIPLY_BASE));
		}
		return mods;
	}
}
