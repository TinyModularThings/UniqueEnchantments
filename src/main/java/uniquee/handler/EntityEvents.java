package uniquee.handler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.storage.MapBanner;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniquee.UniqueEnchantments;
import uniquee.api.crops.CropHarvestRegistry;
import uniquee.enchantments.complex.EnderMendingEnchantment;
import uniquee.enchantments.complex.MomentumEnchantment;
import uniquee.enchantments.complex.PerpetualStrikeEnchantment;
import uniquee.enchantments.complex.SmartAssEnchantment;
import uniquee.enchantments.complex.SpartanWeaponEnchantment;
import uniquee.enchantments.complex.SwiftBladeEnchantment;
import uniquee.enchantments.curse.DeathsOdiumEnchantment;
import uniquee.enchantments.curse.PestilencesOdiumEnchantment;
import uniquee.enchantments.simple.BerserkEnchantment;
import uniquee.enchantments.simple.BoneCrusherEnchantment;
import uniquee.enchantments.simple.FocusImpactEnchantment;
import uniquee.enchantments.simple.RangeEnchantment;
import uniquee.enchantments.simple.SagesBlessingEnchantment;
import uniquee.enchantments.simple.SwiftEnchantment;
import uniquee.enchantments.simple.VitaeEnchantment;
import uniquee.enchantments.unique.AlchemistsGraceEnchantment;
import uniquee.enchantments.unique.AresBlessingEnchantment;
import uniquee.enchantments.unique.ClimateTranquilityEnchantment;
import uniquee.enchantments.unique.CloudwalkerEnchantment;
import uniquee.enchantments.unique.DemetersSoulEnchantment;
import uniquee.enchantments.unique.EcologicalEnchantment;
import uniquee.enchantments.unique.EnderMarksmenEnchantment;
import uniquee.enchantments.unique.FastFoodEnchantment;
import uniquee.enchantments.unique.IcarusAegisEnchantment;
import uniquee.enchantments.unique.NaturesGraceEnchantment;
import uniquee.enchantments.unique.WarriorsGraceEnchantment;
import uniquee.handler.ai.SpecialFindPlayerAI;
import uniquee.utils.HarvestEntry;
import uniquee.utils.MiscUtil;
import uniquee.utils.Triple;

public class EntityEvents
{
	public static final Method ARROW_STACK = MiscUtil.findMethod(AbstractArrowEntity.class, new String[]{"getArrowStack", "func_184550_j"});
	public static final EntityEvents INSTANCE = new EntityEvents();
	List<Tuple<Enchantment, String[]>> tooltips = new ObjectArrayList<Tuple<Enchantment, String[]>>();
	List<Triple<Enchantment, ToIntFunction<ItemStack>, String>> anvilHelpers = new ObjectArrayList<Triple<Enchantment, ToIntFunction<ItemStack>, String>>();
	static final ThreadLocal<UUID> ENDER_MEN_TELEPORT = new ThreadLocal<>();
	
	public void registerStorageTooltip(Enchantment ench, String translation, String tag)
	{
		tooltips.add(new Tuple<Enchantment, String[]>(ench, new String[]{translation, tag}));
	}
	
