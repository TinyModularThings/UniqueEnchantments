package uniqueeutils.handler;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.UEBase;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.handler.MathCache;
import uniquebase.networking.EntityPacket;
import uniquebase.utils.HarvestEntry;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquebase.utils.mixin.EntityMixin;
import uniquebase.utils.mixin.FireworkMixin;
import uniqueeutils.UEUtils;
import uniqueeutils.enchantments.complex.AlchemistsBlessing;
import uniqueeutils.enchantments.complex.AlchemistsBlessing.ConversionEntry;
import uniqueeutils.enchantments.complex.Ambrosia;
import uniqueeutils.enchantments.complex.Climber;
import uniqueeutils.enchantments.complex.EssenceOfSlime;
import uniqueeutils.enchantments.complex.SleipnirsGrace;
import uniqueeutils.enchantments.curse.FaminesOdium;
import uniqueeutils.enchantments.curse.RocketMan;
import uniqueeutils.enchantments.simple.Adept;
import uniqueeutils.enchantments.simple.ThickPick;
import uniqueeutils.enchantments.unique.AnemoiFragment;
import uniqueeutils.enchantments.unique.DemetersSoul;
import uniqueeutils.enchantments.unique.PegasusSoul;
import uniqueeutils.enchantments.unique.PoseidonsSoul;
import uniqueeutils.enchantments.unique.Reinforced;
import uniqueeutils.enchantments.unique.Resonance;
import uniqueeutils.enchantments.unique.SagesSoul;
import uniqueeutils.misc.RenderEntry;

public class UtilsHandler
{
	public static final UtilsHandler INSTANCE = new UtilsHandler();
	private List<RenderEntry> toRender = new ObjectArrayList<>();
	static final ThreadLocal<Boolean> PHANES_REGRET_ACTIVE = ThreadLocal.withInitial(() -> false); 
	
