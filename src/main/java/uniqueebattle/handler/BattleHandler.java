package uniqueebattle.handler;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.events.ItemDurabilityChangeEvent;
import uniquebase.handler.MathCache;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquebase.utils.events.FishingLuckEvent;
import uniquebase.utils.mixin.common.entity.ArrowMixin;
import uniquebase.utils.mixin.common.entity.DragonManagerMixin;
import uniqueebattle.UEBattle;
import uniqueebattle.enchantments.complex.AresFragment;
import uniqueebattle.enchantments.complex.ArtemisSoul;
import uniqueebattle.enchantments.complex.DeepWounds;
import uniqueebattle.enchantments.complex.GranisSoul;
import uniqueebattle.enchantments.complex.IfritsBlessing;
import uniqueebattle.enchantments.curse.IfritsJudgement;
import uniqueebattle.enchantments.curse.LunaticDespair;
import uniqueebattle.enchantments.curse.WarsOdium;
import uniqueebattle.enchantments.simple.AresGrace;
import uniqueebattle.enchantments.simple.CelestialBlessing;
import uniqueebattle.enchantments.simple.Fury;
import uniqueebattle.enchantments.simple.GolemSoul;
import uniqueebattle.enchantments.simple.IronBird;
import uniqueebattle.enchantments.simple.SagesGrace;
import uniqueebattle.enchantments.simple.Snare;
import uniqueebattle.enchantments.simple.StreakersWill;

