package uniquee.handler;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.storage.MapBanner;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniquebase.UniqueEnchantmentsBase;
import uniquebase.handler.MathCache;
import uniquebase.networking.EntityPacket;
import uniquebase.utils.EnchantmentContainer;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquebase.utils.events.EndermenLookEvent;
import uniquebase.utils.mixin.InteractionManagerMixin;
import uniquebase.utils.mixin.MapDataMixin;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.complex.EnderMending;
import uniquee.enchantments.complex.Momentum;
import uniquee.enchantments.complex.PerpetualStrike;
import uniquee.enchantments.complex.SmartAss;
import uniquee.enchantments.complex.SpartanWeapon;
import uniquee.enchantments.complex.SwiftBlade;
import uniquee.enchantments.curse.DeathsOdium;
import uniquee.enchantments.curse.PestilencesOdium;
import uniquee.enchantments.simple.Berserk;
import uniquee.enchantments.simple.BoneCrusher;
import uniquee.enchantments.simple.EnderEyes;
import uniquee.enchantments.simple.FocusImpact;
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

public class EntityEvents
{
	public static final EntityEvents INSTANCE = new EntityEvents();
	static final ThreadLocal<UUID> ENDER_MEN_TELEPORT = new ThreadLocal<>();
		
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		Entity entity = event.getEntity();
		if(entity instanceof ItemEntity)
		{
			if(MiscUtil.getEnchantmentLevel(UniqueEnchantments.GRIMOIRE, ((ItemEntity)entity).getItem()) > 0)
			{
				entity.setInvulnerable(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onEndermenLookEvent(EndermenLookEvent event)
	{
		if(MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_EYES, event.getPlayer().getItemBySlot(EquipmentSlotType.HEAD)) > 0)
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onAnvilRepair(AnvilUpdateEvent event)
	{
		if(MiscUtil.getEnchantmentLevel(UniqueEnchantments.GRIMOIRE, event.getLeft()) > 0)
		{
			event.setCost(Integer.MAX_VALUE);
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START) return;
		PlayerEntity player = event.player;
		EnchantmentContainer container = new EnchantmentContainer(player);
		if(event.side.isServer())
		{
			if(player.getHealth() < player.getMaxHealth())
			{
				int level = container.getEnchantment(UniqueEnchantments.NATURES_GRACE, EquipmentSlotType.CHEST);
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
				EnderMending.shareXP(player, container);				
			}
			if(player.level.getGameTime() % 100 == 0)
			{
				for(Int2ObjectMap.Entry<ItemStack> entry : container.getEnchantedItems(UniqueEnchantments.ENDER_MENDING))
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
				int level = container.getEnchantment(UniqueEnchantments.ENDEST_REAP, EquipmentSlotType.MAINHAND);
				if(level > 0)
				{
					StackUtils.setInt(player.getMainHandItem(), EndestReap.REAP_STORAGE, player.getPersistentData().getInt(EndestReap.REAP_STORAGE));
				}
			}
			if(player.level.getGameTime() % 1200 == 0)
			{
				EquipmentSlotType[] slots = MiscUtil.getEquipmentSlotsFor(UniqueEnchantments.GRIMOIRE);
				for(int i = 0;i<slots.length;i++)
				{
					int level = container.getEnchantment(UniqueEnchantments.GRIMOIRE, slots[i]);
					if(level > 0)
					{
						Grimoire.applyGrimore(player.getItemBySlot(slots[i]), level, player);
					}
				}
			}
			if(player.level.getGameTime() % 30 == 0)
			{
				ClimateTranquility.onClimate(player, container);
			}
			if(player.level.getGameTime() % 10 == 0)
			{
				int level = container.getEnchantment(UniqueEnchantments.ICARUS_AEGIS, EquipmentSlotType.CHEST);
				if(level > 0)
				{
					player.getItemBySlot(EquipmentSlotType.CHEST).getTag().putBoolean(IcarusAegis.FLYING_TAG, player.isFallFlying());
				}
			}
			if(player.level.getGameTime() % 40 == 0)
			{
				int level = container.getCombinedEnchantment(UniqueEnchantments.PESTILENCES_ODIUM);
				if(level > 0)
				{
					List<AgeableEntity> living = player.level.getEntitiesOfClass(AgeableEntity.class, new AxisAlignedBB(player.blockPosition()).inflate(PestilencesOdium.RADIUS.get()));
					for(int i = 0,m=living.size();i<m;i++)
					{
						living.get(i).addEffect(new EffectInstance(UniqueEnchantments.PESTILENCES_ODIUM_POTION, 200, level));
					}
				}
			}
			if(player.level.getGameTime() % 20 == 0)
			{
				Object2IntMap.Entry<EquipmentSlotType> level = container.getEnchantedItem(UniqueEnchantments.SAGES_BLESSING);
				if(level.getIntValue() > 0)
				{
					player.causeFoodExhaustion(0.01F * level.getIntValue());
				}
			}
			CompoundNBT data = event.player.getPersistentData();
			if(data.contains(DeathsOdium.CURSE_DAMAGE) && data.getLong(DeathsOdium.CURSE_TIMER) < event.player.level.getGameTime())
			{
				int total = MathHelper.floor(data.getFloat(DeathsOdium.CURSE_DAMAGE) / DeathsOdium.DAMAGE_FACTOR.get());
				if(total > 0)
				{
					ModifiableAttributeInstance instance = event.player.getAttribute(Attributes.MAX_HEALTH);
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
		int level = container.getEnchantment(UniqueEnchantments.CLOUD_WALKER, EquipmentSlotType.FEET);
		if(level > 0)
		{
			CompoundNBT nbt = player.getPersistentData();
			if(player.isShiftKeyDown() && !nbt.getBoolean(Cloudwalker.TRIGGER) && (!player.isOnGround() || nbt.getBoolean(Cloudwalker.ENABLED)))
			{
				nbt.putBoolean(Cloudwalker.ENABLED, !nbt.getBoolean(Cloudwalker.ENABLED));
				nbt.putBoolean(Cloudwalker.TRIGGER, true);
			}
			else if(!player.isShiftKeyDown())
			{
				nbt.putBoolean(Cloudwalker.TRIGGER, false);
			}
			ItemStack stack = player.getItemBySlot(EquipmentSlotType.FEET);
			if(nbt.getBoolean(Cloudwalker.ENABLED))
			{
				int value = StackUtils.getInt(stack, Cloudwalker.TIMER, Cloudwalker.TICKS.get(level) * 5);
				if(value <= 0)
				{
					nbt.putBoolean(Cloudwalker.ENABLED, false);
					return;
				}
				Vector3d vec = player.getDeltaMovement();
				player.setDeltaMovement(vec.x, player.abilities.flying ? 0.15D : 0D, vec.z);
				player.causeFallDamage(player.fallDistance, 1F);
				player.fallDistance = 0F;
				player.setOnGround(true);
				if(!player.isCreative())
				{
					boolean levi = player.hasEffect(Effects.LEVITATION);
					int leviLevel = levi ? player.getEffect(Effects.LEVITATION).getAmplifier()+1 : 0;
					StackUtils.setInt(stack, Cloudwalker.TIMER, value-Math.max(1, 5 - (leviLevel * 2)));
					if(player.level.getGameTime() % Math.max(1, (int)(20 * (Math.sqrt(level) / (leviLevel+1)))) == 0)
					{
						stack.hurtAndBreak(1, player, MiscUtil.get(EquipmentSlotType.FEET));
					}
				}
			}
			else
			{
				StackUtils.setInt(stack, Cloudwalker.TIMER, Cloudwalker.TICKS.get(level) * 5);
			}
		}
		level = container.getEnchantment(UniqueEnchantments.SWIFT, EquipmentSlotType.LEGS);
		if(level > 0 && player.onClimbable() && player.zza != 0F && player.getDeltaMovement().y() > 0 && player.getDeltaMovement().y() <= 0.2 && !player.isShiftKeyDown())
		{
			player.setDeltaMovement(player.getDeltaMovement().add(0, Swift.SPEED_BONUS.getAsDouble(level) * 3D, 0));
		}
		Boolean cache = null;
		//Reflection is slower then direct call. But Twice the Iteration & Double IsEmpty Check is slower then Reflection.
		EquipmentSlotType[] slots = MiscUtil.getEquipmentSlotsFor(UniqueEnchantments.ECOLOGICAL);
		for(int i = 0;i<slots.length;i++)
		{
			ItemStack equipStack = player.getItemBySlot(slots[i]); 
			level = container.getEnchantment(UniqueEnchantments.ECOLOGICAL, slots[i]);
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
		PlayerEntity player = event.getPlayer();
		if(player == null) return;
		List<ItemStack> all = new ObjectArrayList<>();
		List<ItemStack> ender = new ObjectArrayList<>();
		EquipmentSlotType[] slots = MiscUtil.getEquipmentSlotsFor(UniqueEnchantments.ENDER_MENDING);
		for(int i = 0;i<slots.length;i++)
		{
			ItemStack stack = player.getItemBySlot(slots[i]);
			if(stack.isEmpty()) continue;
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_MENDING, stack);
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
		ExperienceOrbEntity orb = event.getOrb();
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
			orb.remove();
			event.setCanceled(true);
			return;
		}
		usedXP += StackUtils.evenDistribute(totalXP - usedXP, orb.level.random, ender, (stack, i) -> {
			int max = MathHelper.ceil(EnderMending.LIMIT.get() * Math.pow(EnderMending.LIMIT_MULTIPLIER.get(), Math.sqrt(MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_MENDING, stack))));
			int stored = StackUtils.getInt(stack, EnderMending.ENDER_TAG, 0);
			int left = Math.min(i, max - stored);
			StackUtils.setInt(stack, EnderMending.ENDER_TAG, stored + left);
			return left;
		});
		int left = (totalXP - usedXP) / 2;
		player.take(event.getOrb(), 1);
		event.getOrb().remove();
		event.setCanceled(true);
		if(left > 0) player.giveExperiencePoints(left);
	}
	
	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		if(event.getPlayer() == null)
		{
			return;
		}
		PlayerEntity player = event.getPlayer();
		ItemStack held = player.getMainHandItem();
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.MOMENTUM, held);
		if(level > 0 && isMining(player))
		{
			CompoundNBT nbt = player.getPersistentData();
			long worldTime = player.level.getGameTime();
			long time = nbt.getLong(Momentum.LAST_MINE);
			double count = nbt.getDouble(Momentum.COUNT);
			if(worldTime - time > Momentum.MAX_DELAY.get() || worldTime < time)
			{
				count = 0;
				nbt.putDouble(Momentum.COUNT, count);
			}
			double flat = Math.log(1 + Momentum.SPEED.get(count)/Math.pow((1+event.getNewSpeed()), 0.25D));
			double percent = 1 + (Math.pow(Momentum.SPEED_MULTIPLIER.get(count), 0.55F)/level);
			event.setNewSpeed((float)((event.getNewSpeed() + flat) * percent));
			nbt.putLong(Momentum.LAST_MINE, worldTime);
		}
	}
	
	public boolean isMining(PlayerEntity player)
	{
		return !(player instanceof ServerPlayerEntity) || ((InteractionManagerMixin)((ServerPlayerEntity)player).gameMode).isMiningBlock();
	}
	
	@SubscribeEvent
	public void onBlockBreak(BreakEvent event)
	{
		if(event.getPlayer() == null)
		{
			return;
		}
		PlayerEntity player = event.getPlayer();
		ItemStack held = player.getMainHandItem();
		Object2IntMap<Enchantment> enchs = MiscUtil.getEnchantments(held);
		int level = enchs.getInt(UniqueEnchantments.ALCHEMISTS_GRACE);
		if(level > 0)
		{
			AlchemistsGrace.applyToEntity(event.getPlayer(), true, event.getState().getDestroySpeed(event.getWorld(), event.getPos()));
		}
		level = enchs.getInt(UniqueEnchantments.SMART_ASS);
		if(level > 0)
		{
			if(SmartAss.VALID_STATES.test(event.getState()))
			{
				Block block = event.getState().getBlock();
				int limit = SmartAss.RANGE.get(level);
				World world = (World)event.getWorld();
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
				if(lastState != null && MiscUtil.harvestBlock(event, lastState, lastPos))
				{
					event.setCanceled(true);
					return;
				}
			}
		}
		level = enchs.getInt(UniqueEnchantments.SAGES_BLESSING);
		if(level > 0)
		{
			level += (event.getWorld().getRandom().nextInt(enchs.getInt(Enchantments.BLOCK_FORTUNE)+1)+1);
			event.setExpToDrop((int)(event.getExpToDrop() + event.getExpToDrop() * SagesBlessing.XP_BOOST.get(level)));
		}
		level = enchs.getInt(UniqueEnchantments.MOMENTUM);
		if(level > 0)
		{
			double cap = Momentum.CAP.get() * Math.pow(Momentum.CAP_MULTIPLIER.get(level), 2);
			double extra = Math.min(1000, event.getState().getDestroySpeed(event.getWorld(), event.getPos())) * Math.pow(1 + ((level * level) / 100), 1+(level/100));
			CompoundNBT nbt = event.getPlayer().getPersistentData();
			nbt.putDouble(Momentum.COUNT, Math.min(nbt.getDouble(Momentum.COUNT) + extra, cap));
			if(!player.level.isClientSide) UniqueEnchantmentsBase.NETWORKING.sendToPlayer(new EntityPacket(player.getId(), nbt), player);
		}
	}
	
	@SubscribeEvent
	public void onItemClick(RightClickItem event)
	{
		ItemStack stack = event.getItemStack();
		if(!event.getWorld().isClientSide && stack.getItem() instanceof FilledMapItem && MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_LIBRARIAN, stack) > 0)
		{
			MapData data = FilledMapItem.getOrCreateSavedData(stack, event.getWorld());
			if(data == null || !data.dimension.location().equals(event.getWorld().dimension().location()))
			{
				return;
			}
			int x = data.x;
			int z = data.z;
			BlockPos position = null;
			CompoundNBT nbt = stack.getTag();
			boolean isBanner = false;
	        if (nbt != null)
	        {
	        	//Have to do it that way because Mine-craft decorations are rotated and its annoying to math that out properly.
	            ListNBT list = nbt.getList("Decorations", 10);
	            for(int i = 0,m=list.size();i<m;i++)
	            {
	                CompoundNBT nbtData = list.getCompound(i);
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
						position = banner.get(event.getWorld().random.nextInt(banner.size())).getPos();
						isBanner = true;
					}
	            }
	        }
	        if(position != null)
	        {
				BlockPos pos = event.getWorld().getHeightmapPos(Type.MOTION_BLOCKING, position);
				event.getPlayer().teleportTo(pos.getX() + 0.5F, Math.max(isBanner ? 0 : event.getWorld().getSeaLevel(), pos.getY() + 1), pos.getZ() + 0.5F);
	        }
	        else
	        {
		        int limit = 64 * (1 << data.scale) * 2;
		        int xOffset = (int)((event.getWorld().random.nextDouble() - 0.5D) * limit);
		        int zOffset = (int)((event.getWorld().random.nextDouble() - 0.5D) * limit);
				BlockPos pos = event.getWorld().getHeightmapPos(Type.MOTION_BLOCKING, new BlockPos(x + xOffset, 255, z + zOffset));
				event.getPlayer().teleportTo(pos.getX() + 0.5F, Math.max(event.getWorld().getSeaLevel(), pos.getY() + 1), pos.getZ() + 0.5F);
	        }
	        stack.shrink(1);
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
		Entity entity = event.getSource().getEntity();
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(base.getMainHandItem());
			int level = enchantments.getInt(UniqueEnchantments.SWIFT_BLADE);
			if(level > 0)
			{
				ModifiableAttributeInstance attr = base.getAttribute(Attributes.ATTACK_SPEED);
				if(attr != null)
				{
					event.setAmount(event.getAmount() * (float)Math.log10(10D + (1.6 + Math.log(Math.max(0.25D, attr.getValue())) / SwiftBlade.BASE_SPEED.get()) * MathCache.LOG.get(level*level)));
				}
			}
			level = enchantments.getInt(UniqueEnchantments.FOCUS_IMPACT);
			if(level > 0)
			{
				ModifiableAttributeInstance attr = base.getAttribute(Attributes.ATTACK_SPEED);
				if(attr != null)
				{
					event.setAmount(event.getAmount() * (1F + (float)Math.log10(Math.pow(FocusImpact.BASE_SPEED.get() / attr.getValue(), 2D)*MathCache.LOG.get(6+level))));
				}
			}
			level = MiscUtil.getEnchantedItem(UniqueEnchantments.CLIMATE_TRANQUILITY, base).getIntValue();
			if(level > 0)
			{
				Set<BiomeDictionary.Type> effects = BiomeDictionary.getTypes(RegistryKey.create(Registry.BIOME_REGISTRY, base.level.getBiome(base.blockPosition()).getRegistryName()));
				boolean hasHot = effects.contains(BiomeDictionary.Type.HOT) || effects.contains(BiomeDictionary.Type.NETHER);
				boolean hasCold = effects.contains(BiomeDictionary.Type.COLD);
				if(hasHot && !hasCold)
				{
					event.getEntityLiving().addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, ClimateTranquility.SLOW_TIME.get() * level, level));
				}
				else if(hasCold && !hasHot)
				{
					event.getEntityLiving().setSecondsOnFire(level * ClimateTranquility.BURN_TIME.get());
				}
			}
			if(event.getEntityLiving() instanceof AbstractSkeletonEntity)
			{
				level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.BONE_CRUSH, base.getMainHandItem());
				if(level > 0 && BoneCrusher.isNotArmored((AbstractSkeletonEntity)event.getEntityLiving()))
				{
					event.setAmount((float)(event.getAmount() * (1F + Math.log10(1F+BoneCrusher.BONUS_DAMAGE.getFloat(level)))));
				}
			}
			level = enchantments.getInt(UniqueEnchantments.BERSERKER);
			if(level > 0)
			{
				event.setAmount(event.getAmount() * (float)(1D + ((1-(Berserk.MIN_HEALTH.getMax(base.getHealth(), 1D)/base.getMaxHealth())) * Berserk.PERCENTUAL_DAMAGE.get() * MathCache.LOG10.get(level+1))));
			}
			level = enchantments.getInt(UniqueEnchantments.PERPETUAL_STRIKE);
			if(level > 0)
			{
				ItemStack held = base.getMainHandItem();
				int count = StackUtils.getInt(held, PerpetualStrike.HIT_COUNT, 0);
				int lastEntity = StackUtils.getInt(held, PerpetualStrike.HIT_ID, 0);
				if(lastEntity != event.getEntityLiving().getId())
				{
					count = 0;
					StackUtils.setInt(held, PerpetualStrike.HIT_ID, event.getEntityLiving().getId());
				}
				ModifiableAttributeInstance attr = base.getAttribute(Attributes.ATTACK_SPEED);
				float amount = event.getAmount();
				double damage = (1F + Math.pow(PerpetualStrike.PER_HIT.get(count)/Math.log(2.8D+(attr == null ? 1D : attr.getValue())), 1.4D)-1F)*level*PerpetualStrike.PER_HIT_LEVEL.get();
				double multiplier = Math.log10(10+(damage/Math.log10(1+event.getAmount())) * PerpetualStrike.MULTIPLIER.get());
				amount += damage;
				amount *= multiplier;
				event.setAmount(amount);
				StackUtils.setInt(held, PerpetualStrike.HIT_COUNT, count+1);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		Entity entity = event.getSource().getEntity();
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.SPARTAN_WEAPON, base.getMainHandItem());
			if(level > 0 && base.getOffhandItem().getItem() instanceof ShieldItem)
			{
				ModifiableAttributeInstance attr = base.getAttribute(Attributes.ATTACK_SPEED);
				if(attr != null)
				{
					event.setAmount((float)(event.getAmount() * (1D + SpartanWeapon.EXTRA_DAMAGE.getFloat()*Math.log((event.getAmount()*event.getAmount())/attr.getValue())*level)));
				}			
			}
			level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDEST_REAP, base.getMainHandItem());
			if(level > 0)
			{
				 event.setAmount(event.getAmount() + (EndestReap.BONUS_DAMAGE_LEVEL.getFloat(level) + EndestReap.REAP_MULTIPLIER.getFloat(level * base.getPersistentData().getInt(EndestReap.REAP_STORAGE))));
			}
		}
		if(event.getSource() == DamageSource.FLY_INTO_WALL)
		{
			ItemStack stack = event.getEntityLiving().getItemBySlot(EquipmentSlotType.CHEST);
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ICARUS_AEGIS, stack);
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
		if(event.getAmount() >= event.getEntityLiving().getHealth())
		{
			DamageSource source = event.getSource();
			if(!source.isMagic() && source != DamageSource.FALL)
			{
				ItemStack stack = event.getEntityLiving().getItemBySlot(EquipmentSlotType.CHEST);
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ARES_BLESSING, stack);
				if(level > 0 && stack.isDamageableItem())
				{
					float damage = event.getAmount();
					stack.hurtAndBreak((int)(damage * AresBlessing.BASE_DAMAGE.get() / MathCache.LOG.get(level+1)), event.getEntityLiving(), MiscUtil.get(EquipmentSlotType.CHEST));
					event.setCanceled(true);
					return;
				}	
			}
			Object2IntMap.Entry<EquipmentSlotType> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.PHOENIX_BLESSING, event.getEntityLiving());
			if(slot.getIntValue() > 0)
			{
				LivingEntity living = event.getEntityLiving();
				living.heal(living.getMaxHealth());
				living.removeAllEffects();
				if(living instanceof PlayerEntity)
				{
					((PlayerEntity)living).getFoodData().eat(Short.MAX_VALUE, 1F);
				}
				living.getPersistentData().putLong(DeathsOdium.CURSE_TIMER, living.getCommandSenderWorld().getGameTime() + DeathsOdium.DELAY.get());
                living.addEffect(new EffectInstance(Effects.REGENERATION, 600, 1));
                living.addEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
                living.level.broadcastEntityEvent(living, (byte)35);
                event.getEntityLiving().getItemBySlot(slot.getKey()).shrink(1);
				event.setCanceled(true);
	            for(LivingEntity entry : living.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(living.blockPosition()).inflate(PhoenixBlessing.RANGE.getAsDouble(slot.getIntValue()))))
	            {
	            	if(entry == living) continue;
	            	entry.setSecondsOnFire(600000);
	            }
			}
		}
		if(entity instanceof PlayerEntity)
		{
			CompoundNBT compound = entity.getPersistentData();
			if(compound.getLong(DeathsOdium.CURSE_TIMER) >= entity.level.getGameTime())
			{
				compound.putFloat(DeathsOdium.CURSE_DAMAGE, compound.getFloat(DeathsOdium.CURSE_DAMAGE)+event.getAmount());
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event)
	{
		ItemStack stack = event.getEntityLiving().getItemBySlot(EquipmentSlotType.CHEST);
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ICARUS_AEGIS, stack);
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
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.WARRIORS_GRACE, base.getMainHandItem());
			if(level > 0)
			{
				ItemStack stack = base.getMainHandItem();
				int amount = Math.min(stack.getDamageValue(), MathHelper.ceil(Math.sqrt(event.getEntityLiving().getMaxHealth() * level) * WarriorsGrace.DURABILITY_GAIN.get()));
				if(amount > 0)
				{
					stack.hurtAndBreak(-amount, base, MiscUtil.get(EquipmentSlotType.MAINHAND));
				}
			}
			Entity killed = event.getEntity();
			if(killed != null && base instanceof PlayerEntity)
			{
				int amount = EndestReap.isValid(killed);
				if(amount > 0)
				{
					level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDEST_REAP, base.getMainHandItem());
					if(level > 0)
					{
						CompoundNBT nbt = base.getPersistentData();
						nbt.putInt(EndestReap.REAP_STORAGE, Math.min(nbt.getInt(EndestReap.REAP_STORAGE)+amount, ((PlayerEntity)base).experienceLevel));
						StackUtils.setInt(base.getMainHandItem(), EndestReap.REAP_STORAGE, nbt.getInt(EndestReap.REAP_STORAGE));
					}
				}
			}
		}
		int maxLevel = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantments.DEATHS_ODIUM, event.getEntityLiving());
		if(maxLevel > 0)
		{
			for(EquipmentSlotType slot : EquipmentSlotType.values())
			{
				ItemStack stack = event.getEntityLiving().getItemBySlot(slot);
				if(MiscUtil.getEnchantmentLevel(UniqueEnchantments.DEATHS_ODIUM, stack) > 0)
				{
					StackUtils.setInt(stack, DeathsOdium.CURSE_STORAGE, Math.min(StackUtils.getInt(stack, DeathsOdium.CURSE_STORAGE, 0) + 1, DeathsOdium.MAX_STORAGE.get()));
					break;
				}
			}
			ModifiableAttributeInstance instance = event.getEntityLiving().getAttribute(Attributes.MAX_HEALTH);
			AttributeModifier mod = instance.getModifier(DeathsOdium.REMOVE_UUID);
			float toRemove = 0F;
			if(mod != null)
			{
				toRemove += mod.getAmount();
				instance.removeModifier(mod);
			}
			CompoundNBT nbt = event.getEntityLiving().getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
			if(nbt.getBoolean(DeathsOdium.CURSE_RESET))
			{
				nbt.remove(DeathsOdium.CURSE_RESET);
				nbt.remove(DeathsOdium.CURSE_STORAGE);
				for(EquipmentSlotType slot : EquipmentSlotType.values())
				{
					ItemStack stack = event.getEntityLiving().getItemBySlot(slot);
					if(MiscUtil.getEnchantmentLevel(UniqueEnchantments.DEATHS_ODIUM, stack) > 0)
					{
						stack.getTag().remove(DeathsOdium.CURSE_STORAGE);
					}
				}
				return;
			}
			event.getEntityLiving().getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, nbt);
			nbt.putFloat(DeathsOdium.CURSE_STORAGE, toRemove - (float)Math.ceil(Math.sqrt(DeathsOdium.BASE_LOSS.get(maxLevel))));
		}
	}
	
	@SubscribeEvent
	public void onRespawn(PlayerEvent.Clone event)
	{
		float f = event.getPlayer().getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG).getFloat(DeathsOdium.CURSE_STORAGE);
		if(f != 0F)
		{
			event.getEntityLiving().getAttribute(Attributes.MAX_HEALTH).addTransientModifier(new AttributeModifier(DeathsOdium.REMOVE_UUID, "odiums_curse", f, Operation.ADDITION));
		}
	}
	
	@SubscribeEvent
	public void onEaten(LivingEntityUseItemEvent.Finish event)
	{
		if(event.getItem().getItem() == Items.COOKIE && MiscUtil.getEnchantmentLevel(UniqueEnchantments.DEATHS_ODIUM, event.getItem()) > 0)
		{
			event.getEntityLiving().getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG).putBoolean(DeathsOdium.CURSE_RESET, true);
			event.getEntityLiving().kill();
		}
	}
	
	@SubscribeEvent
	public void onXPDrop(LivingExperienceDropEvent event)
	{
		if(event.getAttackingPlayer() == null)
		{
			return;
		}
		Object2IntMap.Entry<EquipmentSlotType> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.SAGES_BLESSING, event.getAttackingPlayer());
		int level = slot.getIntValue();
		if(level > 0)
		{
			level += (event.getAttackingPlayer().level.random.nextInt(MiscUtil.getEnchantmentLevel(Enchantments.MOB_LOOTING, event.getAttackingPlayer().getItemBySlot(slot.getKey()))+1));
			event.setDroppedExperience((int)(event.getDroppedExperience() + event.getDroppedExperience() * (SagesBlessing.XP_BOOST.get(level))));
		}
	}
	
	@SubscribeEvent
	public void onLootingLevel(LootingLevelEvent event)
	{
		if(event.getDamageSource() == null) return;
		Entity entity = event.getDamageSource().getEntity();
		if(entity instanceof LivingEntity && event.getEntityLiving() instanceof AbstractSkeletonEntity)
		{
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.BONE_CRUSH, ((LivingEntity)entity).getMainHandItem());
			if(level > 0 && BoneCrusher.isNotArmored((AbstractSkeletonEntity)event.getEntityLiving()))
			{
				event.setLootingLevel((event.getLootingLevel() + 1) + level);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDrops(LivingDropsEvent event)
	{
		Entity entity = event.getSource().getEntity();
		if(entity instanceof PlayerEntity && event.getEntityLiving() instanceof AnimalEntity)
		{
			PlayerEntity base = (PlayerEntity)entity;
			Object2IntMap.Entry<EquipmentSlotType> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.FAST_FOOD, base);
			int level = slot.getIntValue();
			if(level > 0)
			{
				int looting = base.level.random.nextInt(1+MiscUtil.getEnchantmentLevel(Enchantments.MOB_LOOTING, base.getItemBySlot(slot.getKey())));
				int burning = event.getEntityLiving().isOnFire() ? 2 : 1;
				base.getFoodData().eat(FastFood.NURISHMENT.get(level+looting) * burning, FastFood.SATURATION.getFloat(level+looting) * burning);
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onArrowHit(ProjectileImpactEvent.Arrow event)
	{
		RayTraceResult result = event.getRayTraceResult();
		if(!(result instanceof EntityRayTraceResult))
		{
			return;
		}
		AbstractArrowEntity arrow = event.getArrow();
		Entity shooter = arrow.getOwner();
		AlchemistsGrace.applyToEntity(shooter, false, 1.5F);
		if(shooter instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)shooter;
			Object2IntMap.Entry<EquipmentSlotType> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.ENDERMARKSMEN, player);
			if(slot.getIntValue() > 0)
			{
				int level = slot.getIntValue();
				ItemStack stack = player.getItemBySlot(slot.getKey());
				arrow.pickup = PickupStatus.DISALLOWED;
				player.addItem(StackUtils.getArrowStack(arrow));
				int needed = Math.min(MathHelper.floor(MathCache.LOG_ADD.get(level)*EnderMarksmen.EXTRA_DURABILITY.get()), stack.getDamageValue());
				if(needed > 0)
				{
					stack.hurtAndBreak(-needed, player, MiscUtil.get(slot.getKey()));
				}
				Entity entity = ((EntityRayTraceResult)result).getEntity();
				if(entity instanceof EndermanEntity)
				{
					ENDER_MEN_TELEPORT.set(entity.getUUID());
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEndermenTeleport(EnderTeleportEvent event)
	{
		UUID id = ENDER_MEN_TELEPORT.get();
		if(event.getEntity().getUUID().equals(id))
		{
			ENDER_MEN_TELEPORT.set(null);
			event.setCanceled(true);
		}
		LivingEntity living = event.getEntityLiving();
		ModifiableAttributeInstance attri = living.getAttribute(Attributes.FOLLOW_RANGE);
        if(living.getCommandSenderWorld().getNearestPlayer(new EntityPredicate().range(attri == null ? 16.0D : attri.getValue()).selector(EnderEyes.getPlayerFilter(living)), living) != null)
        {
        	event.setCanceled(true);
        }
	}
	
	@SubscribeEvent
	public void onEquippementSwapped(LivingEquipmentChangeEvent event)
	{
		AttributeModifierManager attribute = event.getEntityLiving().getAttributes();
		Multimap<Attribute, AttributeModifier> mods = createModifiersFromStack(event.getFrom(), event.getEntityLiving(), event.getSlot());
		if(!mods.isEmpty())
		{
			attribute.removeAttributeModifiers(mods);
		}
		mods = createModifiersFromStack(event.getTo(), event.getEntityLiving(), event.getSlot());
		if(!mods.isEmpty())
		{
			attribute.addTransientAttributeModifiers(mods);
		}
	}
	
	private Multimap<Attribute, AttributeModifier> createModifiersFromStack(ItemStack stack, LivingEntity living, EquipmentSlotType slot)
	{
		Multimap<Attribute, AttributeModifier> mods = HashMultimap.create();
		//Optimization. After 3 Enchantment's its sure that on average you have more then 1 full iteration. So now we fully iterate once over it since hash-code would be a faster check.
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		int level = enchantments.getInt(UniqueEnchantments.VITAE);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.VITAE).contains(slot))
		{
			int xpLevel = living instanceof PlayerEntity ? ((PlayerEntity)living).experienceLevel : 100;
			mods.put(Attributes.MAX_HEALTH, new AttributeModifier(Vitae.getForSlot(slot), "Vitae Boost", Math.log10(100+(Vitae.BASE_BOOST.get(level))+Math.sqrt(Vitae.SCALE_BOOST.get(xpLevel)))-2, Operation.MULTIPLY_TOTAL));
		}
		level = enchantments.getInt(UniqueEnchantments.SWIFT);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.SWIFT).contains(slot))
		{
			mods.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(Swift.SPEED_MOD, "Swift Boost", Swift.SPEED_BONUS.getAsDouble(level), Operation.MULTIPLY_TOTAL));
		}
		level = enchantments.getInt(UniqueEnchantments.RANGE);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.RANGE).contains(slot))
		{
			mods.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(Range.RANGE_MOD, "Range Boost", Range.RANGE.getAsFloat(level), Operation.ADDITION));
		}
		level = enchantments.getInt(UniqueEnchantments.DEATHS_ODIUM);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.DEATHS_ODIUM).contains(slot))
		{
			int value = StackUtils.getInt(stack, DeathsOdium.CURSE_STORAGE, 0);
			if(value > 0)
			{
				mods.put(Attributes.MAX_HEALTH, new AttributeModifier(DeathsOdium.getForSlot(slot), "Death Odiums Restore", DeathsOdium.BASE_LOSS.get(value), Operation.ADDITION));
			}
		}
		return mods;
	}
}