	int k = 0;
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)	
	public void onTick(ClientTickEvent event)
	{
		if(event.phase == Phase.START) return;
		Minecraft mc = Minecraft.getInstance();
		if(mc.level == null || mc.player == null) 
		{
			toRender.clear();
			return;
		}
		Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation("uniquee", "treasurers_eyes"));
		if(ench == null) 
		{
			toRender.clear();
			return;
		}
		int level = MiscUtil.getEnchantmentLevel(ench, mc.player.getItemBySlot(EquipmentSlotType.HEAD));
		if(level <= 0) 
		{
			toRender.clear();
			return;
		}
		for(int i = 0,m=toRender.size();i<m;i++)
		{
			if(toRender.get(i).update())
			{
				toRender.remove(i--);
				m--;
			}
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)	
	@SuppressWarnings("deprecation")
	public void onRender(RenderWorldLastEvent event)
	{
		if(toRender.isEmpty()) return;
		Minecraft mc = Minecraft.getInstance();
		Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation("uniquee", "treasurers_eyes"));
		if(ench == null) return;
		int level = MiscUtil.getEnchantmentLevel(ench, mc.player.getItemBySlot(EquipmentSlotType.HEAD));
		if(level <= 0) return;
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder buffer = tes.getBuilder();
		buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
		MatrixStack matrix = event.getMatrixStack();
		matrix.pushPose();
		Vector3d playerPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		matrix.translate(-playerPos.x(), -playerPos.y(), -playerPos.z());
		Matrix4f transform = matrix.last().pose();
		for(int i = 0,m=toRender.size();i<m;i++)
		{
			toRender.get(i).render(buffer, transform);
		}
		matrix.popPose();
		GlStateManager._disableTexture();
		GlStateManager._disableDepthTest();
		GlStateManager._enableAlphaTest();
		GlStateManager._alphaFunc(516, 0.1F);
		GlStateManager._enableBlend();
		RenderSystem.defaultBlendFunc();
		tes.end();
		GlStateManager._enableTexture();
		GlStateManager._enableDepthTest();
	}

	public void addDrawPosition(BlockPos pos)
	{
		toRender.add(new RenderEntry(pos, 0x33BBBBBB, 140));
	}

	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event)
	{
		if(event.world.isClientSide) return;
		Resonance.tick(event.world);
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START)
		{
			return;
		}
		PlayerEntity player = event.player;
		ItemStack armor = player.getItemBySlot(EquipmentSlotType.CHEST);
		if(!armor.isEmpty())
		{
			int level = MiscUtil.getEnchantmentLevel(UEUtils.ANEMOIS_FRAGMENT, armor);
			if(level > 0 && UEUtils.BOOST_KEY.test(player))
			{
				int amount = StackUtils.getInt(armor, AnemoiFragment.STORAGE, 0);
				if(amount > 0)
				{
					amount -= (int)(AnemoiFragment.CONSUMPTION.get()/Math.sqrt(AnemoiFragment.CONSUMPTION_SCALE.get(level)));
					StackUtils.setInt(armor, AnemoiFragment.STORAGE, Math.max(0, amount));
					player.moveRelative(1F, new Vector3d(0F, (float)Math.log10(110+Math.pow(AnemoiFragment.BOOST.get(level*player.experienceLevel), 0.125))-2, 0F));
				}
			}
			
			if(k % 20 == 0) {
				armor.setHoverName(MiscUtil.itemNameGen(armor, player, UEBase.ENTITY_NAME_CHANCE.get(), UEBase.RARITY_NAME_CHANCE.get(), UEBase.LOCATION_NAME_CHANCE.get()));
			}
			k++;
		}
		Entity ridden = player.getRootVehicle();
		if(ridden instanceof HorseEntity)
		{
			CompoundNBT nbt = ridden.getPersistentData();
			HorseEntity horse = (HorseEntity)ridden;
			ModifiableAttributeInstance instance = horse.getAttribute(Attributes.MOVEMENT_SPEED);
			int level = MiscUtil.getEnchantmentLevel(UEUtils.PEGASUS_SOUL, ridden.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(EmptyHandler.INSTANCE).getStackInSlot(1));
			if(level > 0)
			{
				boolean active = UEUtils.BOOST_KEY.test(player);
				if(active && !nbt.getBoolean(PegasusSoul.TRIGGER) && (!player.isOnGround() || nbt.getBoolean(PegasusSoul.ENABLED)))
				{
					nbt.putBoolean(PegasusSoul.ENABLED, !nbt.getBoolean(PegasusSoul.ENABLED));
					nbt.putBoolean(PegasusSoul.TRIGGER, true);
					UEBase.NETWORKING.sendToPlayer(new EntityPacket(ridden.getId(), nbt), player);
				}
				else if(!active)
				{
					nbt.putBoolean(PegasusSoul.TRIGGER, false);
				}
				if(instance.getModifier(PegasusSoul.SPEED_MOD) == null)
				{
					instance.addPermanentModifier(new AttributeModifier(PegasusSoul.SPEED_MOD, "Pegasus Boost", Math.sqrt(PegasusSoul.SPEED.get(level))*0.01D, Operation.MULTIPLY_TOTAL));
				}
			}
			else if(!ridden.level.isClientSide && instance.getModifier(PegasusSoul.SPEED_MOD) != null)
			{
				instance.removeModifier(PegasusSoul.SPEED_MOD);
			}
			if(nbt.getBoolean(PegasusSoul.ENABLED))
			{
				Vector3d vec = player.getDeltaMovement();
				ridden.setDeltaMovement(vec.x, 0D, vec.z);
				ridden.causeFallDamage(ridden.fallDistance, 1F);
				ridden.fallDistance = 0F;
				ridden.setOnGround(true);
			}
		}
		if(event.side.isClient()) return;
		long time = player.level.getGameTime();
		if(player.isPassenger() && time % 20 == 0)
		{
			Entity entity = player.getVehicle();
			if(entity instanceof HorseEntity && entity.isAlive())
			{
				HorseEntity horse = (HorseEntity)entity;
				int level = MiscUtil.getEnchantmentLevel(UEUtils.SLEIPNIRS_GRACE, entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null).getStackInSlot(1));
				if(level > 0)
				{
					CompoundNBT nbt = entity.getPersistentData();
					long lastTime = nbt.getLong(SleipnirsGrace.HORSE_NBT);
					if(lastTime == 0)
					{
						nbt.putLong(SleipnirsGrace.HORSE_NBT, time);
						lastTime = time;
					}
					Block block = horse.level.getBlockState(new BlockPos(horse.getX(), horse.getY() - 0.20000000298023224D,  horse.getZ())).getBlock();
					double bonus = Math.min(SleipnirsGrace.CAP.get()+level, SleipnirsGrace.GAIN.get((time - lastTime) * level));
					ModifiableAttributeInstance attri = horse.getAttribute(Attributes.MOVEMENT_SPEED);
					attri.removeModifier(SleipnirsGrace.SPEED_MOD);
					attri.addTransientModifier(new AttributeModifier(SleipnirsGrace.SPEED_MOD, "Sleipnirs Grace", Math.log10(10 + ((bonus / SleipnirsGrace.MAX.get()) * (block == Blocks.GRASS_PATH ? SleipnirsGrace.PATH_BONUS.get() : level)))-1D, Operation.MULTIPLY_TOTAL));
				}
				else
				{
					ModifiableAttributeInstance attri = horse.getAttribute(Attributes.MOVEMENT_SPEED);
					if(attri.getModifier(SleipnirsGrace.SPEED_MOD) != null)
					{
						attri.removeModifier(SleipnirsGrace.SPEED_MOD);
					}
				}
			}
		}
		int tickRate = Math.max(1, (int)(Reinforced.BASE_DURATION.get() / MathCache.LOG10.get(1000+player.experienceLevel)-2));
		if(time % tickRate == 0)
		{
			PlayerInventory inv = player.inventory;
			// I dislike doing that because its causing enough lag IMO... Efficient but the operation is laggy no matter how much you optimize it
			for(int i = 0,m=inv.getContainerSize();i<m;i++)
			{
				ItemStack stack = inv.getItem(i);
				if(stack.isEmpty()) continue;
				int level = MiscUtil.getEnchantmentLevel(UEUtils.REINFORCED, stack);
				if(level > 0)
				{
					int shield = StackUtils.getInt(stack, Reinforced.SHIELD, 0);
					if(shield > 0 && stack.getDamageValue() > 0 && (i >= inv.items.size() || i == inv.selected))
					{
						int damage = stack.getDamageValue();
						int left = Math.max(0, shield-damage);
						stack.setDamageValue(Math.max(0, damage-shield));
						shield = left;
						StackUtils.setInt(stack, Reinforced.SHIELD, shield);
					}
					int max = (int)Math.sqrt(625+player.experienceLevel*level)*4;
					if(shield < max)
					{
						shield = Math.min(max, shield + MathHelper.ceil(Math.sqrt(level)));
						StackUtils.setInt(stack, Reinforced.SHIELD, shield);						
					}
				}
			}
		}
		CompoundNBT nbt = player.getPersistentData();
		if(nbt.contains(Climber.CLIMB_POS) && time >= nbt.getLong(Climber.CLIMB_START) + nbt.getLong(Climber.CLIMB_DELAY))
		{
			nbt.remove(Climber.CLIMB_DELAY);
			nbt.remove(Climber.CLIMB_START);
			BlockPos pos = BlockPos.of(nbt.getLong(Climber.CLIMB_POS));
			nbt.remove(Climber.CLIMB_POS);
			player.teleportTo(pos.getX()+0.5F, pos.getY(), pos.getZ() + 0.5F);
		}
		int level = MiscUtil.getCombinedEnchantmentLevel(UEUtils.FAMINES_ODIUM, player);
		if(level > 0)
		{
			int duration = (int)Math.max((FaminesOdium.DELAY.get() / Math.pow(level, 0.125D)), 1);
			if(time % duration == 0)
			{
				Int2FloatMap.Entry entry = FaminesOdium.consumeRandomItem(player.inventory, FaminesOdium.NURISHMENT.getFloat() * level);
				if(entry != null)
				{
					float value = MathCache.LOG_ADD_MAX.getFloat(level);
					player.getFoodData().eat(MathHelper.ceil(FaminesOdium.NURISHMENT.get(entry.getIntKey() * value)), entry.getFloatValue() * level * value);
					player.level.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, player.level.random.nextFloat() * 0.1F + 0.9F);
				}
				else
				{
					player.hurt(DamageSource.MAGIC, FaminesOdium.DAMAGE.getFloat(duration * MathCache.LOG_ADD_MAX.getFloat(level)));
				}
			}
		}
		int delay = Math.max(1, MathHelper.ceil(DemetersSoul.DELAY.get() / Math.log(10 + DemetersSoul.SCALING.get(MiscUtil.getEnchantmentLevel(UEUtils.DEMETERS_SOUL, player.getItemInHand(Hand.MAIN_HAND))))));
		if(player.level.getGameTime() % delay == 0)
		{
			HarvestEntry entry = DemetersSoul.getNextIndex(player);
			if(entry != null)
			{
				ActionResultType result = entry.harvest(player.level, player);
				if(result == ActionResultType.FAIL)
				{
					ListNBT list = DemetersSoul.getCrops(player);
					for(int i = 0, m = list.size();i < m;i++)
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
					player.causeFoodExhaustion(0.06F);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onFall(LivingFallEvent event)
	{
		ItemStack stack = event.getEntityLiving().getItemBySlot(EquipmentSlotType.FEET);
		int level = MiscUtil.getEnchantmentLevel(UEUtils.ESSENCE_OF_SLIME, stack);
		if(level > 0)
		{
			if(event.getDistance() <= 1.3)
				return;
			int damage = MathHelper.floor(((event.getDistance() - 2) * EssenceOfSlime.DURABILITY_LOSS.get() / (level + MiscUtil.getEnchantmentLevel(Enchantments.FALL_PROTECTION, stack))));
			if(damage > 0)
			{
				stack.hurtAndBreak(damage, event.getEntityLiving(), MiscUtil.get(EquipmentSlotType.FEET));
			}
			LivingEntity entity = event.getEntityLiving();
			entity.getPersistentData().putDouble("bounce", entity.getDeltaMovement().y()/Math.log10(15.5D+EssenceOfSlime.FALL_DISTANCE_MULTIPLIER.get(entity.getDeltaMovement().y())) * -1D);
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onEntityTick(LivingUpdateEvent event)
	{
		if(event.getEntityLiving() instanceof PlayerEntity == event.getEntityLiving().level.isClientSide)
		{
			CompoundNBT data = event.getEntityLiving().getPersistentData();
			if(data.contains("bounce"))
			{
				Vector3d motion = event.getEntityLiving().getDeltaMovement();
				event.getEntityLiving().setDeltaMovement(motion.x(), data.getDouble("bounce"), motion.z());
				data.remove("bounce");
			}
		}
	}
	
	@SubscribeEvent
	public void onEaten(LivingEntityUseItemEvent.Finish event)
	{
		if(event.getEntityLiving() instanceof PlayerEntity)
		{
			int level = MiscUtil.getEnchantmentLevel(UEUtils.AMBROSIA, event.getItem());
			if(level > 0)
			{
				int duration = (int)(Ambrosia.BASE_DURATION.get() + (Math.log(MathCache.POW5.get(1 + ((PlayerEntity)event.getEntityLiving()).experienceLevel * level)) * Ambrosia.DURATION_MULTIPLIER.get()));
				((PlayerEntity)event.getEntityLiving()).getFoodData().eat(2000, 0);
				event.getEntityLiving().addEffect(new EffectInstance(UEUtils.SATURATION, duration, Math.min(20, level)));
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		if(event.getAmount() >= 1F)
		{
			if(PHANES_REGRET_ACTIVE.get()) return;
			PHANES_REGRET_ACTIVE.set(true);
			int level = MiscUtil.getCombinedEnchantmentLevel(UEUtils.PHANES_REGRET, event.getEntityLiving());
			if(level > 0)
			{
				if(event.getEntityLiving() instanceof PlayerEntity)
				{
					MiscUtil.drainExperience((PlayerEntity)event.getEntityLiving(), MathHelper.ceil(event.getAmount()));
				}
				event.getEntityLiving().heal(event.getAmount() * (1F-(1F/MathCache.LOG_ADD.getFloat(level))));
			}
			PHANES_REGRET_ACTIVE.set(false);
		}
	}
	
	@SubscribeEvent
	public void onHeal(LivingHealEvent event)
	{
		if(PHANES_REGRET_ACTIVE.get()) return;
		PHANES_REGRET_ACTIVE.set(true);
		if(event.getAmount() >= 1F)
		{
			int level = MiscUtil.getCombinedEnchantmentLevel(UEUtils.PHANES_REGRET, event.getEntityLiving());
			if(level > 0)
			{
				event.getEntityLiving().hurt(DamageSource.MAGIC, event.getAmount() * (1F-(1F/MathCache.LOG_ADD.getFloat(level))));
			}
		}
		PHANES_REGRET_ACTIVE.set(false);
	}
	
	@SubscribeEvent
	public void onArrowHit(ProjectileImpactEvent.Arrow event)
	{
		if(!(event.getArrow() instanceof TridentEntity) || !(event.getArrow().getOwner() instanceof PlayerEntity)) return;
		RayTraceResult result = event.getRayTraceResult();
		if(!(result instanceof BlockRayTraceResult)) return;
		BlockRayTraceResult ray = (BlockRayTraceResult)result;
		World world = event.getEntity().getCommandSenderWorld();
		BlockState state = world.getBlockState(ray.getBlockPos());
		if(PoseidonsSoul.isValid(state.getBlock()))
		{
			ItemStack arrowStack = StackUtils.getArrowStack(event.getArrow());
			if(arrowStack.getItem() != Items.TRIDENT) return;
			Object2IntMap<Enchantment> enchs = MiscUtil.getEnchantments(arrowStack);
			int level = enchs.getInt(UEUtils.POSEIDONS_SOUL);
			if(level <= 0) return;
			ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
			stack.enchant(Enchantments.SILK_TOUCH, 1);
			Block.popResource(world, ray.getBlockPos(), new ItemStack(state.getBlock(), level * (world.random.nextInt(enchs.getInt(Enchantments.BLOCK_FORTUNE)+1)+1)));
			MiscUtil.drainExperience((PlayerEntity)event.getArrow().getOwner(), (int)Math.log10(10+Math.pow(PoseidonsSoul.BASE_CONSUMTION.get()+level, level)));
		}
	}
	
	@SubscribeEvent
	public void onItemClick(RightClickItem event)
	{
		if(event.getPlayer() == null || !event.getPlayer().isShiftKeyDown()) return;
		PlayerEntity player = event.getPlayer();
		ItemStack stack = event.getItemStack();
		int level = MiscUtil.getEnchantmentLevel(UEUtils.RESONANCE, stack);
		if(level > 0)
		{
			if(!event.getWorld().isClientSide)
			{
				CompoundNBT nbt = player.getPersistentData();
				if(nbt.getLong(Resonance.COOLDOWN_TAG) > event.getWorld().getGameTime())
				{
					int used = nbt.getInt(Resonance.OVERUSED_TAG);
					player.hurt(DamageSource.MAGIC, 0.5F*used);
					nbt.putInt(Resonance.OVERUSED_TAG, used+1);
					if(!player.level.isClientSide) player.displayClientMessage(new TranslationTextComponent("tooltip.uniqueutil.resonance.cooldown", (nbt.getLong(Resonance.COOLDOWN_TAG) - event.getWorld().getGameTime()) / 20), true);
				}
				else
				{
					Resonance.addResonance(event.getWorld(), event.getPos(), (int)(Resonance.RANGE.getSqrt(level*Math.sqrt(player.experienceLevel))));
					nbt.putLong(Resonance.COOLDOWN_TAG, event.getWorld().getGameTime()+Resonance.COOLDOWN.get());
					nbt.putInt(Resonance.OVERUSED_TAG, 1);
				}
			}
			event.setCanceled(true);
			event.setCancellationResult(ActionResultType.SUCCESS);
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getPlayer().isShiftKeyDown())
		{
			BlockState state = event.getWorld().getBlockState(event.getPos());
			if(state.getBlock().isLadder(state, event.getWorld(), event.getPos(), event.getEntityLiving()))
			{
				int level = MiscUtil.getEnchantmentLevel(UEUtils.CLIMBER, event.getPlayer().getItemBySlot(EquipmentSlotType.LEGS));
				if(level > 0)
				{
					Mutable pos = new Mutable().set(event.getPos());
					List<Block> blocks = new ObjectArrayList<>();
					do 
					{
						pos.move(Direction.UP);
						state = event.getWorld().getBlockState(pos);
						blocks.add(state.getBlock());
					}
					while(state.getBlock().isLadder(state, event.getWorld(), pos, event.getEntityLiving()));
					if(!event.getWorld().getBlockState(pos.above()).isCollisionShapeFullBlock(event.getWorld(), pos.above()) && !event.getWorld().getBlockState(pos.above(2)).isCollisionShapeFullBlock(event.getWorld(), pos.above(2)))
					{
						CompoundNBT nbt = event.getPlayer().getPersistentData();
						boolean wasClimbing = nbt.getLong(Climber.CLIMB_START) != 0;
						nbt.putLong(Climber.CLIMB_POS, pos.asLong());
						nbt.putInt(Climber.CLIMB_DELAY, Climber.getClimbTime(level, blocks));
						nbt.putLong(Climber.CLIMB_START, event.getWorld().getGameTime());
						event.getPlayer().displayClientMessage(new TranslationTextComponent("tooltip.uniqueutil.climb."+(wasClimbing ? "re" : "")+"start.name"), true);
						event.setCancellationResult(ActionResultType.SUCCESS);
					}
					else
					{
						event.getPlayer().displayClientMessage(new TranslationTextComponent("tooltip.uniqueutil.climb.fail.name"), true);
					}
				}
			}
			else if(state.getBlock() == Blocks.ANVIL)
			{
				ItemStack stack = event.getItemStack();
				PlayerEntity player = event.getPlayer();
				Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(stack);
				int level = ench.getInt(UEUtils.SAGES_SOUL);
				if(level > 0)
				{
					int stored = StackUtils.getInt(stack, SagesSoul.STORED_XP, 0);
					int required = MiscUtil.getXPForLvl(stored+10);
					if(player.totalExperience >= required || player.isCreative())
					{
						MiscUtil.drainExperience(player, required);
						StackUtils.setInt(stack, SagesSoul.STORED_XP, stored+5);
						event.setCancellationResult(ActionResultType.SUCCESS);
					}
					else
					{
						if(!player.level.isClientSide) player.displayClientMessage(new TranslationTextComponent("tooltip.uniqueutil.missing.xp"), true);
						event.setCancellationResult(ActionResultType.FAIL);
					}
					event.setCanceled(true);
				}
			}
			else if(state.getBlock() == Blocks.BEACON)
			{
				Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(event.getItemStack());
				int level = ench.getInt(UEUtils.ALCHEMISTS_BLESSING);
				if(level > 0 && !event.getWorld().isClientSide)
				{
					TileEntity tile = event.getWorld().getBlockEntity(event.getPos());
					if(tile instanceof BeaconTileEntity)
					{
						BeaconTileEntity beacon = (BeaconTileEntity)tile;
						if(beacon.save(new CompoundNBT()).getInt("Primary") == -1) return;
						World world = beacon.getLevel();
						BlockPos pos = beacon.getBlockPos().above();
						List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(beacon.getBlockPos().above()));
						if(items.isEmpty()) return;
						int consumed = 0;
						int xpToSpawn = 0;
						for(ItemEntity item : items)
						{
							ItemStack stack = item.getItem();
							consumed += stack.getCount();
							item.remove();
							item.setNeverPickUp();
							ConversionEntry entry = AlchemistsBlessing.RECIPES.get(stack.getItem());
							if(entry == null)
							{
								xpToSpawn += stack.getCount();
								continue;
							}
							List<ItemStack> newDrops = new ObjectArrayList<ItemStack>();
							entry.generateOutput(world.random, level-1, Math.min(world.random.nextInt(Math.max(ench.getInt(Enchantments.BLOCK_FORTUNE), ench.getInt(Enchantments.MOB_LOOTING)) + 1), level), stack.getCount(), newDrops);
							for(ItemStack drop : newDrops)
							{
								world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, drop));
							}
						}
						if(xpToSpawn > 0) world.addFreshEntity(new ExperienceOrbEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, MathHelper.ceil(Math.sqrt(AlchemistsBlessing.BASE_XP_USAGE.get(level))+Math.sqrt(AlchemistsBlessing.LVL_XP_USAGE.get(level*world.random.nextDouble()))) * xpToSpawn));
						consumed = MathHelper.ceil(Math.pow((level*consumed*AlchemistsBlessing.CONSUMTION.get()), 0.6505));
						int stored = StackUtils.getInt(event.getItemStack(), AlchemistsBlessing.STORED_REDSTONE, 0) - consumed;
						StackUtils.setInt(event.getItemStack(), AlchemistsBlessing.STORED_REDSTONE, Math.max(0, stored));
						if(stored < 0) event.getItemStack().hurtAndBreak(-stored, event.getPlayer(), MiscUtil.get(event.getHand() == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND));
						event.setCancellationResult(ActionResultType.SUCCESS);
						event.setCanceled(true);
					}
				}
			}
			else
			{
				int level = MiscUtil.getEnchantmentLevel(UEUtils.DEMETERS_SOUL, event.getItemStack());
				if(level > 0 && CropHarvestRegistry.INSTANCE.isValid(state.getBlock()) && !event.getWorld().isClientSide)
				{
					HarvestEntry entry = new HarvestEntry(event.getWorld().dimension().location(), event.getPos().asLong());
					ListNBT list = DemetersSoul.getCrops(event.getPlayer());
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
						if(list.size() >= DemetersSoul.CAP.get(level))
						{
							event.getPlayer().displayClientMessage(new TranslationTextComponent("tooltip.uniqueutil.crops.full.name"), false);
							event.setCancellationResult(ActionResultType.SUCCESS);
							event.setCanceled(true);
							return;
						}
						list.add(entry.save());
					}
					event.getPlayer().displayClientMessage(new TranslationTextComponent("tooltip.uniqueutil.crops."+(found ? "removed" : "added")+".name"), false);
					event.setCancellationResult(ActionResultType.SUCCESS);
					event.setCanceled(true);
					return;
				}
			}
		}
		else
		{
			Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(event.getItemStack());
			if(ench.getInt(UEUtils.DETEMERS_BLESSING) > 0)
			{
				BlockState state = event.getWorld().getBlockState(event.getPos());
				if(state.getBlock() instanceof CropsBlock)
				{
					CropsBlock crops = (CropsBlock)state.getBlock();
					if(crops.isMaxAge(state))
					{
						Block.dropResources(state, event.getWorld(), event.getPos(), event.getWorld().getBlockEntity(event.getPos()), event.getPlayer(), event.getItemStack());
						event.getWorld().setBlockAndUpdate(event.getPos(), crops.getStateForAge(0));
						event.getItemStack().hurtAndBreak(1, event.getEntityLiving(), T -> T.broadcastBreakEvent(event.getHand()));
					}
				}
				else if(state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.GRASS_PATH || state.getBlock() == Blocks.GRASS_BLOCK)
				{
					event.getItemStack().hurt(-ench.getInt(UEUtils.DETEMERS_BLESSING), event.getWorld().random, null);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLeftHorse(EntityMountEvent event)
	{
		if(event.getEntityBeingMounted() instanceof HorseEntity)
		{
			HorseEntity horse = (HorseEntity)event.getEntityBeingMounted();
			horse.getPersistentData().remove(SleipnirsGrace.HORSE_NBT);
			horse.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SleipnirsGrace.SPEED_MOD);
		}
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
		
		int level = MiscUtil.getEnchantmentLevel(UEUtils.ADEPT, player.getItemBySlot(EquipmentSlotType.HEAD));
		if(level > 0)
		{
			boolean inWater = player.isEyeInFluid(FluidTags.WATER);
			event.setNewSpeed(event.getNewSpeed() * (player.isOnGround() ? 1F : 5F) * (inWater && !EnchantmentHelper.hasAquaAffinity(player) ? 5F : 1F));
			if(inWater || !player.isOnGround())
			{
				event.setNewSpeed((float)(event.getNewSpeed() * -Math.sqrt(1/(1 + level*level * Adept.SPEED_SCALE.get()))+1));	
			}
		}
		Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(held);
		level = ench.getInt(UEUtils.SAGES_SOUL);
		if(level > 0)
		{
			double power = SagesSoul.getEnchantPower(held, level);
			int levels = StackUtils.getInt(held, SagesSoul.STORED_XP, 0);
			float toolSpeed = player.inventory.getDestroySpeed(event.getState());
			float invToolSpeed = 1F/toolSpeed;
			float speed = event.getNewSpeed();
			event.setNewSpeed(speed + toolSpeed * (1 + (float)Math.pow(Math.log(7.39+(levels*levels)/(speed*invToolSpeed)), power)*invToolSpeed));
		}
		level = ench.getInt(UEUtils.THICK_PICK);
		if(level > 0 && event.getState().getDestroySpeed(player.level, event.getPos()) >= 20)
		{
			int amount = StackUtils.getInt(held, ThickPick.TAG, 0);
			if(amount > 0)
			{
				event.setNewSpeed(event.getNewSpeed() * ThickPick.MINING_SPEED.getAsFloat(level));
			}
		}
		level = ench.getInt(UEUtils.REINFORCED);
		if(level > 0)
		{
			double base = Reinforced.BASE_REDUCTION.get();
			event.setNewSpeed((float)(event.getNewSpeed() * Math.pow((base+((1D-base)*0.01D)*level), Math.sqrt(level))));
		}
	}
	
	@SubscribeEvent
	public void onItemUseTick(LivingEntityUseItemEvent.Tick event)
	{
		int level = MiscUtil.getEnchantmentLevel(UEUtils.SAGES_SOUL, event.getItem());
		if(level > 0)
		{
			event.setDuration((int)(event.getDuration() * 1/(Math.log10(Math.pow(1000+SagesSoul.DRAW_SPEED.get(StackUtils.getInt(event.getItem(), SagesSoul.STORED_XP, 0)), SagesSoul.getEnchantPower(event.getItem(), level)))-2)));
		}
	}
	
	@SubscribeEvent
	public void onBlockBroken(BreakEvent event)
	{
		if(event.getPlayer() == null)
		{
			return;
		}
		PlayerEntity player = event.getPlayer();
		ItemStack held = player.getMainHandItem();
		int level = MiscUtil.getEnchantmentLevel(UEUtils.THICK_PICK, held);
		if(level > 0)
		{
			int amount = StackUtils.getInt(held, ThickPick.TAG, 0);
			if(amount > 0)
			{
				StackUtils.setInt(held, ThickPick.TAG, amount-1);
			}
		}
	}
	
	@SubscribeEvent
	public void onItemAirClick(RightClickItem event)
	{
		ItemStack stack = event.getItemStack();
		if(stack.getItem() == Items.FIREWORK_ROCKET && event.getPlayer() != null && !event.getPlayer().isFallFlying())
		{
			ItemStack armor = event.getPlayer().getItemBySlot(EquipmentSlotType.CHEST);
			int level = MiscUtil.getEnchantmentLevel(UEUtils.ROCKET_MAN, armor);
			if(armor.getItem() instanceof ElytraItem && level > 0)
			{
                if(!event.getWorld().isClientSide)
                {
                    event.getWorld().addFreshEntity(new FireworkRocketEntity(event.getWorld(), stack, event.getPlayer()));
                }
                else
                {
                	PlayerEntity player = event.getPlayer();
                	event.getPlayer().teleportTo(player.getX(), player.getY() + 0.5D, player.getZ());
                }
                ((EntityMixin)event.getPlayer()).setFlag(7, true);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		if(event.getEntity() instanceof FireworkRocketEntity)
		{
			FireworkRocketEntity rocket = (FireworkRocketEntity)event.getEntity();
			try
			{
				LivingEntity entity = ((FireworkMixin)rocket).getRidingEntity();
				if(entity != null)
				{
					ItemStack stack = entity.getItemBySlot(EquipmentSlotType.CHEST);
					if(stack.getItem() instanceof ElytraItem)
					{
						int level = MiscUtil.getEnchantmentLevel(UEUtils.ROCKET_MAN, stack);
						if(level > 0)
						{
							CompoundNBT nbt = new CompoundNBT();
							rocket.addAdditionalSaveData(nbt);
							int time = nbt.getInt("LifeTime");
							time += time * RocketMan.FLIGHT_TIME.getAsDouble(level) * MathCache.LOG_ADD_MAX.get(level);
							nbt.putInt("LifeTime", time);
							rocket.readAdditionalSaveData(nbt);
						}
					}
				}
			}
			catch(Exception e){e.printStackTrace();}
		}
	}
	
	@SubscribeEvent
	public void onEntityAttack(LivingAttackEvent event)
	{
		if(event.getAmount() > 0F)
		{
			for(PlayerEntity player : getPlayers(event.getEntity()))
			{
				if(canBlockDamageSource(event.getSource(), player))
				{
					damageShield(event.getAmount(), player);
					event.setCanceled(true);
					return;
				}
			}
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
		mods = createModifiersFromStack(event.getTo(),event.getEntityLiving(), event.getSlot());
		if(!mods.isEmpty())
		{
			attribute.addTransientAttributeModifiers(mods);
		}
	}

	private Multimap<Attribute, AttributeModifier> createModifiersFromStack(ItemStack stack, LivingEntity living, EquipmentSlotType slot)
	{
		Multimap<Attribute, AttributeModifier> mods = HashMultimap.create();
		Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(stack);
		int level = ench.getInt(UEUtils.REINFORCED);
		if(level > 0)
		{
			double base = Reinforced.BASE_REDUCTION.get();
			mods.put(Attributes.ATTACK_SPEED, new AttributeModifier(Reinforced.SPEED_MOD, "Reinforced Boost", Math.pow((base+((1D-base)*0.01D)*level), Math.sqrt(level)), Operation.MULTIPLY_TOTAL));
		}
		level = ench.getInt(UEUtils.SAGES_SOUL);
		if(level > 0)
		{
			int levels = StackUtils.getInt(stack, SagesSoul.STORED_XP, 0);
			double power = SagesSoul.getEnchantPower(stack, level);
			mods.put(Attributes.ATTACK_SPEED, new AttributeModifier(SagesSoul.ATTACK_MOD.getId(slot), "Sage Attack Boost", Math.pow(-0.5+Math.sqrt(0.25+SagesSoul.ATTACK_SPEED.get(2*levels)), power)/SagesSoul.ATTACK_SPEED.get(), Operation.MULTIPLY_TOTAL));
			mods.put(Attributes.ARMOR, new AttributeModifier(SagesSoul.ARMOR_MOD.getId(slot), "Sages Armor Boost", Math.pow((-0.5+SagesSoul.ARMOR_SCALE.get(Math.sqrt(0.25+8*(levels*Math.sqrt(level))))), power)/SagesSoul.ARMOR_DIVIDOR.get(), Operation.ADDITION));
			mods.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(SagesSoul.TOUGHNESS_MOD.getId(slot), "Sages Toughness Boost", Math.pow((-0.5+SagesSoul.TOUGHNESS_SCALE.get(Math.sqrt(0.25+1*(levels*Math.sqrt(level))))), power)/SagesSoul.TOUGHNESS_DIVIDOR.get(), Operation.ADDITION));
		}
		return mods;
	}
	
	public List<PlayerEntity> getPlayers(Entity original)
	{
		List<PlayerEntity> players = new ObjectArrayList<>();
		for(Entity entity : original.getIndirectPassengers())
		{
			if(entity instanceof PlayerEntity) players.add((PlayerEntity)entity);
		}
		return players;
	}
	
    public static void damageShield(float damage, PlayerEntity player)
    {
        if (damage >= 3.0F && player.getUseItem().getItem().isShield(player.getUseItem(), player))
        {
            ItemStack copyBeforeUse = player.getUseItem().copy();
            int i = 1 + MathHelper.floor(damage);
            player.getUseItem().hurtAndBreak(i, player, T -> T.broadcastBreakEvent(player.getUsedItemHand()));
            if (player.getUseItem().isEmpty())
            {
                Hand hand = player.getUsedItemHand();
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, hand);

                if (hand == Hand.MAIN_HAND)
                {
                    player.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                }
                else
                {
                    player.setItemSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
                }

                player.stopUsingItem();
                player.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + player.level.random.nextFloat() * 0.4F);
            }
        }
    }

    public static boolean canBlockDamageSource(DamageSource damageSourceIn, PlayerEntity player)
    {
        if (!damageSourceIn.isBypassArmor() && player.isBlocking())
        {
        	if(MiscUtil.getEnchantmentLevel(UEUtils.MOUNTING_AEGIS, player.getUseItem()) <= 0) return false;
            Vector3d Vector3d = damageSourceIn.getSourcePosition();

            if (Vector3d != null)
            {
                Vector3d Vector3d1 = player.getViewVector(1.0F);
                Vector3d Vector3d2 = Vector3d.vectorTo(new Vector3d(player.getX(), player.getY(), player.getZ())).normalize();
                Vector3d2 = new Vector3d(Vector3d2.x, 0.0D, Vector3d2.z);
                if (Vector3d2.dot(Vector3d1) < 0.0D)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
