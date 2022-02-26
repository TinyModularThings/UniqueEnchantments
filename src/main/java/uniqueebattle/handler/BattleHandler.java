package uniqueebattle.handler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import uniquebase.handler.MathCache;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniqueebattle.UniqueEnchantmentsBattle;
import uniqueebattle.enchantments.AresFragment;
import uniqueebattle.enchantments.AresGrace;
import uniqueebattle.enchantments.ArtemisSoul;
import uniqueebattle.enchantments.CelestialBlessing;
import uniqueebattle.enchantments.DeepWounds;
import uniqueebattle.enchantments.Fury;
import uniqueebattle.enchantments.GolemSoul;
import uniqueebattle.enchantments.GranisSoul;
import uniqueebattle.enchantments.IfritsBlessing;
import uniqueebattle.enchantments.IfritsJudgement;
import uniqueebattle.enchantments.IronBird;
import uniqueebattle.enchantments.LunaticDespair;
import uniqueebattle.enchantments.Snare;
import uniqueebattle.enchantments.StreakersWill;
import uniqueebattle.enchantments.WarsOdium;

public class BattleHandler
{
	public static final BattleHandler INSTANCE = new BattleHandler();
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START || event.side.isClient()) return;
		PlayerEntity player = event.player;
		if(player.onGround && player.world.getGameTime() % 40 == 0)
		{
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsBattle.IRON_BIRD, player.getItemStackFromSlot(EquipmentSlotType.CHEST));
			if(level > 0)
			{
				player.addPotionEffect(new EffectInstance(UniqueEnchantmentsBattle.TOUGHEND, 80, Math.max(MathHelper.floor(Math.sqrt(level)-1), 0), true, true));
			}
		}
		if(player.world.getGameTime() % 4800 == 0)
		{
			Object2IntMap.Entry<EquipmentSlotType> entry = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.ARTEMIS_SOUL, player);
			if(entry.getIntValue() > 0)
			{
				ItemStack stack = player.getItemStackFromSlot(entry.getKey());
				int enderPoints = StackUtils.getInt(stack, ArtemisSoul.ENDER_STORAGE, 0);
				if(enderPoints > 0)
				{
					StackUtils.setInt(stack, ArtemisSoul.ENDER_STORAGE, Math.max(0, enderPoints-MathHelper.ceil(Math.sqrt(entry.getIntValue()))));
				}
				else
				{
					StackUtils.setInt(stack, ArtemisSoul.TEMPORARY_SOUL_COUNT, (int)(StackUtils.getInt(stack, ArtemisSoul.TEMPORARY_SOUL_COUNT, 0) * ((-1D/(entry.getIntValue()+1D))+1D)));
				}
			}
		}
		Entity entity = event.player.getLowestRidingEntity();
		if(entity instanceof HorseEntity)
		{
			HorseEntity horse = (HorseEntity)entity;
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsBattle.GRANIS_SOUL, horse.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(EmptyHandler.INSTANCE).getStackInSlot(1));
			if(level > 0)
			{
				CompoundNBT nbt = horse.getPersistentData();
				if(nbt.getLong(GranisSoul.NEXT_DASH) < player.world.getGameTime())
				{
					IAttributeInstance instance = horse.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
					if(instance.getModifier(GranisSoul.DASH_ID) != null)
					{
						instance.removeModifier(GranisSoul.DASH_ID);	
					}
					if(UniqueEnchantmentsBattle.GRANIS_SOUL_DASH.test(player))
					{
						int duration = (MathCache.LOG10.getInt(GranisSoul.DASH_DURATION.get()+player.experienceLevel)-1)*20;
						nbt.putLong(GranisSoul.NEXT_DASH, player.world.getGameTime() + duration+20);
						nbt.putInt(GranisSoul.DASH_TIME, duration);
						instance.applyModifier(new AttributeModifier(GranisSoul.DASH_ID, "Granis Dash", Math.sqrt(GranisSoul.DASH_SPEED.get()+level), Operation.MULTIPLY_TOTAL));
						int bleed = GranisSoul.BLEED_DURATION.get(level);
						for(LivingEntity base : player.world.getEntitiesWithinAABB(LivingEntity.class, horse.getBoundingBox().grow(GranisSoul.BLEED_RANGE.get(level)), T -> T != horse && T != player && EntityPredicates.NOT_SPECTATING.test(T)))
						{
							base.addPotionEffect(new EffectInstance(UniqueEnchantmentsBattle.BLEED, bleed, level-1));
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
							horse.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(GranisSoul.DASH_ID);
						}
					}
				}
			}
			else
			{
				IAttributeInstance instance = horse.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
				if(instance.getModifier(GranisSoul.DASH_ID) != null)
				{
					instance.removeModifier(GranisSoul.DASH_ID);	
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getWorld() instanceof ServerWorld)
		{
			int count = event.getPlayer().getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG).getInt(IfritsJudgement.FLAG_JUDGEMENT_LOOT);
			if(count > 0)
			{
				TileEntity tile = event.getWorld().getTileEntity(event.getPos());
				if(tile == null) return;
				IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, event.getFace()).orElse(EmptyHandler.INSTANCE);
				if(handler.getSlots() < 9) return;
				LootTable table = ((ServerWorld)event.getWorld()).getServer().getLootTableManager().getLootTableFromLocation(IfritsJudgement.JUDGEMENT_LOOT);
				LootContext.Builder builder = (new LootContext.Builder((ServerWorld)event.getWorld())).withRandom(event.getWorld().getRandom()).withParameter(LootParameters.POSITION, event.getPos()).withLuck(event.getPlayer().getLuck()).withParameter(LootParameters.THIS_ENTITY, event.getPlayer());
				table.generate(builder.build(LootParameterSets.CHEST), T -> ItemHandlerHelper.insertItem(handler, T, false));
				MiscUtil.getPersistentData(event.getPlayer()).putInt(IfritsJudgement.FLAG_JUDGEMENT_LOOT, count-1);
				if(event.getPlayer().isSneaking()) event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onCritEvent(CriticalHitEvent event)
	{
		LivingEntity source = event.getEntityLiving();
		Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(source.getHeldItemMainhand());
		if(event.isVanillaCritical())
		{
			dropPlayerHand(event.getTarget(), ench.getInt(UniqueEnchantmentsBattle.FURY));
			return;
		}
		int level = ench.getInt(UniqueEnchantmentsBattle.ARES_FRAGMENT);
		if(level > 0 && source instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)source;
			int maxRolls = MathHelper.floor(Math.sqrt(level*player.experienceLevel)*AresFragment.BASE_ROLL_MULTIPLIER.get()) + AresFragment.BASE_ROLL.get();
			int posRolls = Math.max(source.world.rand.nextInt(Math.max(1, maxRolls)), MathHelper.floor(Math.sqrt(level)));
			int negRolls = maxRolls - posRolls;
			if(negRolls >= posRolls) return;
			event.setResult(Result.ALLOW);
			event.setDamageModifier(1.5F);
			dropPlayerHand(event.getTarget(), ench.getInt(UniqueEnchantmentsBattle.FURY));
		}
	}

	protected void dropPlayerHand(Entity target, int level)
	{
		if(level > 0 && target.world.rand.nextDouble() < Fury.DROP_CHANCE.getDevided(level))
		{
			if(target instanceof PlayerEntity)
			{
				PlayerInventory player = ((PlayerEntity)target).inventory;
				int firstEmpty = player.getFirstEmptyStack();
				if(firstEmpty == -1)
				{
					player.player.dropItem(player.getCurrentItem(), false);
					player.removeStackFromSlot(player.currentItem);
				}
				else
				{
					ItemStack stack = player.getCurrentItem().copy();
					player.removeStackFromSlot(player.currentItem);
					player.add(firstEmpty, stack);
				}
			}
			else if(target instanceof LivingEntity)
			{
				LivingEntity other = (LivingEntity)target;
				if(!other.getHeldItemMainhand().isEmpty())
				{
					if(other.getHeldItemOffhand().isEmpty())
					{
						other.setHeldItem(Hand.OFF_HAND, other.getHeldItem(Hand.MAIN_HAND));
						other.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
					}
					else
					{
						if(!other.world.isRemote)
						{
							other.entityDropItem(other.getHeldItemMainhand(), 0F);
						}
						other.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityLoot(LootingLevelEvent event)
	{
		Entity entity = event.getDamageSource().getTrueSource();
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			Object2IntMap.Entry<EquipmentSlotType> entry = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.ARTEMIS_SOUL, base);
			if(entry.getIntValue() > 0)
			{
				event.setLootingLevel(event.getLootingLevel() + MathCache.LOG10.getInt(1+StackUtils.getInt(base.getItemStackFromSlot(entry.getKey()), ArtemisSoul.PERSISTEN_SOUL_COUNT, 0)*entry.getIntValue()));
			}
		}
	}

	@SubscribeEvent
	public void onXPDrop(LivingExperienceDropEvent event)
	{
		if(event.getAttackingPlayer() == null) return;
		Object2IntMap.Entry<EquipmentSlotType> entry = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.ARTEMIS_SOUL, event.getAttackingPlayer());
		if(entry.getIntValue() > 0)
		{
			ItemStack stack = event.getAttackingPlayer().getItemStackFromSlot(entry.getKey());
			double temp = ArtemisSoul.TEMP_SOUL_SCALE.get(StackUtils.getInt(stack, ArtemisSoul.TEMPORARY_SOUL_COUNT, 0));
			event.setDroppedExperience(event.getDroppedExperience()*MathCache.LOG10.getInt(100+MathHelper.ceil(entry.getIntValue()*Math.sqrt((ArtemisSoul.PERM_SOUL_SCALE.get(StackUtils.getInt(stack, ArtemisSoul.PERSISTEN_SOUL_COUNT, 0)))+(temp*temp))))-1);
		}
	}
	
	@SubscribeEvent
	public void onArmorDamage(LivingHurtEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity)entity;
			LivingEntity target = event.getEntityLiving();
			Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(source.getHeldItemMainhand());
			int level = ench.getInt(UniqueEnchantmentsBattle.ARES_FRAGMENT);
			if(level > 0 && source instanceof PlayerEntity)
			{
				PlayerEntity player = (PlayerEntity)source;
				int maxRolls = MathHelper.floor(Math.sqrt(level*player.experienceLevel)*AresFragment.BASE_ROLL_MULTIPLIER.get()) + AresFragment.BASE_ROLL.get();
				int posRolls = Math.max(source.world.rand.nextInt(Math.max(1, maxRolls)), MathHelper.floor(Math.sqrt(level)));
				int negRolls = maxRolls - posRolls;
				double speed = MiscUtil.getAttackSpeed(player);
				float damageFactor = (float)(Math.log(1+Math.sqrt(player.experienceLevel*level*level)*(1+(MiscUtil.getArmorProtection(target))*AresFragment.ARMOR_PERCENTAGE.get())) / (100F*speed));
				event.setAmount(event.getAmount() * (1F+(damageFactor*posRolls)) / (1F+(damageFactor*negRolls)));
				source.getHeldItemMainhand().damageItem(MathHelper.ceil(Math.log(Math.abs(posRolls-Math.sqrt(negRolls)))*(((posRolls-negRolls) != 0 ? posRolls-negRolls : 1)/speed)), source, MiscUtil.get(EquipmentSlotType.MAINHAND));
			}
			level = ench.getInt(UniqueEnchantmentsBattle.DEEP_WOUNDS);
			if(level > 0 && target.getItemStackFromSlot(EquipmentSlotType.CHEST).isEmpty())
			{
				EffectInstance effect = target.getActivePotionEffect(UniqueEnchantmentsBattle.BLEED);
				if(effect != null)
				{
					event.setAmount(event.getAmount() * MathCache.LOG.getFloat(10+((int)Math.pow(DeepWounds.BLEED_SCALE.get(MiscUtil.getPlayerLevel(source, 70)),2))/100));
				}
				target.addPotionEffect(new EffectInstance(UniqueEnchantmentsBattle.BLEED, (int)Math.pow(DeepWounds.DURATION.get(level), 0.4D)*20, effect == null ? 0 : effect.getAmplifier()+1));
			}
			if(event.getEntityLiving().isBurning())
			{
				level = event.getSource().isProjectile() ? MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.IFRITS_BLESSING, source).getIntValue() : ench.getInt(UniqueEnchantmentsBattle.IFRITS_BLESSING);
				if(level > 0)
				{
					event.setAmount(event.getAmount() * ((1 + IfritsBlessing.BONUS_DAMAGE.getLogValue(2.8D, level)) * (source.isBurning() ? 2 : 1)));
				}
			}
			level = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.STREAKERS_WILL, source).getIntValue();
			if(level > 0 && source.world.rand.nextDouble() < StreakersWill.CHANCE.getAsDouble(level))
			{
				LivingEntity enemy = target;
				for(EquipmentSlotType slot : new EquipmentSlotType[]{EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.HEAD})
				{
					ItemStack stack = enemy.getItemStackFromSlot(slot);
					if(stack.isEmpty()) continue;
					stack.damageItem((int)StreakersWill.LOSS_PER_LEVEL.get(level), enemy, MiscUtil.get(slot));
					break;
				}
			}
			level = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.ARES_GRACE, source).getIntValue();
			if(level > 0)
			{
				event.setAmount(event.getAmount() + (float)Math.log(1+Math.sqrt(MiscUtil.getArmorProtection(target)*target.getHealth())*MiscUtil.getPlayerLevel(source, 0)*level*AresGrace.DAMAGE.get()));
				source.getHeldItemMainhand().damageItem(MathHelper.ceil(AresGrace.DURABILITY.get(Math.log(1+level*source.getHealth()))), source, MiscUtil.get(EquipmentSlotType.MAINHAND));
			}
			EquipmentSlotType slot = null;
			if(event.getSource().isProjectile())
			{
				Object2IntMap.Entry<EquipmentSlotType> found = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.IFRITS_BLESSING, source);
				slot = found.getKey();
				level = found.getIntValue();
			}
			else
			{
				level = ench.getInt(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT);
				slot = level > 0 ? EquipmentSlotType.MAINHAND : null;
			}
			if(level > 0)
			{
				CompoundNBT entityNBT = MiscUtil.getPersistentData(event.getEntityLiving());
				ListNBT list = entityNBT.getList(IfritsJudgement.FLAG_JUDGEMENT_ID, 10);
				boolean found = false;
				String id = source.getItemStackFromSlot(slot).getItem().getRegistryName().toString();
				for(int i = 0,m=list.size();i<m;i++)
				{
					CompoundNBT data = list.getCompound(i);
					if(data.getString(IfritsJudgement.FLAG_JUDGEMENT_ID).equalsIgnoreCase(id))
					{
						found = true;
						data.putInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT, data.getInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT)+1);
						break;
					}
				}
				if(!found)
				{
					CompoundNBT data = new CompoundNBT();
					data.putString(IfritsJudgement.FLAG_JUDGEMENT_ID, id);
					data.putInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT, 1);
					list.add(data);
					entityNBT.put(IfritsJudgement.FLAG_JUDGEMENT_ID, list);
				}
			}
			EffectInstance effect = event.getEntityLiving().getActivePotionEffect(UniqueEnchantmentsBattle.TOUGHEND);
			if(effect != null)
			{
				event.setAmount((float)(event.getAmount() * Math.pow(0.9, effect.getAmplifier()+1)));
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity)entity;
			int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.LUNATIC_DESPAIR, source);
			if(level > 0)
			{
				event.setAmount(event.getAmount() * (1F + LunaticDespair.BONUS_DAMAGE.getFloat(MathCache.LOG_ADD.getFloat(level))));
				source.hurtResistantTime = 0;
				source.attackEntityFrom(DamageSource.MAGIC, (float)Math.pow(event.getAmount()*level, 0.25)-1);
			}
			level = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.SNARE, source).getIntValue();
			if(level > 0)
			{
				event.getEntityLiving().addPotionEffect(new EffectInstance(UniqueEnchantmentsBattle.LOCK_DOWN, Snare.DURATION.get(level)));
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity)entity;
			Object2IntMap.Entry<EquipmentSlotType> found = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT, source);
			if(found.getIntValue() > 0)
			{
				ListNBT list = MiscUtil.getPersistentData(event.getEntityLiving()).getList(IfritsJudgement.FLAG_JUDGEMENT_ID, 10);
				int max = 0;
				for(int i = 0,m=list.size();i<m;i++)
				{
					max = Math.max(max, list.getCompound(i).getInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT));
				}
				if(max > IfritsJudgement.LAVA_HITS.get())
				{
					int combined = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT, source);
					source.attackEntityFrom(DamageSource.LAVA, IfritsJudgement.LAVA_DAMAGE.getAsFloat(found.getIntValue() * MathCache.LOG_MUL_MAX.getFloat(combined)));
					entity.setFire(Math.max(1, IfritsJudgement.DURATION.get(found.getIntValue()) / 20));
				}
				else if(max > IfritsJudgement.FIRE_HITS.get())
				{
					int combined = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT, source);
					source.attackEntityFrom(DamageSource.IN_FIRE, IfritsJudgement.FIRE_DAMAGE.getAsFloat(found.getIntValue() * MathCache.LOG_MUL_MAX.getFloat(combined)));
					entity.setFire(Math.max(1, IfritsJudgement.DURATION.get(found.getIntValue()) / 20));					
				}
				else if(max > 0)
				{
					CompoundNBT compound = MiscUtil.getPersistentData(source);
					compound.putInt(IfritsJudgement.FLAG_JUDGEMENT_LOOT, compound.getInt(IfritsJudgement.FLAG_JUDGEMENT_LOOT)+1);
				}
			}
			int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.WARS_ODIUM, source);
			if(level > 0 && !(event.getEntity() instanceof PlayerEntity))
			{
				CompoundNBT nbt = MiscUtil.getPersistentData(source);
				double chance = WarsOdium.SPAWN_CHANCE.getAsDouble(nbt.getInt(WarsOdium.HIT_COUNTER)) * MathCache.LOG_ADD_MAX.get(level);
				nbt.remove(WarsOdium.HIT_COUNTER);
				if(chance >= source.world.rand.nextDouble())
				{
					double spawnMod = Math.log(54.6+WarsOdium.MULTIPLIER.get(level))-3;
					int value = (int)spawnMod;
					if(value > 0)
					{
						double extraHealth = WarsOdium.HEALTH_BUFF.getAsDouble(spawnMod);
						ILivingEntityData data = null;//IDEA implement group data support so the curse gets really mean
						EntityType<?> location = event.getEntity().getType();
						Vec3d pos = event.getEntity().getPositionVector();
						if(location != null)
						{
							for(int i = 0;i<value;i++)
							{
								Entity toSpawn = location.create(event.getEntity().world);
								if(toSpawn instanceof MobEntity)
								{
									MobEntity base = (MobEntity)toSpawn;
									base.setLocationAndAngles(pos.x, pos.y, pos.z, MathHelper.wrapDegrees(event.getEntityLiving().world.rand.nextFloat() * 360.0F), 0.0F);
									base.rotationYawHead = base.rotationYaw;
				                    base.renderYawOffset = base.rotationYaw;
									data = base.onInitialSpawn(event.getEntity().world, event.getEntity().world.getDifficultyForLocation(new BlockPos(toSpawn)), SpawnReason.COMMAND, data, null);
									base.getAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier(WarsOdium.HEALTH_MOD, "wars_spawn_buff", extraHealth, Operation.MULTIPLY_TOTAL));
									base.setHealth(base.getMaxHealth());
									event.getEntity().world.addEntity(toSpawn);
									base.playAmbientSound();
								}
							}
						}
					}
				}
			}
			Object2IntMap.Entry<EquipmentSlotType> entry = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.ARTEMIS_SOUL, source);
			level = entry.getIntValue();
			if(level > 0 && !(event.getEntityLiving() instanceof AgeableEntity))
			{
				ItemStack stack = source.getItemStackFromSlot(entry.getKey());
				String key = ArtemisSoul.isValidSpecialMob(event.getEntity()) ? ArtemisSoul.PERSISTEN_SOUL_COUNT : ArtemisSoul.TEMPORARY_SOUL_COUNT;
				int playerLevel = MiscUtil.getPlayerLevel(source, 70);
				int max = (int)Math.max(Math.sqrt(playerLevel*ArtemisSoul.CAP_SCALE.get())*ArtemisSoul.CAP_FACTOR.get(), ArtemisSoul.CAP_BASE.get());
				int gain = MathCache.LOG10.getInt((int)(10+(entity.world.rand.nextInt(MiscUtil.getEnchantmentLevel(Enchantments.LOOTING, stack)+1)+1)*level*ArtemisSoul.REAP_SCALE.get()*playerLevel));
				StackUtils.setInt(stack, key, Math.min(StackUtils.getInt(stack, key, 0)+gain, max));
			}
		}
	}
	
	@SubscribeEvent
	public void onEquippementSwapped(LivingEquipmentChangeEvent event)
	{
		AbstractAttributeMap attribute = event.getEntityLiving().getAttributes();
		Multimap<String, AttributeModifier> mods = createModifiersFromStack(event.getEntityLiving(), event.getFrom(), event.getSlot(), event.getEntityLiving().getEntityWorld());
		if(!mods.isEmpty())
		{
			attribute.removeAttributeModifiers(mods);
		}
		mods = createModifiersFromStack(event.getEntityLiving(), event.getTo(), event.getSlot(), event.getEntityLiving().getEntityWorld());
		if(!mods.isEmpty())
		{
			attribute.applyAttributeModifiers(mods);
		}
	}
	
	private Multimap<String, AttributeModifier> createModifiersFromStack(LivingEntity entity, ItemStack stack, EquipmentSlotType slot, World world)
	{
		Multimap<String, AttributeModifier> mods = HashMultimap.create();
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		int level = enchantments.getInt(UniqueEnchantmentsBattle.CELESTIAL_BLESSING);
		if(level > 0)
		{
			mods.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(CelestialBlessing.SPEED_MOD, "speed_boost", world.isDaytime() ? 0F : CelestialBlessing.SPEED_BONUS.getAsDouble(level), Operation.MULTIPLY_TOTAL));
		}
		level = enchantments.getInt(UniqueEnchantmentsBattle.IRON_BIRD);
		if(level > 0)
		{
			double armor = IronBird.ARMOR.getAsDouble(level);
			mods.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(IronBird.DAMAGE_MOD, "damage_mod", armor, Operation.ADDITION));
			mods.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(IronBird.TOUGHNESS_MOD, "toughness_mod", Math.sqrt(IronBird.TOUGHNESS.get(armor)), Operation.ADDITION));
		}
		level = enchantments.getInt(UniqueEnchantmentsBattle.GOLEM_SOUL);
		if(level > 0)
		{
			mods.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(GolemSoul.SPEED_MOD, "speed_loss", (Math.pow(1-GolemSoul.SPEED.get(), level)-1), Operation.MULTIPLY_TOTAL));
			mods.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(GolemSoul.KNOCKBACK_MOD, "knockback_boost", GolemSoul.KNOCKBACK.get(level), Operation.ADDITION));
		}
		level = enchantments.getInt(UniqueEnchantmentsBattle.FURY);
		if(level > 0)
		{
			mods.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(Fury.SPEED_MOD, "fury_faster_speed", Math.pow(Fury.ATTACK_SPEED_SCALE.get(1.43D * level), 0.125D), Operation.ADDITION));
		}
		return mods;
	}
}