	public void registerAnvilHelper(Enchantment ench, ToIntFunction<ItemStack> helper, String tag)
	{
		anvilHelpers.add(Triple.create(ench, helper, tag));
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onToolTipEvent(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		for(int i = 0,m=tooltips.size();i<m;i++)
		{
			Tuple<Enchantment, String[]> entry = tooltips.get(i);
			if(enchantments.getInt(entry.getA()) > 0)
			{
				String[] names = entry.getB();
				event.getToolTip().add(new TranslationTextComponent(names[0], getInt(stack, names[1], 0)).mergeStyle(TextFormatting.GOLD));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		Entity entity = event.getEntity();
		if(entity instanceof EndermanEntity)
		{
			GoalSelector goals = ((EndermanEntity)entity).targetSelector;
			for(PrioritizedGoal goal : new ObjectArrayList<PrioritizedGoal>(MiscUtil.findField(GoalSelector.class, goals, Set.class, "goals", "field_220892_d")))
			{
				if(goal.getPriority() == 1 && goal.getGoal() instanceof NearestAttackableTargetGoal)
				{
					goals.removeGoal(goal.getGoal());
					goals.addGoal(1, new SpecialFindPlayerAI((EndermanEntity)entity));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityUpdate(PlayerTickEvent event)
	{
		if(event.phase == Phase.START)
		{
			return;
		}
		PlayerEntity player = event.player;
		if(event.side.isServer())
		{
			if(player.getHealth() < player.getMaxHealth())
			{
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.NATURES_GRACE, player.getItemStackFromSlot(EquipmentSlotType.CHEST));
				if(level > 0 && player.world.getGameTime() % Math.max(1, MathHelper.floor(Math.sqrt(NaturesGraceEnchantment.DELAY.get() / level))) == 0)
				{
					if(player.getCombatTracker().getBestAttacker() == null && hasBlockCount(player.world, player.getPosition(), 4, NaturesGraceEnchantment.FLOWERS))
					{
						player.heal(NaturesGraceEnchantment.HEALING.getAsFloat(level));
					}
				}
			}
			if(player.world.getGameTime() % 100 == 0)
			{
				EquipmentSlotType[] slots = MiscUtil.getEquipmentSlotsFor(UniqueEnchantments.ENDER_MENDING);
				for(int i = 0;i<slots.length;i++)
				{
					ItemStack stack = player.getItemStackFromSlot(slots[i]);
					int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_MENDING, stack);
					if(level > 0 && stack.isDamaged())
					{
						int stored = getInt(stack, EnderMendingEnchantment.ENDER_TAG, 0);
						if(stored > 0)
						{
							int toRemove = Math.min(stack.getDamage(), stored);
							stack.setDamage(stack.getDamage() - toRemove);
							setInt(stack, EnderMendingEnchantment.ENDER_TAG, stored - toRemove);
						}
					}
				}
			}
			if(player.world.getGameTime() % 30 == 0)
			{
				ClimateTranquilityEnchantment.onClimate(player);
			}
			if(player.world.getGameTime() % 10 == 0)
			{
				ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ICARUS_AEGIS, stack);
				if(level > 0)
				{
					stack.getTag().putBoolean(IcarusAegisEnchantment.FLYING_TAG, player.isElytraFlying());
				}
			}
			if(player.world.getGameTime() % 40 == 0)
			{
				int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantments.PESTILENCES_ODIUM, player);
				if(level > 0)
				{
					List<AgeableEntity> living = player.world.getEntitiesWithinAABB(AgeableEntity.class, new AxisAlignedBB(player.getPosition()).grow(PestilencesOdiumEnchantment.RADIUS.get()));
					for(int i = 0,m=living.size();i<m;i++)
					{
						living.get(i).addPotionEffect(new EffectInstance(UniqueEnchantments.PESTILENCES_ODIUM_POTION, 200, level));
					}
				}
			}
			if(player.world.getGameTime() % 20 == 0)
			{
				HarvestEntry entry = DemetersSoulEnchantment.getNextIndex(player);
				if(entry != null)
				{
					ActionResultType result = entry.harvest(player.world, player);
					if(result == ActionResultType.FAIL)
					{
						ListNBT list = DemetersSoulEnchantment.getCrops(player);
						for(int i = 0,m=list.size();i<m;i++)
						{
							if(entry.matches(list.getCompound(i)))
							{
								list.remove(i--);
								break;
							}
						}
					}
					else if(result == ActionResultType.SUCCESS)
					{
						player.addExhaustion(0.06F);
					}
				}
				if(isMining(player))
				{
					int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ALCHEMISTS_GRACE, player.getHeldItemMainhand());
					if(level > 0)
					{
						AlchemistsGraceEnchantment.applyToEntity(player);
					}
				}
				Object2IntMap.Entry<EquipmentSlotType> level = MiscUtil.getEnchantedItem(UniqueEnchantments.SAGES_BLESSING, player);
				if(level.getIntValue() > 0)
				{
					player.addExhaustion(0.01F * level.getIntValue());
				}
			}
			CompoundNBT data = event.player.getPersistentData();
			if(data.contains(DeathsOdiumEnchantment.CURSE_DAMAGE) && data.getLong(DeathsOdiumEnchantment.CRUSE_TIMER) < event.player.world.getGameTime())
			{
				int total = MathHelper.floor(data.getFloat(DeathsOdiumEnchantment.CURSE_DAMAGE) / DeathsOdiumEnchantment.DAMAGE_FACTOR.get());
				if(total > 0)
				{
					ModifiableAttributeInstance instance = event.player.getAttribute(Attributes.MAX_HEALTH);
					AttributeModifier mod = instance.getModifier(DeathsOdiumEnchantment.REMOVE_UUID);
					if(mod != null)
					{
						double newValue = Math.max(0D, mod.getAmount() - total);
						instance.removeModifier(mod);
						if(newValue > 0)
						{
							instance.applyPersistentModifier(new AttributeModifier(DeathsOdiumEnchantment.REMOVE_UUID, "odiums_curse", newValue, Operation.ADDITION));
						}
					}
				}	
				data.remove(DeathsOdiumEnchantment.CURSE_DAMAGE);
			}
		}
		ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.FEET);
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.CLOUD_WALKER, stack);
		if(level > 0)
		{
			CompoundNBT nbt = player.getPersistentData();
			if(player.isSneaking() && !nbt.getBoolean(CloudwalkerEnchantment.TRIGGER) && (!player.isOnGround() || nbt.getBoolean(CloudwalkerEnchantment.ENABLED)))
			{
				nbt.putBoolean(CloudwalkerEnchantment.ENABLED, !nbt.getBoolean(CloudwalkerEnchantment.ENABLED));
				nbt.putBoolean(CloudwalkerEnchantment.TRIGGER, true);
			}
			else if(!player.isSneaking())
			{
				nbt.putBoolean(CloudwalkerEnchantment.TRIGGER, false);
			}
			if(nbt.getBoolean(CloudwalkerEnchantment.ENABLED))
			{
				int value = getInt(stack, CloudwalkerEnchantment.TIMER, CloudwalkerEnchantment.TICKS.get(level));
				if(value <= 0)
				{
					nbt.putBoolean(CloudwalkerEnchantment.ENABLED, false);
					return;
				}
				Vector3d vec = player.getMotion();
				player.setMotion(vec.x, player.abilities.isFlying ? 0.15D : 0D, vec.z);
				player.onLivingFall(player.fallDistance, 1F);
				player.fallDistance = 0F;
				if(!player.isCreative())
				{
					setInt(stack, CloudwalkerEnchantment.TIMER, value-1);
					if(player.world.getGameTime() % 20 == 0)
					{
						stack.damageItem(1, player, MiscUtil.get(EquipmentSlotType.FEET));
					}
				}
			}
			else
			{
				setInt(stack, CloudwalkerEnchantment.TIMER, CloudwalkerEnchantment.TICKS.get(level));
			}
		}
		level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.SWIFT, player.getItemStackFromSlot(EquipmentSlotType.LEGS));
		if(level > 0 && player.isOnLadder() && player.moveForward != 0F && player.getMotion().getY() > 0 && player.getMotion().getY() <= 0.2 && !player.isSneaking())
		{
			player.setMotion(player.getMotion().add(0, SwiftEnchantment.SPEED_BONUS.getAsDouble(level) * 3D, 0));
		}
		Boolean cache = null;
		//Reflection is slower then direct call. But Twice the Iteration & Double IsEmpty Check is slower then Reflection.
		EquipmentSlotType[] slots = MiscUtil.getEquipmentSlotsFor(UniqueEnchantments.ECOLOGICAL);
		for(int i = 0;i<slots.length;i++)
		{
			ItemStack equipStack = player.getItemStackFromSlot(slots[i]); 
			level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ECOLOGICAL, equipStack);
			if(level > 0 && equipStack.isDamaged() && player.world.getGameTime() % Math.max(1, (int)(EcologicalEnchantment.SPEED.get() / Math.sqrt(level / EcologicalEnchantment.SCALE.get()))) == 0)
			{
				if((cache == null ? cache = hasBlockCount(player.world, player.getPosition(), 1, EcologicalEnchantment.STATES) : cache.booleanValue()))
				{
					equipStack.damageItem(-1, player, MiscUtil.get(slots[i]));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onXPPickup(PickupXp event)
	{
		PlayerEntity player = event.getPlayer();
		if(player == null)
		{
			return;
		}
		int maxLevel = 0;
		Object2BooleanMap<ItemStack> values = new Object2BooleanLinkedOpenHashMap<ItemStack>();
		int foundItems = 0;
		EquipmentSlotType[] slots = MiscUtil.getEquipmentSlotsFor(UniqueEnchantments.ENDER_MENDING);
		for(int i = 0;i<slots.length;i++)
		{
			ItemStack stack = player.getItemStackFromSlot(slots[i]);
			if(stack.isEmpty())
			{
				continue;
			}
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_MENDING, stack);
			if(level > 0)
			{
				values.put(stack, true);
				foundItems++;
				maxLevel = Math.max(maxLevel, level);
			}
			else if(stack.isDamaged() && MiscUtil.getEnchantmentLevel(Enchantments.MENDING, stack) > 0)
			{
				values.put(stack, false);
			}
		}
		if(values.isEmpty() || foundItems <= 0)
		{
			return;
		}
		int xp = event.getOrb().xpValue;
		int totalXP = (int)((xp * 1F - EnderMendingEnchantment.ABSORBTION_RATIO.getAsFloat(maxLevel)) * 2);
		xp -= (totalXP / 2);
		int usedXP = 0;
		for(Object2BooleanMap.Entry<ItemStack> entry : values.object2BooleanEntrySet())
		{
			ItemStack stack = entry.getKey();
			if(stack.isDamaged())
			{
				int used = Math.min(totalXP - usedXP, stack.getDamage());
                stack.setDamage(stack.getDamage() - used);
                usedXP += used;
                if(usedXP >= totalXP)
                {
                	break;
                }
			}
		}
		if(totalXP <= usedXP)
		{
			player.onItemPickup(event.getOrb(), 1);
			event.getOrb().remove();
			event.setCanceled(true);
			player.giveExperiencePoints(xp);
			return;
		}
		totalXP -= usedXP;
		usedXP = 0;
		int perItem = Math.max(1, totalXP / foundItems);
		for(Object2BooleanMap.Entry<ItemStack> entry : values.object2BooleanEntrySet())
		{
			if(entry.getBooleanValue())
			{
				ItemStack stack = entry.getKey();
				int stored = getInt(stack, EnderMendingEnchantment.ENDER_TAG, 0);
				int left = Math.min(Math.min(totalXP - usedXP, perItem), EnderMendingEnchantment.LIMIT.get() - stored);
				if(left <= 0)
				{
					continue;
				}
				usedXP+=left;
				setInt(stack, EnderMendingEnchantment.ENDER_TAG, stored + left);
			}
		}
		perItem = totalXP - usedXP;
		if(perItem > 0)
		{
			player.giveExperiencePoints(perItem / 2);
		}
		player.onItemPickup(event.getOrb(), 1);
		event.getOrb().remove();
		event.setCanceled(true);
		player.giveExperiencePoints(xp);
	}
	
	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		if(event.getPlayer() == null)
		{
			return;
		}
		PlayerEntity player = event.getPlayer();
		ItemStack held = player.getHeldItemMainhand();
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.MOMENTUM, held);
		if(level > 0 && isMining(player))
		{
			CompoundNBT nbt = player.getPersistentData();
			long worldTime = player.world.getGameTime();
			long time = nbt.getLong(MomentumEnchantment.LAST_MINE);
			int count = getInt(held, MomentumEnchantment.COUNT, 0);
			if(worldTime - time > MomentumEnchantment.MAX_DELAY.get() || worldTime < time)
			{
				count = 0;
				setInt(held, MomentumEnchantment.COUNT, 0);
			}
			event.setNewSpeed(event.getNewSpeed() + (event.getNewSpeed() * (level * (count / MomentumEnchantment.SCALAR.getFloat()) / event.getOriginalSpeed())));
			nbt.putLong(MomentumEnchantment.LAST_MINE, worldTime);
		}
		level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.RANGE, held);
		if(level > 0)
		{
			double value = player.getBaseAttributeValue(ForgeMod.REACH_DISTANCE.get());
			if(value * value < player.getDistanceSq(Vector3d.copy(event.getPos()).add(0.5D, 0.5D, 0.5D)))
			{
				event.setNewSpeed(event.getNewSpeed() * (1F - RangeEnchantment.REDUCTION.getDevided(level)));
			}
		}
	}
	
	public boolean isMining(PlayerEntity player)
	{
		if(player instanceof ServerPlayerEntity)
		{
			return MiscUtil.findField(PlayerInteractionManager.class, ((ServerPlayerEntity)player).interactionManager, Boolean.class, "isDestroyingBlock", "field_73088_d");
		}
		return true;
	}
	
	@SubscribeEvent
	public void onBlockBreak(BreakEvent event)
	{
		if(event.getPlayer() == null)
		{
			return;
		}
		ItemStack held = event.getPlayer().getHeldItemMainhand();
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.SMART_ASS, held);
		if(level > 0)
		{
			if(SmartAssEnchantment.VALID_STATES.test(event.getState()))
			{
				Block block = event.getState().getBlock();
				int limit = SmartAssEnchantment.STATS.get(level);
				World world = (World)event.getWorld();
				BlockState lastState = null;
				BlockPos lastPos = null;
				for(int i = 1;i<limit;i++)
				{
					BlockPos pos = event.getPos().up(i);
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
		level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.SAGES_BLESSING, held);
		if(level > 0)
		{
			event.setExpToDrop((int)(event.getExpToDrop() + event.getExpToDrop() * (level * SagesBlessingEnchantment.XP_BOOST.get())));
		}
		level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.MOMENTUM, held);
		if(level > 0)
		{
			int count = getInt(held, MomentumEnchantment.COUNT, 0);
			int old = count;
			count = Math.min(MomentumEnchantment.CAP.get() + level, count+1);
			if(count != old)
			{
				setInt(held, MomentumEnchantment.COUNT, count);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void onItemClick(RightClickItem event)
	{
		ItemStack stack = event.getItemStack();
		if(!event.getWorld().isRemote && stack.getItem() instanceof FilledMapItem && MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_LIBRARIAN, stack) > 0)
		{
			MapData data = FilledMapItem.getMapData(stack, event.getWorld());
			if(data == null || !data.dimension.getLocation().equals(event.getWorld().getDimensionKey().getLocation()))
			{
				return;
			}
			int x = data.xCenter;
			int z = data.zCenter;
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
					List<MapBanner> banner = new ObjectArrayList<MapBanner>(MiscUtil.findField(MapData.class, data, Map.class, "banners", "field_204270_k").values());
					if(banner.size() > 0)
					{
						position = banner.get(event.getWorld().rand.nextInt(banner.size())).getPos();
						isBanner = true;
					}
	            }
	        }
	        if(position != null)
	        {
				BlockPos pos = event.getWorld().getHeight(Type.MOTION_BLOCKING, position);
				event.getPlayer().setPositionAndUpdate(pos.getX() + 0.5F, Math.max(isBanner ? 0 : event.getWorld().getSeaLevel(), pos.getY() + 1), pos.getZ() + 0.5F);
	        }
	        else
	        {
		        int limit = 64 * (1 << data.scale) * 2;
		        int xOffset = (int)((event.getWorld().rand.nextDouble() - 0.5D) * limit);
		        int zOffset = (int)((event.getWorld().rand.nextDouble() - 0.5D) * limit);
				BlockPos pos = event.getWorld().getHeight(Type.MOTION_BLOCKING, new BlockPos(x + xOffset, 255, z + zOffset));
				event.getPlayer().setPositionAndUpdate(pos.getX() + 0.5F, Math.max(event.getWorld().getSeaLevel(), pos.getY() + 1), pos.getZ() + 0.5F);
	        }
	        stack.shrink(1);
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getPlayer().isSneaking())
		{
			BlockState state = event.getWorld().getBlockState(event.getPos());
			if(state.getBlock() instanceof AnvilBlock)
			{
				ItemStack stack = event.getItemStack();
				Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
				for(int i = 0,m=anvilHelpers.size();i<m;i++)
				{
					Triple<Enchantment, ToIntFunction<ItemStack>, String> entry = anvilHelpers.get(i);
					if(enchantments.getInt(entry.getKey()) > 0)
					{
						int found = consumeItems(event.getPlayer(), entry.getValue(), Integer.MAX_VALUE);
						if(found > 0)
						{
							setInt(stack, entry.getExtra(), found + getInt(stack, entry.getExtra(), 0));
							event.setCancellationResult(ActionResultType.SUCCESS);
							event.setCanceled(true);
						}
					}
				}
			}
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.DEMETERS_SOUL, event.getItemStack());
			if(level > 0 && CropHarvestRegistry.INSTANCE.isValid(state.getBlock()) && !event.getWorld().isRemote)
			{
				HarvestEntry entry = new HarvestEntry(event.getWorld().getDimensionKey().getLocation(), event.getPos().toLong());
				ListNBT list = DemetersSoulEnchantment.getCrops(event.getPlayer());
				boolean found = false;
				for(int i = 0,m=list.size();i<m;i++)
				{
					if(entry.matches(list.getCompound(i)))
					{
						found = true;
						list.remove(i--);
						break;
					}
				}
				if(!found)
				{
					list.add(entry.save());
				}
				event.getPlayer().sendStatusMessage(new TranslationTextComponent("tooltip.uniqee.crops."+(found ? "removed" : "added")+".name"), false);
				event.setCancellationResult(ActionResultType.SUCCESS);
				event.setCanceled(true);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityHit(LivingAttackEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		AlchemistsGraceEnchantment.applyToEntity(entity);
	}
	
	@SubscribeEvent
	public void onEntityAttack(LivingHurtEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(base.getHeldItemMainhand());
			if(enchantments.getInt(UniqueEnchantments.BERSERKER) > 0)
			{
				event.setAmount(event.getAmount() * (1F + (BerserkEnchantment.SCALAR.getFloat() * (base.getMaxHealth() / Math.max(1F, base.getHealth())))));
			}
			int level = enchantments.getInt(UniqueEnchantments.SWIFT_BLADE);
			if(level > 0)
			{
				ModifiableAttributeInstance attr = base.getAttribute(Attributes.ATTACK_SPEED);
				if(attr != null)
				{
					event.setAmount(event.getAmount() * (1F + (float)Math.log10(attr.getValue() / SwiftBladeEnchantment.SCALAR.get() * level)));
				}
			}
			level = enchantments.getInt(UniqueEnchantments.FOCUS_IMPACT);
			if(level > 0)
			{
				ModifiableAttributeInstance attr = base.getAttribute(Attributes.ATTACK_SPEED);
				if(attr != null)
				{
					event.setAmount(event.getAmount() * (1F + (float)Math.log10(FocusImpactEnchantment.DAMAGE.get() * level / (attr.getValue() * attr.getValue()))));
				}
			}
			level = enchantments.getInt(UniqueEnchantments.PERPETUAL_STRIKE);
			if(level > 0)
			{
				ItemStack held = base.getHeldItemMainhand();
				int count = getInt(held, PerpetualStrikeEnchantment.HIT_COUNT, 0);
				int lastEntity = getInt(held, PerpetualStrikeEnchantment.HIT_ID, 0);
				if(lastEntity != event.getEntityLiving().getEntityId())
				{
					count = 0;
					setInt(held, PerpetualStrikeEnchantment.HIT_ID, event.getEntityLiving().getEntityId());
				}
				event.setAmount(event.getAmount() * (1F + (level * count * PerpetualStrikeEnchantment.SCALAR.getFloat())));
				setInt(held, PerpetualStrikeEnchantment.HIT_COUNT, count+1);
			}
			level = MiscUtil.getEnchantedItem(UniqueEnchantments.CLIMATE_TRANQUILITY, base).getIntValue();
			if(level > 0)
			{
				Set<BiomeDictionary.Type> effects = BiomeDictionary.getTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, base.world.getBiome(base.getPosition()).getRegistryName()));
				boolean hasHot = effects.contains(BiomeDictionary.Type.HOT) || effects.contains(BiomeDictionary.Type.NETHER);
				boolean hasCold = effects.contains(BiomeDictionary.Type.COLD);
				if(hasHot && !hasCold)
				{
					event.getEntityLiving().addPotionEffect(new EffectInstance(Effects.SLOWNESS, ClimateTranquilityEnchantment.SLOW_TIME.get() * level, level));
				}
				else if(hasCold && !hasHot)
				{
					event.getEntityLiving().setFire(level * ClimateTranquilityEnchantment.BURN_TIME.get());
				}
			}
			if(event.getEntityLiving() instanceof AbstractSkeletonEntity)
			{
				level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.BONE_CRUSH, base.getHeldItemMainhand());
				if(level > 0 && BoneCrusherEnchantment.isNotArmored((AbstractSkeletonEntity)event.getEntityLiving()))
				{
					event.setAmount(event.getAmount() * (1F + (BoneCrusherEnchantment.SCALAR.getFloat() * level)));
				}
			}
		}
		if(entity instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)entity;
			ItemStack stack = player.getHeldItemMainhand();
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.RANGE, stack);
			if(level > 0)
			{
				double value = player.getBaseAttributeValue(ForgeMod.REACH_DISTANCE.get());
				if(value * value < player.getDistanceSq(event.getEntity()))
				{
					event.setAmount(event.getAmount() * (1F - RangeEnchantment.REDUCTION.getDevided(level)));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.SPARTAN_WEAPON, base.getHeldItemMainhand());
			if(level > 0 && base.getHeldItemOffhand().getItem() instanceof ShieldItem)
			{
				event.setAmount(event.getAmount() + (event.getAmount() * (SpartanWeaponEnchantment.SCALAR.getFloat() * level)));
			}
		}
		if(event.getSource() == DamageSource.FLY_INTO_WALL)
		{
			ItemStack stack = event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.CHEST);
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ICARUS_AEGIS, stack);
			if(level > 0)
			{
				int feathers = getInt(stack, IcarusAegisEnchantment.FEATHER_TAG, 0);
				int consume = (int)Math.ceil((double)IcarusAegisEnchantment.SCALAR.get() / level);
				if(feathers >= consume)
				{
					feathers -= consume;
					setInt(stack, IcarusAegisEnchantment.FEATHER_TAG, feathers);
					event.setCanceled(true);
					return;
				}
			}
		}
		if(event.getAmount() >= event.getEntityLiving().getHealth())
		{
			DamageSource source = event.getSource();
			if(!source.isMagicDamage() && source != DamageSource.FALL)
			{
				ItemStack stack = event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.CHEST);
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ARES_BLESSING, stack);
				if(level > 0 && stack.isDamageable())
				{
					float damage = event.getAmount();
					stack.damageItem((int)(damage * (AresBlessingEnchantment.SCALAR.get() / level)), event.getEntityLiving(), MiscUtil.get(EquipmentSlotType.CHEST));
					event.setCanceled(true);
					return;
				}	
			}
			Object2IntMap.Entry<EquipmentSlotType> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.PHOENIX_BLESSING, event.getEntityLiving());
			if(slot.getIntValue() > 0)
			{
				LivingEntity living = event.getEntityLiving();
				living.heal(living.getMaxHealth());
				living.clearActivePotions();
				if(living instanceof PlayerEntity)
				{
					((PlayerEntity)living).getFoodStats().addStats(Short.MAX_VALUE, 1F);
				}
				living.getPersistentData().putLong(DeathsOdiumEnchantment.CRUSE_TIMER, living.getEntityWorld().getGameTime() + DeathsOdiumEnchantment.DELAY.get());
                living.addPotionEffect(new EffectInstance(Effects.REGENERATION, 600, 1));
                living.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
                living.world.setEntityState(living, (byte)35);
                event.getEntityLiving().getItemStackFromSlot(slot.getKey()).shrink(1);
				event.setCanceled(true);
			}
		}
		if(entity instanceof MobEntity)
		{
			CompoundNBT compound = entity.getPersistentData();
			if(compound.getLong(DeathsOdiumEnchantment.CRUSE_TIMER) >= entity.world.getGameTime())
			{
				compound.putFloat(DeathsOdiumEnchantment.CURSE_DAMAGE, compound.getFloat(DeathsOdiumEnchantment.CURSE_DAMAGE)+event.getAmount());
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event)
	{
		ItemStack stack = event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.CHEST);
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ICARUS_AEGIS, stack);
		if(level > 0 && stack.getTag().getBoolean(IcarusAegisEnchantment.FLYING_TAG) && event.getDistance() > 3F)
		{
			int feathers = getInt(stack, IcarusAegisEnchantment.FEATHER_TAG, 0);
			int consume = (int)Math.max(Math.ceil((double)IcarusAegisEnchantment.SCALAR.get() / level), 4);
			if(feathers >= consume)
			{
				feathers -= consume;
				setInt(stack, IcarusAegisEnchantment.FEATHER_TAG, feathers);
				event.setCanceled(true);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityKilled(LivingDeathEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.WARRIORS_GRACE, base.getHeldItemMainhand());
			if(level > 0)
			{
				ItemStack stack = base.getHeldItemMainhand();
				int amount = Math.min(stack.getDamage(), MathHelper.ceil(Math.sqrt(event.getEntityLiving().getMaxHealth() * level) * WarriorsGraceEnchantment.DURABILITY_GAIN.get()));
				if(amount > 0)
				{
					stack.damageItem(-amount, base, MiscUtil.get(EquipmentSlotType.MAINHAND));
				}
			}
		}
		Object2IntMap.Entry<EquipmentSlotType> ench = MiscUtil.getEnchantedItem(UniqueEnchantments.DEATHS_ODIUM, event.getEntityLiving());
		if(ench.getIntValue() > 0)
		{
			ItemStack stack = event.getEntityLiving().getItemStackFromSlot(ench.getKey());
			setInt(stack, DeathsOdiumEnchantment.CURSE_STORAGE, getInt(stack, DeathsOdiumEnchantment.CURSE_STORAGE, 0) + 1);
			ModifiableAttributeInstance instance = event.getEntityLiving().getAttribute(Attributes.MAX_HEALTH);
			AttributeModifier mod = instance.getModifier(DeathsOdiumEnchantment.REMOVE_UUID);
			float toRemove = 0F;
			if(mod != null)
			{
				toRemove += mod.getAmount();
				instance.removeModifier(mod);
			}
			CompoundNBT nbt = event.getEntityLiving().getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
			event.getEntityLiving().getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, nbt);
			nbt.putFloat(DeathsOdiumEnchantment.CURSE_STORAGE, toRemove - 1F);
		}
	}
	
	@SubscribeEvent
	public void onRespawn(PlayerEvent.Clone event)
	{
		float f = event.getPlayer().getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG).getFloat(DeathsOdiumEnchantment.CURSE_STORAGE);
		if(f != 0)
		{
			event.getEntityLiving().getAttribute(Attributes.MAX_HEALTH).applyNonPersistentModifier(new AttributeModifier(DeathsOdiumEnchantment.REMOVE_UUID, "odiums_curse", f, Operation.ADDITION));
		}
	}
	
	@SubscribeEvent
	public void onXPDrop(LivingExperienceDropEvent event)
	{
		if(event.getAttackingPlayer() == null)
		{
			return;
		}
		int level = MiscUtil.getEnchantedItem(UniqueEnchantments.SAGES_BLESSING, event.getAttackingPlayer()).getIntValue();
		if(level > 0)
		{
			event.setDroppedExperience((int)(event.getDroppedExperience() + event.getDroppedExperience() * (level * SagesBlessingEnchantment.XP_BOOST.get())));
		}
	}
	
	@SubscribeEvent
	public void onLootingLevel(LootingLevelEvent event)
	{
		Entity entity = event.getDamageSource() == null ? null : event.getDamageSource().getTrueSource();
		if(entity instanceof LivingEntity && event.getEntityLiving() instanceof AbstractSkeletonEntity)
		{
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.BONE_CRUSH, ((LivingEntity)entity).getHeldItemMainhand());
			if(level > 0 && BoneCrusherEnchantment.isNotArmored((AbstractSkeletonEntity)event.getEntityLiving()))
			{
				event.setLootingLevel((event.getLootingLevel() + 1) + level);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDrops(LivingDropsEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof PlayerEntity && event.getEntityLiving() instanceof AnimalEntity)
		{
			PlayerEntity base = (PlayerEntity)entity;
			int level = MiscUtil.getEnchantedItem(UniqueEnchantments.FAST_FOOD, base).getIntValue();
			if(level > 0)
			{
				base.getFoodStats().addStats(FastFoodEnchantment.NURISHMENT.get(level), FastFoodEnchantment.SATURATION.getFloat() * level);
				event.setCanceled(true);
			}
		}
		if(event.getEntityLiving().isPotionActive(UniqueEnchantments.PESTILENCES_ODIUM_POTION))
		{
			event.setCanceled(true);
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
		Entity shooter = arrow.func_234616_v_();
		AlchemistsGraceEnchantment.applyToEntity(shooter);
		if(shooter instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)shooter;
			Object2IntMap.Entry<EquipmentSlotType> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.ENDERMARKSMEN, player);
			if(slot.getIntValue() > 0)
			{
				int level = slot.getIntValue();
				ItemStack stack = player.getItemStackFromSlot(slot.getKey());
				arrow.pickupStatus = PickupStatus.DISALLOWED;
				player.addItemStackToInventory(getArrowStack(arrow));
				int needed = Math.min((int)(level*EnderMarksmenEnchantment.SCALAR.get()), stack.getDamage());
				if(needed > 0)
				{
					stack.damageItem(-needed, player, MiscUtil.get(slot.getKey()));
				}
				Entity entity = ((EntityRayTraceResult)result).getEntity();
				if(entity instanceof EndermanEntity)
				{
					ENDER_MEN_TELEPORT.set(entity.getUniqueID());
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEndermenTeleport(EnderTeleportEvent event)
	{
		UUID id = ENDER_MEN_TELEPORT.get();
		if(event.getEntity().getUniqueID().equals(id))
		{
			ENDER_MEN_TELEPORT.set(null);
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onEquippementSwapped(LivingEquipmentChangeEvent event)
	{
		AttributeModifierManager attribute = event.getEntityLiving().getAttributeManager();
		Multimap<Attribute, AttributeModifier> mods = createModifiersFromStack(event.getFrom(), event.getSlot());
		if(!mods.isEmpty())
		{
			attribute.removeModifiers(mods);
		}
		mods = createModifiersFromStack(event.getTo(), event.getSlot());
		if(!mods.isEmpty())
		{
			attribute.reapplyModifiers(mods);
		}
	}
	
	private Multimap<Attribute, AttributeModifier> createModifiersFromStack(ItemStack stack, EquipmentSlotType slot)
	{
		Multimap<Attribute, AttributeModifier> mods = HashMultimap.create();
		//Optimization. After 3 Enchantment's its sure that on average you have more then 1 full iteration. So now we fully iterate once over it since hash-code would be a faster check.
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		int level = enchantments.getInt(UniqueEnchantments.VITAE);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.VITAE).contains(slot))
		{
			mods.put(Attributes.MAX_HEALTH, new AttributeModifier(VitaeEnchantment.getForSlot(slot), "Vitae Boost", level * VitaeEnchantment.HEALTH_BOOST.get(), Operation.ADDITION));
		}
		level = enchantments.getInt(UniqueEnchantments.SWIFT);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.SWIFT).contains(slot))
		{
			mods.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SwiftEnchantment.SPEED_MOD, "Swift Boost", SwiftEnchantment.SPEED_BONUS.getAsDouble(level), Operation.MULTIPLY_TOTAL));
		}
		level = enchantments.getInt(UniqueEnchantments.RANGE);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.RANGE).contains(slot))
		{
			mods.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(RangeEnchantment.RANGE_MOD, "Range Boost", RangeEnchantment.RANGE.getAsFloat(level), Operation.ADDITION));
		}
		level = enchantments.getInt(UniqueEnchantments.DEATHS_ODIUM);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.DEATHS_ODIUM).contains(slot))
		{
			int value = getInt(stack, DeathsOdiumEnchantment.CURSE_STORAGE, 0);
			if(value > 0)
			{
				mods.put(Attributes.MAX_HEALTH, new AttributeModifier(DeathsOdiumEnchantment.getForSlot(slot), "Death Odiums Restore", value, Operation.ADDITION));
			}
		}
		return mods;
	}
	
	public static int getInt(ItemStack stack, String tagName, int defaultValue)
	{
		CompoundNBT nbt = stack.getTag();
		return nbt == null || !nbt.contains(tagName) ? defaultValue : nbt.getInt(tagName);
	}
	
	public static void setInt(ItemStack stack, String tagName, int value)
	{
		stack.setTagInfo(tagName, IntNBT.valueOf(value));
	}
	
	public static long getLong(ItemStack stack, String tagName, long defaultValue)
	{
		CompoundNBT nbt = stack.getTag();
		return nbt == null || !nbt.contains(tagName) ? defaultValue : nbt.getLong(tagName);
	}
	
	public static void setLong(ItemStack stack, String tagName, long value)
	{
		stack.setTagInfo(tagName, LongNBT.valueOf(value));
	}
	
	public static void growStack(ItemStack source, int size, List<ItemStack> output)
	{
		while(size > 0)
		{
			ItemStack stack = source.copy();
			stack.setCount(Math.min(size, stack.getMaxStackSize()));
			output.add(stack);
			size -= stack.getCount();
		}
	}
	
	public static ItemStack getArrowStack(AbstractArrowEntity arrow)
	{
		try
		{
			//For Every ASM user. No. Anti ASM Guy writing this. Aka no ASM coming here. Live with it.
			return (ItemStack)ARROW_STACK.invoke(arrow, new Object[0]);
		}
		catch(Exception e)
		{
		}
		if(arrow instanceof SpectralArrowEntity)
		{
			return new ItemStack(Items.SPECTRAL_ARROW);
		}
		else if(arrow instanceof ArrowEntity)
		{
			CompoundNBT nbt = new CompoundNBT();
			arrow.writeWithoutTypeId(nbt);
			if(nbt.contains("CustomPotionEffects"))
			{
				ItemStack stack = new ItemStack(Items.TIPPED_ARROW);
				stack.setTagInfo("CustomPotionEffects", nbt.get("CustomPotionEffects"));
				return stack;
			}
			return new ItemStack(Items.ARROW);
		}
		return ItemStack.EMPTY;
	}
	
	public static boolean isOre(BlockState state)
	{
		return Tags.Items.ORES.contains(state.getBlock().asItem());
	}
	
	public static int consumeItems(PlayerEntity player, ToIntFunction<ItemStack> validator, int limit)
	{
		int found = 0;
		NonNullList<ItemStack> inv = player.inventory.mainInventory;
		for(int i = 0,m=inv.size();i<m;i++)
		{
			ItemStack stack = inv.get(i);
			int value = validator.applyAsInt(stack);
			if(value <= 0)
			{
				continue;
			}
			int left = limit - found;
			if(left >= stack.getCount() * value)
			{
				found+=stack.getCount() * value;
				if(stack.getItem().hasContainerItem(stack))
				{
					inv.set(i, stack.getItem().getContainerItem(stack));
					continue;
				}
				inv.set(i, ItemStack.EMPTY);
			}
			else if(left / value > 0)
			{
				stack.shrink(left / value);
				found += left;
			}
			if(found >= limit)
			{
				break;
			}
		}
		return found;
	}
	
	public static boolean hasBlockCount(World world, BlockPos pos, int limit, Predicate<BlockState> validator)
	{
		Mutable newPos = new Mutable();
		int found = 0;
		for(int y = 0;y<=1;y++)
		{
			for(int x = -4;x<=4;x++)
			{
				for(int z = -4;z<=4;z++)
				{
					newPos.setPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
					if(validator.test(world.getBlockState(newPos)))
					{
						found++;
						if(found >= limit)
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public static int getXP(PlayerEntity player)
	{
		return getXPForLvl(player.experienceLevel) + (int)(player.experience * player.xpBarCap());
	}
	
	public static int getXPForLvl(int level) 
	{
		if (level < 0) return Integer.MAX_VALUE;
		if (level <= 15)return level * level + 6 * level;
		if (level <= 30)return (int) (((level * level) * 2.5D) - (40.5D * level) + 360.0D);
		return (int) (((level * level) * 4.5D) - (162.5D * level) + 2220.0D);
	}

	public static int getLvlForXP(int totalXP)
	{
		int result = 0;
		while (getXPForLvl(result) <= totalXP) 
		{
			result++;
		}
		return --result;
	}
}