public class BattleHandler
{
	public static final BattleHandler INSTANCE = new BattleHandler();
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START || event.side.isClient()) return;
		Player player = event.player;
		
		if(player.level.getGameTime() % 100 == 0)
		{
			int level = MiscUtil.getEnchantmentLevel(UEBattle.CELESTIAL_BLESSING, player.getMainHandItem());
			if(level > 0)
			{
				CompoundTag data = player.getPersistentData();
				boolean celestial = data.getBoolean(CelestialBlessing.CELESTIAL_DAY);
				if(player.level.isDay() == celestial)
				{
					player.level.playSound(null, player.blockPosition(), celestial ? UEBattle.CELESTIAL_BLESSING_END_SOUND : UEBattle.CELESTIAL_BLESSING_START_SOUND, SoundSource.AMBIENT, 1F, 1F);					
					data.putBoolean(CelestialBlessing.CELESTIAL_DAY, !celestial);
				}
			}
			
		}
		if(player.isOnGround() && player.level.getGameTime() % 40 == 0)
		{
			int level = MiscUtil.getEnchantmentLevel(UEBattle.IRON_BIRD, player.getItemBySlot(EquipmentSlot.CHEST));
			if(level > 0)
			{
				player.addEffect(new MobEffectInstance(UEBattle.TOUGHEND, 80, Math.max(Mth.floor(Math.sqrt(level)-1), 0), true, true));
			}
		}
		if(player.level.getGameTime() % 4800 == 0)
		{
			Object2IntMap.Entry<EquipmentSlot> entry = MiscUtil.getEnchantedItem(UEBattle.ARTEMIS_SOUL, player);
			if(entry.getIntValue() > 0)
			{
				ItemStack stack = player.getItemBySlot(entry.getKey());
				int enderPoints = StackUtils.getInt(stack, ArtemisSoul.ENDER_STORAGE, 0);
				if(enderPoints > 0)
				{
					StackUtils.setInt(stack, ArtemisSoul.ENDER_STORAGE, Math.max(0, enderPoints-Mth.ceil(Math.sqrt(entry.getIntValue()))));
				}
				else
				{
					StackUtils.setInt(stack, ArtemisSoul.TEMPORARY_SOUL_COUNT, (int)(StackUtils.getInt(stack, ArtemisSoul.TEMPORARY_SOUL_COUNT, 0) * ((-1D/(entry.getIntValue()+1D))+1D)));
				}
			}
		}
		Entity entity = event.player.getRootVehicle();
		if(entity instanceof Horse)
		{
			Horse horse = (Horse)entity;
			int level = MiscUtil.getEnchantmentLevel(UEBattle.GRANIS_SOUL, horse.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(EmptyHandler.INSTANCE).getStackInSlot(1));
			if(level > 0)
			{
				CompoundTag nbt = horse.getPersistentData();
				if(nbt.getLong(GranisSoul.NEXT_DASH) < player.level.getGameTime())
				{
					AttributeInstance instance = horse.getAttribute(Attributes.MOVEMENT_SPEED);
					if(instance.getModifier(GranisSoul.DASH_ID) != null)
					{
						instance.removeModifier(GranisSoul.DASH_ID);	
					}
					if(UEBattle.GRANIS_SOUL_DASH.test(player))
					{
						int duration = (MathCache.LOG10.getInt(GranisSoul.DASH_DURATION.get()+player.experienceLevel)-1)*20;
						nbt.putLong(GranisSoul.NEXT_DASH, player.level.getGameTime() + duration+20);
						nbt.putInt(GranisSoul.DASH_TIME, duration);
						instance.addPermanentModifier(new AttributeModifier(GranisSoul.DASH_ID, "Granis Dash", Math.sqrt(GranisSoul.DASH_SPEED.get()+level), Operation.MULTIPLY_TOTAL));
						int bleed = GranisSoul.BLEED_DURATION.get(level);
						for(LivingEntity base : player.level.getEntitiesOfClass(LivingEntity.class, horse.getBoundingBox().inflate(GranisSoul.BLEED_RANGE.get(level)), T -> T != horse && T != player && EntitySelector.NO_SPECTATORS.test(T)))
						{
							MobEffectInstance present = base.getEffect(UEBattle.BLEED);
							if(present == null) base.addEffect(new MobEffectInstance(UEBattle.BLEED, bleed, level-1));
							else base.addEffect(new MobEffectInstance(UEBattle.BLEED, bleed, present.getAmplifier()+1));
						}
					}
				}
				else
				{
					int timeLeft = nbt.getInt(GranisSoul.DASH_TIME);
					if(timeLeft >= 0)
					{
						timeLeft--;
						nbt.putInt(GranisSoul.DASH_TIME, timeLeft);
						if(timeLeft <= 0)
						{
							horse.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(GranisSoul.DASH_ID);
						}
					}
				}
			}
			else
			{
				AttributeInstance instance = horse.getAttribute(Attributes.MOVEMENT_SPEED);
				if(instance.getModifier(GranisSoul.DASH_ID) != null)
				{
					instance.removeModifier(GranisSoul.DASH_ID);	
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onItemDamaged(ItemDurabilityChangeEvent event)
	{
		if(event.damageDone <= 0) return;
		if(MiscUtil.isTranscendent(event.entity, event.item, UEBattle.STREAKERS_WILL)) {
			StackUtils.setInt(event.item, StreakersWill.STREAKERS_WILL_NAME, StackUtils.getInt(event.item, StreakersWill.STREAKERS_WILL_NAME, 0) + event.damageDone);
		}
	}
	
	@SubscribeEvent
	public void onCritEvent(CriticalHitEvent event)
	{
		LivingEntity source = event.getEntity();
		Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(source.getMainHandItem());
		if(event.isVanillaCritical())
		{
			dropPlayerHand(event.getTarget(), ench.getInt(UEBattle.FURY));
			
			int level = ench.getInt(UEBattle.ARES_FRAGMENT);
			if(level > 0 && source instanceof Player)
			{
				Player player = (Player)source;
				int maxRolls = (int) (AresFragment.BASE_ROLL_MULTIPLIER.get(Math.pow(MiscUtil.getPlayerLevel(source, 200) * level*level, 0.35))) + AresFragment.BASE_ROLL.get();
				int posRolls = Math.max(source.level.random.nextInt(maxRolls), Mth.floor(Math.sqrt(level)));
				int negRolls = maxRolls - posRolls;
				if(negRolls >= posRolls) return;
				event.setResult(Result.ALLOW);
				if(MiscUtil.isTranscendent(source, player.getMainHandItem(), UEBattle.ARES_FRAGMENT))
				{
					event.setDamageModifier(event.getDamageModifier() + AresFragment.TRANSCENDED_CRIT_MULTIPLIER.getFloat());
				}
			}

			level = UEBattle.ARES_UPGRADE.getPoints(source.getMainHandItem());
			if(level > 0) event.setDamageModifier((int) (event.getDamageModifier() * (1+Math.log(1+level)/100)));
		}
	}
	
	@SubscribeEvent
	public void onItemUseTick(LivingEntityUseItemEvent.Tick event) {
		int level = MiscUtil.getEnchantmentLevel(UEBattle.CELESTIAL_BLESSING, event.getItem());
		LivingEntity entity = event.getEntity();
		if(level > 0 && event.getDuration() > 10 && entity.getLevel().isNight() && MiscUtil.isTranscendent(entity, event.getItem(), UEBattle.CELESTIAL_BLESSING)) {
			event.setDuration(event.getDuration() - level);
		}
	}
	
	@SubscribeEvent
	public void onItemUseStart(LivingEntityUseItemEvent.Start event)
	{
		int level = MiscUtil.getEnchantmentLevel(UEBattle.CELESTIAL_BLESSING, event.getItem());
		LivingEntity entity = event.getEntity();
		if(level > 0 && event.getDuration() > 10)
		{
			float boost = CelestialBlessing.SPEED_BONUS.getAsFloat(level);
			double num = (1 + (event.getEntity().level.isNight() ? boost : 0));
			event.setDuration((int)Math.max(10,event.getDuration() / num));
		}
		level = UEBattle.LUNATIC_UPGRADE.getCombinedPoints(entity);
		if(level > 0)
		{
			event.setDuration(Math.max(1, event.getDuration() - MathCache.LOG10.getInt(level+1)));
		}
	}
	
	protected void dropPlayerHand(Entity target, int level)
	{
		if(level > 0 && target.level.random.nextDouble() < Fury.DROP_CHANCE.getDevided(level))
		{
			if(target instanceof Player inv)
			{
				target.level.playSound(null, target.blockPosition(), UEBattle.FURY_DROP_SOUND, SoundSource.AMBIENT, 10F, 1F);
				Inventory player = inv.getInventory();
				int firstEmpty = player.getFreeSlot();
				if(firstEmpty == -1)
				{
					player.player.drop(player.getSelected(), false);
					player.removeItemNoUpdate(player.selected);
				}
				else
				{
					ItemStack stack = player.getSelected().copy();
					player.removeItemNoUpdate(player.selected);
					player.add(firstEmpty, stack);
				}
			}
			else if(target instanceof LivingEntity other)
			{
				if(!other.getMainHandItem().isEmpty())
				{
					if(other.getOffhandItem().isEmpty())
					{
						other.setItemInHand(InteractionHand.OFF_HAND, other.getItemInHand(InteractionHand.MAIN_HAND));
						other.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
					}
					else
					{
						if(!other.level.isClientSide)
						{
							ItemEntity entity = other.spawnAtLocation(other.getMainHandItem(), 0F);
							if(entity != null) entity.setPickUpDelay(30);
						}
						other.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityLoot(LootingLevelEvent event)
	{
		if(event.getDamageSource() == null) return;
		Entity entity = event.getDamageSource().getEntity();
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			Object2IntMap.Entry<EquipmentSlot> entry = MiscUtil.getEnchantedItem(UEBattle.ARTEMIS_SOUL, base);
			if(entry.getIntValue() > 0)
			{
				event.setLootingLevel(event.getLootingLevel() + MathCache.LOG10.getInt(1+StackUtils.getInt(base.getItemBySlot(entry.getKey()), ArtemisSoul.PERSISTEN_SOUL_COUNT, 0)*entry.getIntValue()));
			}
			int level = UEBattle.IFRITS_UPGRADE.getCombinedPoints(base);
			if(level > 0)
			{
				event.setLootingLevel(event.getLootingLevel() + (int) Math.floor(Math.log10(10+level)));
			}
		}
	}
	
	@SubscribeEvent
	public void onFishingLuck(FishingLuckEvent event)
	{
		int level = UEBattle.IFRITS_UPGRADE.getPoints(event.getStack());
		if(level > 0)
		{
			event.setLevel(event.getLevel() + (int) Math.floor(Math.log10(10+level)));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onXPDrop(LivingExperienceDropEvent event)
	{
		if(event.getAttackingPlayer() == null) return;
		Player player = event.getAttackingPlayer();
		Object2IntMap.Entry<EquipmentSlot> entry = MiscUtil.getEnchantedItem(UEBattle.ARTEMIS_SOUL, player);
		if(entry.getIntValue() > 0)
		{
			ItemStack stack = event.getAttackingPlayer().getItemBySlot(entry.getKey());
			double temp = ArtemisSoul.TEMP_SOUL_SCALE.get(StackUtils.getInt(stack, ArtemisSoul.TEMPORARY_SOUL_COUNT, 0));
			event.setDroppedExperience(event.getDroppedExperience()*MathCache.LOG10.getInt(100+Mth.ceil(entry.getIntValue()*Math.sqrt((ArtemisSoul.PERM_SOUL_SCALE.get(StackUtils.getInt(stack, ArtemisSoul.PERSISTEN_SOUL_COUNT, 0)))+(temp*temp))))-1);
		}
		for(EquipmentSlot slot:MiscUtil.getSlotsFor(UEBattle.SAGES_GRACE)) {
			ItemStack stack = player.getItemBySlot(slot);
			int level = player.getItemBySlot(slot).getEnchantmentLevel(UEBattle.SAGES_GRACE);
			event.setDroppedExperience(event.getDroppedExperience()+level);
			stack.hurtAndBreak(level * player.getRandom().nextInt(1), player, MiscUtil.get(stack.getEquipmentSlot()));
		}
	}
	
	@SubscribeEvent
	public void onArmorDamage(LivingHurtEvent event)
	{
		Entity entity = event.getSource().getEntity();
		if(entity instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity)entity;
			LivingEntity target = event.getEntity();
			ItemStack stack = event.getSource().getDirectEntity() instanceof ThrownTrident trident ? ((ArrowMixin)trident).getArrowItem() : source.getMainHandItem();
			Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(stack);
			int level = ench.getInt(UEBattle.ARES_FRAGMENT);
			if(level > 0 && source instanceof Player player)
			{
				int maxRolls = (int) (AresFragment.BASE_ROLL_MULTIPLIER.get(Math.pow(MiscUtil.getPlayerLevel(source, 200) * level*level, 0.35))) + AresFragment.BASE_ROLL.get();
				int posRolls = Math.max(source.level.random.nextInt(maxRolls), Mth.floor(Math.sqrt(level)));
				int negRolls = maxRolls - posRolls;
				double speed = MiscUtil.getAttackSpeed(player);
				float damageFactor = (float) (Math.log((1+MiscUtil.getArmorProtection(target))*level)/(100*speed));
				event.setAmount(event.getAmount() * (1F+(damageFactor*posRolls)) / (1F+(damageFactor*negRolls)));
				stack.hurtAndBreak(Mth.ceil(Math.log(Math.abs(posRolls-Math.sqrt(negRolls)))*(((posRolls-negRolls) != 0 ? posRolls-negRolls : 1)/speed)), source, MiscUtil.get(EquipmentSlot.MAINHAND));
			}
			if(event.getEntity().isOnFire())
			{
				level = event.getSource().isProjectile() ? MiscUtil.getEnchantedItem(UEBattle.IFRITS_BLESSING, source).getIntValue() : ench.getInt(UEBattle.IFRITS_BLESSING);
				if(level > 0)
				{
					event.setAmount(event.getAmount() * ((1 + IfritsBlessing.BONUS_DAMAGE.getLogValue(2.8D, level)) * (source.isOnFire() ? 2 : 1)));
				}
			}
			level = ench.getInt(UEBattle.DEEP_WOUNDS);
			if(level > 0 && target.getItemBySlot(EquipmentSlot.CHEST).isEmpty())
			{
				MobEffectInstance effect = target.getEffect(UEBattle.BLEED);
				if(effect != null)
				{
					event.setAmount(event.getAmount() * MathCache.LOG.getFloat(10+(int) Math.pow(DeepWounds.BLEED_SCALE.get(MiscUtil.getPlayerLevel(source, 70)), 0.35)));
				}
				target.addEffect(new MobEffectInstance(UEBattle.BLEED, (int)Math.pow(DeepWounds.DURATION.get(level), 0.4D)*20, effect == null ? 0 : effect.getAmplifier()+1));
			}
			level = MiscUtil.getEnchantedItem(UEBattle.STREAKERS_WILL, source).getIntValue();
			if(level > 0 && source.level.random.nextDouble() < StreakersWill.CHANCE.getAsDouble(level))
			{
				LivingEntity enemy = target;
				for(EquipmentSlot targetSlot : new EquipmentSlot[]{EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.HEAD})
				{
					ItemStack itemStack = enemy.getItemBySlot(targetSlot);
					if(itemStack.isEmpty()) continue;
					int num = StackUtils.getInt(stack, StreakersWill.STREAKERS_WILL_NAME, 0);
					itemStack.hurtAndBreak((int)Math.ceil(StreakersWill.LOSS_PER_LEVEL.get(level)/MiscUtil.getAttackSpeed(source, 1.6D))+ num, enemy, MiscUtil.get(targetSlot));
					StackUtils.setInt(stack, StreakersWill.STREAKERS_WILL_NAME, 0);
					break;
				}
			}
			level = MiscUtil.getEnchantedItem(UEBattle.ARES_GRACE, source).getIntValue();
			if(level > 0)
			{
				MiscUtil.doNewDamageInstance(target, UEBattle.ARES_GRACE_DAMAGE, (float)Math.log(1+Math.sqrt(MiscUtil.getArmorProtection(target)*target.getHealth()*MiscUtil.getPlayerLevel(source, 0)*level)*AresGrace.DAMAGE.get()));
				stack.hurtAndBreak(Mth.ceil(AresGrace.DURABILITY.get(Math.log(1+level*source.getHealth()))), source, MiscUtil.get(EquipmentSlot.MAINHAND));
			}
			level = ench.getInt(UEBattle.SAGES_GRACE);
			if(level > 0)
			{
				int value = StackUtils.getInt(stack, SagesGrace.STORED_XP, 0);
				float amount = (float) Math.log(1+value);
				MiscUtil.doNewDamageInstance(target, UEBattle.SAGES_GRACE_DAMAGE, amount);
				if(source instanceof Player player && player.totalExperience > 10 && player.experienceLevel > 0) {
					StackUtils.setInt(stack, SagesGrace.STORED_XP, value + MiscUtil.drainExperience(player, player.experienceLevel));
				}
			}
			level = ench.getInt(UEBattle.IFRITS_JUDGEMENT);
			if(level > 0)
			{
				CompoundTag entityNBT = MiscUtil.getPersistentData(event.getEntity());
				ListTag list = entityNBT.getList(IfritsJudgement.FLAG_JUDGEMENT_ID, 10);
				boolean found = false;
				String id = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
				for(int i = 0,m=list.size();i<m;i++)
				{
					CompoundTag data = list.getCompound(i);
					if(data.getString(IfritsJudgement.FLAG_JUDGEMENT_ID).equalsIgnoreCase(id))
					{
						found = true;
						data.putInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT, data.getInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT)+1);
						break;
					}
				}
				if(!found)
				{
					CompoundTag data = new CompoundTag();
					data.putString(IfritsJudgement.FLAG_JUDGEMENT_ID, id);
					data.putInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT, 1);
					list.add(data);
					entityNBT.put(IfritsJudgement.FLAG_JUDGEMENT_ID, list);
				}
			}
			MobEffectInstance effect = event.getEntity().getEffect(UEBattle.TOUGHEND);
			if(effect != null)
			{
				event.setAmount((float)(event.getAmount() * Math.pow(0.9, effect.getAmplifier()+1)));
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		Entity entity = event.getSource().getEntity();
		if(entity instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity)entity;
			int level = MiscUtil.getCombinedEnchantmentLevel(UEBattle.LUNATIC_DESPAIR, source);
			if(level > 0)
			{
				event.setAmount(event.getAmount() * (1F + LunaticDespair.BONUS_DAMAGE.getFloat() + (float)Math.sqrt(level)/10F));
				boolean transcent = MiscUtil.isTranscendent(source, source.getItemBySlot(MiscUtil.getEnchantedItem(UEBattle.LUNATIC_DESPAIR, source).getKey()), UEBattle.LUNATIC_DESPAIR);
				MiscUtil.doNewDamageInstance(source, transcent ? DamageSource.MAGIC : DamageSource.OUT_OF_WORLD, event.getAmount()*(float)Math.sqrt(level/1000D));
			}
			level = MiscUtil.getCombinedEnchantmentLevel(UEBattle.WARS_ODIUM, source);
			if(level > 0)
			{
				CompoundTag nbt = MiscUtil.getPersistentData(source);
				nbt.putInt(WarsOdium.HIT_COUNTER, nbt.getInt(WarsOdium.HIT_COUNTER)+1);
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
		if(hit.getEntity() instanceof LivingEntity living)
		{
			int level = MiscUtil.getEnchantmentLevel(UEBattle.SNARE, StackUtils.getArrowStack(arrow));
			if(level > 0)
			{
				living.addEffect(new MobEffectInstance(UEBattle.LOCK_DOWN, Snare.DURATION.get(level)));
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event)
	{
		Entity entity = event.getSource().getEntity();
		Entity deadEntity = event.getEntity();
		if(entity instanceof LivingEntity source && deadEntity instanceof LivingEntity dead)
		{
			Object2IntMap.Entry<EquipmentSlot> found = MiscUtil.getEnchantedItem(UEBattle.IFRITS_JUDGEMENT, source);
			if(found.getIntValue() > 0)
			{
				ListTag list = MiscUtil.getPersistentData(event.getEntity()).getList(IfritsJudgement.FLAG_JUDGEMENT_ID, 10);
				int max = 0;
				for(int i = 0,m=list.size();i<m;i++)
				{
					max = Math.max(max, list.getCompound(i).getInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT));
				}
				if(max > IfritsJudgement.LAVA_HITS.get())
				{
					CompoundTag compound = MiscUtil.getPersistentData(source);
					int totalSuccess = compound.getInt(IfritsJudgement.FLAG_JUDGEMENT_SUCCESS)+1;
					int combined = MiscUtil.getCombinedEnchantmentLevel(UEBattle.IFRITS_JUDGEMENT, source);
					MiscUtil.doNewDamageInstance(source, DamageSource.LAVA, IfritsJudgement.LAVA_DAMAGE.getAsFloat(found.getIntValue() * MathCache.LOG_MUL_MAX.getFloat(combined)) * totalSuccess);
					entity.setSecondsOnFire(Math.max(1, IfritsJudgement.DURATION.get(found.getIntValue()) / 20));
					compound.putInt(IfritsJudgement.FLAG_JUDGEMENT_SUCCESS, 0);
				}
				else if(max > 0)
				{
					CompoundTag compound = MiscUtil.getPersistentData(source);
					compound.putInt(IfritsJudgement.FLAG_JUDGEMENT_SUCCESS, compound.getInt(IfritsJudgement.FLAG_JUDGEMENT_SUCCESS)+1);
					LootTable table = ((ServerLevel)entity.getLevel()).getServer().getLootTables().get(IfritsJudgement.JUDGEMENT_LOOT);
					LootContext.Builder builder = (new LootContext.Builder((ServerLevel)entity.getLevel())).withRandom(entity.getLevel().getRandom()).withLuck(MiscUtil.getLuck(entity, 2F)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(deadEntity.blockPosition())).withParameter(LootContextParams.THIS_ENTITY, entity);
					table.getRandomItemsRaw(builder.create(LootContextParamSets.CHEST), T -> Block.popResource(entity.getLevel(), deadEntity.blockPosition(), T));
				}
			}
			int level = MiscUtil.getCombinedEnchantmentLevel(UEBattle.WARS_ODIUM, source);
			if(level > 0 && !(event.getEntity() instanceof Player) && !WarsOdium.BLACKLIST.contains(event.getEntity().getType()) && !event.getEntity().getTags().contains(WarsOdium.IGNORE))
			{
				CompoundTag nbt = MiscUtil.getPersistentData(source);
				boolean isBoss = WarsOdium.BOSS_LIST.contains(event.getEntity().getType());
				nbt.remove(WarsOdium.HIT_COUNTER);
				int limit = 1+Math.min(9, isBoss ? 0 : MiscUtil.getPlayerLevel(source, 0)/100);
				double chance = (1-Math.pow((isBoss ? WarsOdium.BOSS_CHANCE : WarsOdium.SPAWN_CHANCE).get(), Math.sqrt(nbt.getInt(WarsOdium.HIT_COUNTER)+level))) / limit;
				List<Mob> toFilter = new ObjectArrayList<>();
				for(int j = 0;j<limit;j++)
				{
					double random = source.level.random.nextDouble();
					if(chance >= random)
					{
						double spawnMod = Math.log(54.6+WarsOdium.MULTIPLIER.get(level))-3;
						int value = (int)spawnMod;
						if(value > 0)
						{
							double extraHealth = WarsOdium.HEALTH_BUFF.getAsDouble(spawnMod);
							SpawnGroupData data = null;//IDEA implement group data support so the curse gets really mean
							EntityType<?> location = dead.getType();
							Vec3 pos = dead.position();
							if(location != null)
							{
								for(int i = 0;i<value;i++)
								{
									Entity toSpawn = location.create(dead.level);
									if(toSpawn instanceof Mob)
									{
										Mob base = (Mob)toSpawn;
										base.moveTo(pos.x, pos.y, pos.z, Mth.wrapDegrees(dead.level.random.nextFloat() * 360.0F), 0.0F);
										toFilter.add(base);
										base.moveTo(pos.x, pos.y, pos.z, Mth.wrapDegrees(event.getEntity().level.random.nextFloat() * 360.0F), 0.0F);
										base.yRotO = base.getYRot();
										base.xRotO = base.getXRot();
										data = base.finalizeSpawn((ServerLevelAccessor)dead.level, dead.level.getCurrentDifficultyAt(toSpawn.blockPosition()), MobSpawnType.COMMAND, data, null);
										base.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(WarsOdium.HEALTH_MOD, "wars_spawn_buff", extraHealth, Operation.MULTIPLY_TOTAL));
										base.setHealth(base.getMaxHealth());
										dead.level.addFreshEntity(toSpawn);
										toSpawn.level.playSound(null, toSpawn.blockPosition(), UEBattle.WARS_ODIUM_REVIVE_SOUND, SoundSource.AMBIENT, 1F, 1F);
										base.playAmbientSound();
										Level world = dead.level;
										if(base instanceof EnderDragon && world instanceof ServerLevel)
										{
											EndDragonFight manager = ((ServerLevel)world).dragonFight();
											if(manager != null) ((DragonManagerMixin)manager).setNewDragon(base.getUUID());
										}
									}
								}
							}
						}
					}
				}
				if(toFilter.size() > 1) {
					for(int i = 1;i<toFilter.size();i++) {
						toFilter.get(i).addTag(WarsOdium.IGNORE);
					}
				}
			}
			ItemStack stack = event.getSource().getDirectEntity() instanceof ThrownTrident trident ? ((ArrowMixin)trident).getArrowItem() : source.getMainHandItem();
			level = MiscUtil.getEnchantmentLevel(UEBattle.ARTEMIS_SOUL, stack);
			if(level > 0 && !(event.getEntity() instanceof AgeableMob))
			{
				String key = ArtemisSoul.isValidSpecialMob(event.getEntity()) ? ArtemisSoul.PERSISTEN_SOUL_COUNT : ArtemisSoul.TEMPORARY_SOUL_COUNT;
				int playerLevel = MiscUtil.getPlayerLevel(source, 70);
				int max = (int)Math.max(Math.sqrt(playerLevel*ArtemisSoul.CAP_SCALE.get())*ArtemisSoul.CAP_FACTOR.get(), ArtemisSoul.CAP_BASE.get());
				int gain = MathCache.LOG10.getInt((int)(10+(entity.level.random.nextInt(MiscUtil.getEnchantmentLevel(Enchantments.MOB_LOOTING, stack)+1)+1)*level*ArtemisSoul.REAP_SCALE.get()*playerLevel));
				if(MiscUtil.isTranscendent(entity, stack, UEBattle.ARTEMIS_SOUL)) {
					float num = (float) Math.max(Math.sqrt(ArtemisSoul.TRANSCENDED_REAP_MULTIPLIER.get(dead.getMaxHealth())), StackUtils.getFloat(stack, ArtemisSoul.TRANSCENDED_MOD, 0));
					StackUtils.setFloat(stack, ArtemisSoul.TRANSCENDED_MOD, num);
				}
				StackUtils.setInt(stack, key, Math.min(StackUtils.getInt(stack, key, 0)+gain, max));
			}
			int points = UEBattle.WARS_UPGRADE.getPoints(source.getMainHandItem());
			if(points > 0) {
				source.heal(MathCache.SQRT_EXTRA_SPECIAL.getFloat(points));
			}
		}
	}
	
	@SubscribeEvent
	public void onEquippementSwapped(LivingEquipmentChangeEvent event)
	{
		AttributeMap attribute = event.getEntity().getAttributes();
		Multimap<Attribute, AttributeModifier> mods = createModifiersFromStack(event.getEntity(), event.getFrom(), event.getSlot(), event.getEntity().getCommandSenderWorld());
		if(!mods.isEmpty())
		{
			attribute.removeAttributeModifiers(mods);
		}
		mods = createModifiersFromStack(event.getEntity(), event.getTo(), event.getSlot(), event.getEntity().getCommandSenderWorld());
		if(!mods.isEmpty())
		{
			attribute.addTransientAttributeModifiers(mods);
		}
	}
	
	@SubscribeEvent
	public void onFall(LivingFallEvent event) {
		LivingEntity entity = event.getEntity();
		float distance = event.getDistance();
		if(distance >= 4F)
		{
			ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
			if(MiscUtil.isTranscendent(entity, stack, UEBattle.GOLEM_SOUL))
			{
				int level = MiscUtil.getEnchantmentLevel(UEBattle.GOLEM_SOUL, stack);
				if(level > 0) 
				{
					for(Entity ent : entity.level.getEntities(entity, new AABB(entity.blockPosition()).inflate(Math.min(distance, 16)))) 
					{
						if(ent instanceof LivingEntity enti)
						{
							enti.hurt(DamageSource.DRAGON_BREATH, (float) Math.sqrt(distance));
							enti.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, GolemSoul.TRANSCENDED_SLOW_TIME.get(level), Math.min(level-1,5)));
						}
					}
				}
			}
		}
	}
	
	private Multimap<Attribute, AttributeModifier> createModifiersFromStack(LivingEntity entity, ItemStack stack, EquipmentSlot slot, Level world)
	{
		Multimap<Attribute, AttributeModifier> mods = HashMultimap.create();
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);

		int level = enchantments.getInt(UEBattle.CELESTIAL_BLESSING);
		if(level > 0)
		{
			double value = !world.isDay() ? (MiscUtil.isTranscendent(entity, stack, UEBattle.CELESTIAL_BLESSING) ? Math.pow(1+CelestialBlessing.SPEED_BONUS.getAsDouble(level),2)-1 : CelestialBlessing.SPEED_BONUS.getAsDouble(level)) : 0;
			mods.put(Attributes.ATTACK_SPEED, new AttributeModifier(CelestialBlessing.SPEED_MOD, "speed_boost", value, Operation.MULTIPLY_TOTAL));
		}
		level = enchantments.getInt(UEBattle.IRON_BIRD);
		if(level > 0)
		{
			double armor = IronBird.ARMOR.getAsDouble(level);
			mods.put(Attributes.ARMOR, new AttributeModifier(IronBird.DAMAGE_MOD, "damage_mod", armor, Operation.ADDITION));
			mods.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(IronBird.TOUGHNESS_MOD, "toughness_mod", Math.sqrt(IronBird.TOUGHNESS.get(armor)), Operation.ADDITION));
		}
		level = enchantments.getInt(UEBattle.GOLEM_SOUL);
		if(level > 0)
		{
			mods.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(GolemSoul.SPEED_MOD, "speed_loss", (Math.pow(1-GolemSoul.SPEED.get(), level)-1), Operation.MULTIPLY_TOTAL));
			mods.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(GolemSoul.KNOCKBACK_MOD, "knockback_boost", GolemSoul.KNOCKBACK.get(level), Operation.ADDITION));
		}
		level = enchantments.getInt(UEBattle.FURY);
		if(level > 0)
		{
			mods.put(Attributes.ATTACK_SPEED, new AttributeModifier(Fury.SPEED_MOD, "fury_faster_speed", Math.pow(Fury.ATTACK_SPEED_SCALE.get(1.43D * level), 0.125D), Operation.ADDITION));
		}
		return mods;
	}
}
