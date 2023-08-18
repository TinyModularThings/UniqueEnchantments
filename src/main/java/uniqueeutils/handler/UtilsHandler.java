package uniqueeutils.handler;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.UEBase;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.api.events.ItemDurabilityChangeEvent;
import uniquebase.handler.MathCache;
import uniquebase.networking.EntityPacket;
import uniquebase.utils.HarvestEntry;
import uniquebase.utils.ICurioHelper.CurioSlot;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquebase.utils.mixin.common.entity.EntityMixin;
import uniquebase.utils.mixin.common.entity.FireworkMixin;
import uniquebase.utils.mixin.common.entity.PotionMixin;
import uniqueeutils.UEUtils;
import uniqueeutils.enchantments.complex.AlchemistsBlessing;
import uniqueeutils.enchantments.complex.AlchemistsBlessing.ConversionEntry;
import uniqueeutils.enchantments.complex.Ambrosia;
import uniqueeutils.enchantments.complex.Climber;
import uniqueeutils.enchantments.complex.EssenceOfSlime;
import uniqueeutils.enchantments.complex.SleipnirsGrace;
import uniqueeutils.enchantments.curse.FaminesOdium;
import uniqueeutils.enchantments.curse.FaminesOdium.FoodEntry;
import uniqueeutils.enchantments.curse.RocketMan;
import uniqueeutils.enchantments.simple.Adept;
import uniqueeutils.enchantments.simple.Dreams;
import uniqueeutils.enchantments.simple.ThickPick;
import uniqueeutils.enchantments.unique.AnemoiFragment;
import uniqueeutils.enchantments.unique.DemetersSoul;
import uniqueeutils.enchantments.unique.PegasusSoul;
import uniqueeutils.enchantments.unique.PoseidonsSoul;
import uniqueeutils.enchantments.unique.Reinforced;
import uniqueeutils.enchantments.unique.Resonance;
import uniqueeutils.enchantments.unique.SagesSoul;
import uniqueeutils.enchantments.upgrades.PhanesUpgrade;
import uniqueeutils.enchantments.upgrades.RocketUpgrade;
import uniqueeutils.misc.RenderEntry;

public class UtilsHandler
{
	public static final UtilsHandler INSTANCE = new UtilsHandler();
	private List<RenderEntry> toRender = new ObjectArrayList<>();
	static final ThreadLocal<Boolean> PHANES_REGRET_ACTIVE = ThreadLocal.withInitial(() -> false);
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onTick(ClientTickEvent event)
	{
		if(event.phase == Phase.START)
			return;
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
		int level = MiscUtil.getEnchantmentLevel(ench, mc.player.getItemBySlot(EquipmentSlot.HEAD));
		if(level <= 0)
		{
			toRender.clear();
			return;
		}
		for(int i = 0, m = toRender.size();i < m;i++)
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
	public void onRender(RenderLevelStageEvent event)
	{
		if(toRender.isEmpty() || event.getStage() != Stage.AFTER_WEATHER) return;
		Minecraft mc = Minecraft.getInstance();
		Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation("uniquee", "treasurers_eyes"));
		if(ench == null)
			return;
		int level = MiscUtil.getEnchantmentLevel(ench, mc.player.getItemBySlot(EquipmentSlot.HEAD));
		if(level <= 0)
			return;
		
		Tesselator tes = Tesselator.getInstance();
		BufferBuilder buffer = tes.getBuilder();
		buffer.begin(Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
		PoseStack matrix = event.getPoseStack();
		matrix.pushPose();
		Vec3 playerPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		matrix.translate(-playerPos.x(), -playerPos.y(), -playerPos.z());
		Matrix4f transform = matrix.last().pose();
		for(int i = 0, m = toRender.size();i < m;i++)
		{
			toRender.get(i).render(buffer, transform);
		}
		matrix.popPose();
		RenderSystem.disableTexture();
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		tes.end();
		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
	}
	
	@OnlyIn(Dist.CLIENT)
	public void onOverlay(PoseStack mStack)
	{
		Minecraft mc = Minecraft.getInstance();
		Gui gui = mc.gui;
		Window window = mc.getWindow();
		Player player = mc.player;
		float shield = MiscUtil.getPersistentData(player).getInt(PhanesUpgrade.SHIELD_STORAGE);
		float max = MathCache.LOG.getFloat(1+ UEUtils.PHANES_UPGRADE.getCombinedPoints(player));
		int hearts = Mth.ceil(Mth.clamp(shield / max * 20F, 0, 20));
		if(hearts <= 0 || !UEUtils.RENDER_SHIELD_HUD.get()) return;
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, new ResourceLocation("uniqueutil", "textures/gui/icons.png"));
        int left = window.getGuiScaledWidth() / 2 - 91;
        int top = window.getGuiScaledHeight() - 39;
        int textureY = 9 * (mc.level.getLevelData().isHardcore() ? 5 : 0);
        int regen = player.hasEffect(MobEffects.REGENERATION) ? gui.getGuiTicks() % 25 : -1;
        int health = Mth.ceil(player.getHealth());
        RenderSystem.setShaderColor(1F, 1F, 1F, 0.7F);
        for(int i = hearts/2;i>=0;i--)
        {
            int row = Mth.ceil((float)(i + 1) / 10.0F) - 1;
            int x = left + i % 10 * 8;
            int y = top - row * 3;
            if (health <= 4) y += mc.level.random.nextInt(2);
            if (i == regen) y -= 2;
            if (i * 2 + 1 < hearts)
            	gui.blit(mStack, x, y, 52, textureY, 9, 9); //4
            else if (i * 2 + 1 == hearts)
                gui.blit(mStack, x, y, 61, textureY, 9, 9); //5
        }
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();
	}
	
	public void addDrawPosition(BlockPos pos)
	{
		toRender.add(new RenderEntry(pos, 0x33BBBBBB, 140));
	}
	
	@SubscribeEvent
	public void onWorldTick(LevelTickEvent event)
	{
		if(event.level.isClientSide)
			return;
		Resonance.tick(event.level);
	}
	
	@SubscribeEvent
	public void onSleep(PlayerWakeUpEvent event) {
		if(event.getResult() == Result.DENY) return;
		Player player = event.getEntity();
		double multiplier = 1D;
		int total = 0;
		for(EquipmentSlot slot : MiscUtil.getEquipmentSlotsFor(UEUtils.DREAMS))
		{
			int lvl = MiscUtil.getEnchantmentLevel(UEUtils.DREAMS, player.getItemBySlot(slot));
			if(lvl == 0) continue;
			multiplier *= 1-Math.pow(Dreams.DURABILITY_FACTOR.get(), Math.sqrt(lvl));
			total += lvl;
		}
		if(total == 0) return;
		List<ItemStack> items = new ArrayList<>();
		IItemHandler handler = player.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(EmptyHandler.INSTANCE);
		filterItem(handler, false, items);
		for(ItemStack item : items)
		{
			item.setDamageValue((int) (item.getDamageValue()*multiplier));
		}
	}
	
	private static void filterItem(IItemHandler handler, boolean subLayer, List<ItemStack> result) 
	{
		for(int i = 0,m=handler.getSlots();i<m;i++)
		{
			ItemStack stack = handler.extractItem(i, 1, true);
			if(stack.isEmpty()) continue;
			if(stack.isDamageableItem() && stack.isDamaged())
			{
				result.add(stack);
			}
			else if(!subLayer)
			{
				LazyOptional<IItemHandler> subHandler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
				if(!subHandler.isPresent()) continue;
				filterItem(subHandler.orElse(EmptyHandler.INSTANCE), true, result);
			}
		}
	}
	
	@SubscribeEvent
	public void onItemDamage(ItemDurabilityChangeEvent event) 
	{
		ItemStack stack = event.item;
		LivingEntity ent = event.entity;
		double damage = stack.getDamageValue();
		if(damage > stack.getMaxDamage()*0.9d) 
		{
			int level = MiscUtil.getEnchantmentLevel(UEUtils.ALCHEMISTS_MENDING, stack);
			if(level > 0 && ent.getActiveEffects().size() > 0) 
			{	
				MobEffectInstance mei = getFirstEffect(600, 106800, ent);
				if(mei == null) return;
				int duration = mei.getDuration();
				double factor = (mei.getAmplifier() + level);
				double a = Math.ceil(damage/factor);
				int rem = (int) Math.max(duration - (a*20), 1);
				stack.setDamageValue((int) Math.max(damage-(a*factor),0));
				((PotionMixin)mei).setPotionDuration(rem);
				if(ent instanceof ServerPlayer player) {
					player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), mei));
				}
			}
		}
		if(damage >= stack.getMaxDamage() - 10) 
		{
			int level = MiscUtil.getEnchantmentLevel(UEUtils.REINFORCED, stack);
			if(level > 0) 
			{
				int points = StackUtils.getInt(stack, Reinforced.SHIELD, 0);
				if(points <= 0) return;
				stack.setDamageValue(stack.getDamageValue()-points);
				StackUtils.setInt(stack, Reinforced.SHIELD, 0);
			}
		}
	}
	
	private MobEffectInstance getFirstEffect(int min, int max, LivingEntity ent) {
		for(MobEffectInstance mei : ent.getActiveEffects()) 
		{
			int duration = mei.getDuration();
			if(duration > min && duration < max) 
			{
				return mei;
			}
		}
		return null;
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START)
		{
			return;
		}
		Player player = event.player;
		Object2IntMap.Entry<ItemStack> armorResult = MiscUtil.getEquipment(player, UEUtils.ANEMOIS_FRAGMENT, CurioSlot.BACK);
		if(armorResult.getIntValue() > 0 && UEUtils.BOOST_KEY.test(player))
		{
			ItemStack armor = armorResult.getKey();
			int amount = StackUtils.getInt(armor, AnemoiFragment.STORAGE, 0);
			if(amount > 0)
			{
				int lvl = armorResult.getIntValue();
				amount -= (int)(AnemoiFragment.CONSUMPTION.get() / Math.sqrt(AnemoiFragment.CONSUMPTION_SCALE.get(lvl)));
				StackUtils.setInt(armor, AnemoiFragment.STORAGE, Math.max(0, amount));
				player.moveRelative(1F, new Vec3(0F, (float)Math.log10(110 + Math.pow(AnemoiFragment.BOOST.get(lvl * player.experienceLevel), 0.125)) - 2, 0F));
			}
		}
		ItemStack armor = player.getItemBySlot(EquipmentSlot.LEGS);
		if(!armor.isEmpty())
		{
			int level = MiscUtil.getEnchantmentLevel(UEUtils.CLIMBER, armor);
			if(level > 0 && player.onClimbable() && !player.isShiftKeyDown() && !player.isSpectator() && !player.isInWater() && !player.isInLava())
			{
				int motion = -Math.min(0, (int)(player.getRotationVector().x - 25F) / 25);
				if(motion >= 1)
				{
					float data = (1f - ((2 - motion) * 0.1F));
					player.setDeltaMovement(player.getDeltaMovement().add(0, data * 0.075, 0));
				}
			}
		}
		Entity ridden = player.getRootVehicle();
		if(ridden instanceof Horse)
		{
			CompoundTag nbt = ridden.getPersistentData();
			Horse horse = (Horse)ridden;
			AttributeInstance instance = horse.getAttribute(Attributes.MOVEMENT_SPEED);
			int level = MiscUtil.getEnchantmentLevel(UEUtils.PEGASUS_SOUL, ridden.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(EmptyHandler.INSTANCE).getStackInSlot(1));
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
					instance.addPermanentModifier(new AttributeModifier(PegasusSoul.SPEED_MOD, "Pegasus Boost", Math.sqrt(PegasusSoul.SPEED.get(level)) * 0.01D, Operation.MULTIPLY_TOTAL));
				}
			}
			else if(!ridden.level.isClientSide && instance.getModifier(PegasusSoul.SPEED_MOD) != null)
			{
				instance.removeModifier(PegasusSoul.SPEED_MOD);
			}
			if(nbt.getBoolean(PegasusSoul.ENABLED))
			{
				Vec3 vec = player.getDeltaMovement();
				ridden.setDeltaMovement(vec.x, 0D, vec.z);
				ridden.causeFallDamage(ridden.fallDistance, 1F, DamageSource.FALL);
				ridden.fallDistance = 0F;
				ridden.setOnGround(true);
			}
		}
		if(event.side.isClient())
			return;
		long time = player.level.getGameTime();
		if(time % 20 == 0)
		{
			int points = UEUtils.PHANES_UPGRADE.getCombinedPoints(player);
			if(points > 0 && player.getCombatTracker().getKiller() == null)
			{
				int maxStorage = MathCache.LOG.getInt(points);
				CompoundTag data = MiscUtil.getPersistentData(player);
				int shield = data.getInt(PhanesUpgrade.SHIELD_STORAGE);
				if(shield < maxStorage) {
					data.putInt(PhanesUpgrade.SHIELD_STORAGE, shield+1);
					UEBase.NETWORKING.sendToPlayer(new EntityPacket(player.getId(), player.getPersistentData()), player);
				}
			}
			if(player.isPassenger())
			{
				Entity entity = player.getVehicle();
				if(entity instanceof Horse && entity.isAlive())
				{
					Horse horse = (Horse)entity;
					int level = MiscUtil.getEnchantmentLevel(UEUtils.SLEIPNIRS_GRACE, entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null).getStackInSlot(1));
					if(level > 0)
					{
						CompoundTag nbt = entity.getPersistentData();
						long lastTime = nbt.getLong(SleipnirsGrace.HORSE_NBT);
						if(lastTime == 0)
						{
							nbt.putLong(SleipnirsGrace.HORSE_NBT, time);
							lastTime = time;
						}
						Block block = horse.level.getBlockState(new BlockPos(horse.getX(), horse.getY() - 0.20000000298023224D, horse.getZ())).getBlock();
						double bonus = Math.min(SleipnirsGrace.CAP.get() + level, SleipnirsGrace.GAIN.get((time - lastTime) * level));
						AttributeInstance attri = horse.getAttribute(Attributes.MOVEMENT_SPEED);
						attri.removeModifier(SleipnirsGrace.SPEED_MOD);
						attri.addTransientModifier(new AttributeModifier(SleipnirsGrace.SPEED_MOD, "Sleipnirs Grace", Math.log10(10 + ((bonus / SleipnirsGrace.MAX.get()) * (block == Blocks.DIRT_PATH ? SleipnirsGrace.PATH_BONUS.get() : level))) - 1D, Operation.MULTIPLY_TOTAL));
					}
					else
					{
						AttributeInstance attri = horse.getAttribute(Attributes.MOVEMENT_SPEED);
						if(attri.getModifier(SleipnirsGrace.SPEED_MOD) != null)
						{
							attri.removeModifier(SleipnirsGrace.SPEED_MOD);
						}
					}
				}
			}
		}
		int tickRate = Math.max(1, (int)(Reinforced.BASE_DURATION.get() / MathCache.LOG10.get(1000 + player.experienceLevel) - 2));
		if(time % tickRate == 0)
		{
			Inventory inv = player.getInventory();
			// I dislike doing that because its causing enough lag IMO...
			// Efficient but the operation is laggy no matter how much you
			// optimize it
			for(int i = 0, m = inv.getContainerSize();i < m;i++)
			{
				ItemStack stack = inv.getItem(i);
				if(stack.isEmpty())
					continue;
				int level = MiscUtil.getEnchantmentLevel(UEUtils.REINFORCED, stack);
				if(level > 0)
				{
					int shield = StackUtils.getInt(stack, Reinforced.SHIELD, 0);
					if(shield > 0 && stack.getDamageValue() > 0 && (i >= inv.items.size() || i == inv.selected))
					{
						int damage = stack.getDamageValue();
						int left = Math.max(0, shield - damage);
						stack.setDamageValue(Math.max(0, damage - shield));
						shield = left;
						StackUtils.setInt(stack, Reinforced.SHIELD, shield);
					}
					int max = (int)Math.sqrt(625 + player.experienceLevel * level) * 4;
					if(shield < max)
					{
						shield = Math.min(max, shield + Mth.ceil(Math.sqrt(level)));
						StackUtils.setInt(stack, Reinforced.SHIELD, shield);
					}
				}
			}
		}
		CompoundTag nbt = player.getPersistentData();
		if(nbt.contains(Climber.CLIMB_POS) && time >= nbt.getLong(Climber.CLIMB_START) + nbt.getLong(Climber.CLIMB_DELAY))
		{
			nbt.remove(Climber.CLIMB_DELAY);
			nbt.remove(Climber.CLIMB_START);
			BlockPos pos = BlockPos.of(nbt.getLong(Climber.CLIMB_POS));
			nbt.remove(Climber.CLIMB_POS);
			player.teleportTo(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
		}
		int level = MiscUtil.getCombinedEnchantmentLevel(UEUtils.FAMINES_ODIUM, player);
		if(level > 0)
		{
			int duration = (int)Math.max((FaminesOdium.DELAY.get() / Math.pow(level, 0.125D)), 1);
			if(time % duration == 0)
			{
				FoodEntry entry = FaminesOdium.getRandomFood(player, player.getRandom());
				if(entry != null) entry.eat(player);
			}
		}
		int delay = Math.max(1, Mth.ceil(DemetersSoul.DELAY.get() / Math.log(10 + DemetersSoul.SCALING.get(MiscUtil.getEnchantmentLevel(UEUtils.DEMETERS_SOUL, player.getItemInHand(InteractionHand.MAIN_HAND))))));
		if(player.level.getGameTime() % delay == 0)
		{
			HarvestEntry entry = DemetersSoul.getNextIndex(player);
			if(entry != null)
			{
				InteractionResult result = entry.harvest(player.level, player);
				if(result == InteractionResult.FAIL)
				{
					ListTag list = DemetersSoul.getCrops(player);
					for(int i = 0, m = list.size();i < m;i++)
					{
						if(entry.matches(list.getCompound(i)))
						{
							list.remove(i--);
							break;
						}
					}
				}
				else if(result == InteractionResult.SUCCESS)
				{
					player.causeFoodExhaustion(0.06F);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onDeath(LivingDeathEvent event)
	{
		LivingEntity deadEntity = event.getEntity();
		boolean slime = deadEntity instanceof Slime && deadEntity.getRandom().nextInt(100) < 1;
		boolean horse = deadEntity instanceof Horse && deadEntity.getRandom().nextInt(100) < 3;
		if((slime || horse) && event.getSource() == DamageSource.FALL) {
			if(slime) MiscUtil.spawnDrops(deadEntity, UEUtils.ESSENCE_OF_SLIME, 1);
			else MiscUtil.spawnDrops(deadEntity, UEUtils.PEGASUS_SOUL, 1);
		}
	}
	
	@SubscribeEvent
	public void onFall(LivingFallEvent event)
	{
		ItemStack stack = event.getEntity().getItemBySlot(EquipmentSlot.FEET);
		int level = MiscUtil.getEnchantmentLevel(UEUtils.ESSENCE_OF_SLIME, stack);
		if(level > 0)
		{
			if(event.getDistance() <= 1.3)
				return;
			int damage = Mth.floor(((event.getDistance() - 2) * EssenceOfSlime.DURABILITY_LOSS.get() / (level + MiscUtil.getEnchantmentLevel(Enchantments.FALL_PROTECTION, stack))));
			if(damage > 0)
			{
				stack.hurtAndBreak(damage, event.getEntity(), MiscUtil.get(EquipmentSlot.FEET));
			}
			LivingEntity entity = event.getEntity();
			entity.getPersistentData().putDouble("bounce", entity.getDeltaMovement().y() / Math.log10(15.5D + EssenceOfSlime.FALL_DISTANCE_MULTIPLIER.get(entity.getDeltaMovement().y())) * -1D);
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onEntityTick(LivingTickEvent event)
	{
		if(event.getEntity() instanceof Player == event.getEntity().level.isClientSide)
		{
			CompoundTag data = event.getEntity().getPersistentData();
			if(data.contains("bounce"))
			{
				Vec3 motion = event.getEntity().getDeltaMovement();
				event.getEntity().setDeltaMovement(motion.x(), data.getDouble("bounce"), motion.z());
				data.remove("bounce");
			}
		}
	}
	
	@SubscribeEvent
	public void onEaten(LivingEntityUseItemEvent.Finish event)
	{
		if(event.getEntity() instanceof Player player)
		{
			int level = MiscUtil.getEnchantmentLevel(UEUtils.AMBROSIA, event.getItem());
			if(level > 0)
			{
				int duration = (int)(Ambrosia.BASE_DURATION.get() + (Math.log(MathCache.POW5.get(1 + player.experienceLevel * level)) * Ambrosia.DURATION_MULTIPLIER.get()));
				player.getFoodData().eat(2000, 0);
				if(!player.getCombatTracker().isInCombat() && Ambrosia.HEALING.get()) {
					player.setHealth(player.getMaxHealth());
				}
				player.addEffect(new MobEffectInstance(UEUtils.SATURATION, duration, Math.min(20, level)));
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		if(event.getAmount() >= 1F)
		{
			if(PHANES_REGRET_ACTIVE.get())
				return;
			PHANES_REGRET_ACTIVE.set(true);
			int level = MiscUtil.getCombinedEnchantmentLevel(UEUtils.PHANES_REGRET, event.getEntity());
			if(level > 0)
			{
				if(event.getEntity() instanceof Player)
				{
					MiscUtil.drainExperience((Player)event.getEntity(), Mth.ceil(event.getAmount()));
				}
				event.getEntity().heal(event.getAmount() * (1F - (1F / MathCache.LOG_ADD.getFloat(level))));
			}
			PHANES_REGRET_ACTIVE.set(false);
		}
		if(event.getSource().isMagic())
		{
			CompoundTag data = MiscUtil.getPersistentData(event.getEntity());
			int points = data.getInt(PhanesUpgrade.SHIELD_STORAGE);
			if(points > 0)
			{
				int toRemove = Mth.ceil(event.getAmount());
				data.putInt(PhanesUpgrade.SHIELD_STORAGE, Math.max(0, points - toRemove));
				event.setAmount(Math.max(0F, toRemove - points));
				if(event.getEntity() instanceof Player)
				{
					Player player = (Player)event.getEntity();
					UEBase.NETWORKING.sendToPlayer(new EntityPacket(player.getId(), player.getPersistentData()), player);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onHeal(LivingHealEvent event)
	{
		if(PHANES_REGRET_ACTIVE.get())
			return;
		PHANES_REGRET_ACTIVE.set(true);
		if(event.getAmount() >= 1F)
		{
			int level = MiscUtil.getCombinedEnchantmentLevel(UEUtils.PHANES_REGRET, event.getEntity());
			if(level > 0)
			{
				event.getEntity().hurt(DamageSource.MAGIC, event.getAmount() * (1F - (1F / MathCache.LOG_ADD.getFloat(level))));
			}
		}
		PHANES_REGRET_ACTIVE.set(false);
	}
	
	@SubscribeEvent
	public void onRepair(AnvilRepairEvent event)
	{
		if(event.getEntity() == null) return;
		if(event.getLeft().getDamageValue() > event.getOutput().getDamageValue() && event.getEntity().getRandom().nextInt(100) < 2)
		{
			MiscUtil.spawnDrops(event.getEntity(), UEUtils.REINFORCED, Mth.nextInt(event.getEntity().getRandom(), 3, 5));
		}
	}
	
	
	@SubscribeEvent
	public void onArrowHit(ProjectileImpactEvent event)
	{
		if(!(event.getEntity() instanceof ThrownTrident arrow) || !(arrow.getOwner() instanceof Player player))
			return;
		HitResult result = event.getRayTraceResult();
		if(!(result instanceof BlockHitResult))
			return;
		BlockHitResult ray = (BlockHitResult)result;
		Level world = event.getEntity().getCommandSenderWorld();
		BlockState state = world.getBlockState(ray.getBlockPos());
		if(PoseidonsSoul.isValid(state))
		{
			ItemStack arrowStack = StackUtils.getArrowStack(arrow);
			if(arrowStack.getItem() != Items.TRIDENT)
				return;
			Object2IntMap<Enchantment> enchs = MiscUtil.getEnchantments(arrowStack);
			int level = enchs.getInt(UEUtils.POSEIDONS_SOUL);
			if(level <= 0)
				return;
			ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
			stack.enchant(Enchantments.SILK_TOUCH, 1);
			Block.popResource(world, ray.getBlockPos(), new ItemStack(state.getBlock(), level * (world.random.nextInt(enchs.getInt(Enchantments.BLOCK_FORTUNE) + 1) + 1)));
			MiscUtil.drainExperience(player, (int)Math.log10(10 + Math.pow(PoseidonsSoul.BASE_CONSUMTION.get() + level, level)));
		}
	}
	
	@SubscribeEvent
	public void onItemClick(RightClickItem event)
	{
		if(event.getEntity() == null || !event.getEntity().isShiftKeyDown())
			return;
		Player player = event.getEntity();
		ItemStack stack = event.getItemStack();
		int level = MiscUtil.getEnchantmentLevel(UEUtils.RESONANCE, stack);
		if(level > 0)
		{
			if(!event.getLevel().isClientSide)
			{
				CompoundTag nbt = player.getPersistentData();
				if(nbt.getLong(Resonance.COOLDOWN_TAG) > event.getLevel().getGameTime())
				{
					int used = nbt.getInt(Resonance.OVERUSED_TAG);
					player.hurt(DamageSource.MAGIC, 0.5F * used);
					nbt.putInt(Resonance.OVERUSED_TAG, used + 1);
					if(!player.level.isClientSide)
						player.displayClientMessage(Component.translatable("tooltip.uniqueutil.resonance.cooldown", (nbt.getLong(Resonance.COOLDOWN_TAG) - event.getLevel().getGameTime()) / 20), true);
				}
				else
				{
					Resonance.addResonance(event.getLevel(), event.getPos(), (int)(Resonance.RANGE.getSqrt(level * Math.sqrt(player.experienceLevel))));
					nbt.putLong(Resonance.COOLDOWN_TAG, event.getLevel().getGameTime() + Resonance.COOLDOWN.get());
					nbt.putInt(Resonance.OVERUSED_TAG, 1);
				}
			}
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getEntity().isShiftKeyDown())
		{
			BlockState state = event.getLevel().getBlockState(event.getPos());
			if(state.getBlock().isLadder(state, event.getLevel(), event.getPos(), event.getEntity()))
			{
				int level = MiscUtil.getEnchantmentLevel(UEUtils.CLIMBER, event.getEntity().getItemBySlot(EquipmentSlot.LEGS));
				if(level > 0 && MiscUtil.isTranscendent(event.getEntity(), event.getEntity().getItemBySlot(EquipmentSlot.LEGS), UEUtils.CLIMBER))
				{
					MutableBlockPos pos = new MutableBlockPos().set(event.getPos());
					List<Block> blocks = new ObjectArrayList<>();
					do
					{
						pos.move(Direction.UP);
						state = event.getLevel().getBlockState(pos);
						blocks.add(state.getBlock());
					}
					while(state.getBlock().isLadder(state, event.getLevel(), pos, event.getEntity()));
					if(!event.getLevel().getBlockState(pos.above()).isCollisionShapeFullBlock(event.getLevel(), pos.above()) && !event.getLevel().getBlockState(pos.above(2)).isCollisionShapeFullBlock(event.getLevel(), pos.above(2)))
					{
						CompoundTag nbt = event.getEntity().getPersistentData();
						boolean wasClimbing = nbt.getLong(Climber.CLIMB_START) != 0;
						nbt.putLong(Climber.CLIMB_POS, pos.asLong());
						nbt.putInt(Climber.CLIMB_DELAY, Climber.getClimbTime(level, blocks));
						nbt.putLong(Climber.CLIMB_START, event.getLevel().getGameTime());
						event.getEntity().displayClientMessage(Component.translatable("tooltip.uniqueutil.climb." + (wasClimbing ? "re" : "") + "start.name"), true);
						event.setCancellationResult(InteractionResult.SUCCESS);
					}
					else
					{
						event.getEntity().displayClientMessage(Component.translatable("tooltip.uniqueutil.climb.fail.name"), true);
					}
				}
			}
			else if(state.getBlock() == Blocks.ANVIL)
			{
				ItemStack stack = event.getItemStack();
				Player player = event.getEntity();
				Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(stack);
				int level = ench.getInt(UEUtils.SAGES_SOUL);
				if(level > 0)
				{
					int stored = StackUtils.getInt(stack, SagesSoul.STORED_XP, 0);
					int required = MiscUtil.getXPForLvl(stored + 10);
					if(player.totalExperience >= required || player.isCreative())
					{
						MiscUtil.drainExperience(player, required);
						StackUtils.setInt(stack, SagesSoul.STORED_XP, stored + 5);
						event.setCancellationResult(InteractionResult.SUCCESS);
					}
					else
					{
						if(!player.level.isClientSide)
							player.displayClientMessage(Component.translatable("tooltip.uniqueutil.missing.xp"), true);
						event.setCancellationResult(InteractionResult.FAIL);
					}
					event.setCanceled(true);
				}
			}
			else if(state.getBlock() == Blocks.BEACON)
			{
				Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(event.getItemStack());
				int level = ench.getInt(UEUtils.ALCHEMISTS_BLESSING);
				if(level > 0 && !event.getLevel().isClientSide)
				{
					BlockEntity tile = event.getLevel().getBlockEntity(event.getPos());
					if(tile instanceof BeaconBlockEntity)
					{
						BeaconBlockEntity beacon = (BeaconBlockEntity)tile;
						if(beacon.saveWithoutMetadata().getInt("Primary") == -1)
							return;
						Level world = beacon.getLevel();
						BlockPos pos = beacon.getBlockPos().above();
						List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(beacon.getBlockPos().above()));
						if(items.isEmpty())
							return;
						int consumed = 0;
						int xpToSpawn = 0;
						world.playSound(event.getEntity(), pos.above(), UEUtils.ALCHEMIST_BLESSING_SOUND, SoundSource.BLOCKS, 100F, 1F);
						for(ItemEntity item : items)
						{
							ItemStack stack = item.getItem();
							consumed += stack.getCount();
							item.remove(RemovalReason.DISCARDED);
							item.setNeverPickUp();
							ConversionEntry entry = AlchemistsBlessing.RECIPES.get(stack.getItem());
							if(entry == null)
							{
								xpToSpawn += stack.getCount();
								continue;
							}
							List<ItemStack> newDrops = new ObjectArrayList<ItemStack>();
							entry.generateOutput(world.random, level - 1, Math.min(world.random.nextInt(Math.max(ench.getInt(Enchantments.BLOCK_FORTUNE), ench.getInt(Enchantments.MOB_LOOTING)) + 1), level), stack.getCount(), newDrops);
							for(ItemStack drop : newDrops)
							{
								world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, drop));
							}
						}
						if(xpToSpawn > 0)
						{
							world.addFreshEntity(new ExperienceOrb(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, Mth.ceil(Math.sqrt(AlchemistsBlessing.BASE_XP_USAGE.get(level)) + Math.sqrt(AlchemistsBlessing.LVL_XP_USAGE.get(level * world.random.nextDouble()))) * xpToSpawn));
							
						}
						consumed = Mth.ceil(Math.pow((level * consumed * AlchemistsBlessing.CONSUMTION.get()), 0.6505));
						int stored = StackUtils.getInt(event.getItemStack(), AlchemistsBlessing.STORED_REDSTONE, 0) - consumed;
						StackUtils.setInt(event.getItemStack(), AlchemistsBlessing.STORED_REDSTONE, Math.max(0, stored));
						if(stored < 0)
							event.getItemStack().hurtAndBreak(-stored, event.getEntity(), MiscUtil.get(event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
						event.setCancellationResult(InteractionResult.SUCCESS);
						event.setCanceled(true);
					}
				}
			}
			else
			{
				int level = MiscUtil.getEnchantmentLevel(UEUtils.DEMETERS_SOUL, event.getItemStack());
				if(level > 0 && CropHarvestRegistry.INSTANCE.isValid(state.getBlock()) && !event.getLevel().isClientSide)
				{
					HarvestEntry entry = new HarvestEntry(event.getLevel().dimension().location(), event.getPos().asLong());
					ListTag list = DemetersSoul.getCrops(event.getEntity());
					boolean found = false;
					for(int i = 0, m = list.size();i < m;i++)
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
						if(list.size() >= DemetersSoul.CAP.get())
						{
							event.getEntity().displayClientMessage(Component.translatable("tooltip.uniqueutil.crops.full.name"), false);
							event.setCancellationResult(InteractionResult.SUCCESS);
							event.setCanceled(true);
							return;
						}
						list.add(entry.save());
					}
					event.getEntity().displayClientMessage(Component.translatable("tooltip.uniqueutil.crops." + (found ? "removed" : "added") + ".name"), false);
					event.setCancellationResult(InteractionResult.SUCCESS);
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
				BlockState state = event.getLevel().getBlockState(event.getPos());
				if(state.getBlock() instanceof CropBlock)
				{
					CropBlock crops = (CropBlock)state.getBlock();
					if(crops.isMaxAge(state))
					{
						Block.dropResources(state, event.getLevel(), event.getPos(), event.getLevel().getBlockEntity(event.getPos()), event.getEntity(), event.getItemStack());
						event.getLevel().setBlockAndUpdate(event.getPos(), crops.getStateForAge(0));
						event.getItemStack().hurtAndBreak(1, event.getEntity(), T -> T.broadcastBreakEvent(event.getHand()));
					}
				}
				else if(state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.DIRT_PATH || state.getBlock() == Blocks.GRASS_BLOCK)
				{
					event.getItemStack().hurt(-ench.getInt(UEUtils.DETEMERS_BLESSING), event.getLevel().random, null);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLeftHorse(EntityMountEvent event)
	{
		if(event.getEntityBeingMounted() instanceof Horse)
		{
			Horse horse = (Horse)event.getEntityBeingMounted();
			horse.getPersistentData().remove(SleipnirsGrace.HORSE_NBT);
			horse.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SleipnirsGrace.SPEED_MOD);
		}
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
		
		int level = UEUtils.THICK_UPGRADE.getPoints(held);
		if(level > 0)
		{
			event.setNewSpeed(event.getNewSpeed() + (float)Math.log(1+level));
		}
		level = ench.getInt(UEUtils.THICK_PICK);
		if(level > 0 && held.isCorrectToolForDrops(event.getState()))
		{
			int amount = StackUtils.getInt(held, ThickPick.TAG, 0);
			if(amount > 0)
			{
				float hard = event.getState().getDestroySpeed(player.level, event.getPosition().orElse(BlockPos.ZERO));
				if(MiscUtil.isTranscendent(player, held, UEUtils.THICK_PICK)) 
				{
					System.out.println(hard);
					event.setNewSpeed((float) (event.getNewSpeed() + Math.sqrt(hard)));
				}
				if(hard >= 20) {
					event.setNewSpeed(event.getNewSpeed() * ThickPick.MINING_SPEED.getAsFloat(level));
				}
			}
		}
		level = ench.getInt(UEUtils.SAGES_SOUL);
		if(level > 0)
		{
			double power = SagesSoul.getEnchantPower(held, level);
			int levels = StackUtils.getInt(held, SagesSoul.STORED_XP, 0);
			event.setNewSpeed((int) (event.getNewSpeed() * Math.log10(10+Math.pow(SagesSoul.MINING_SPEED.get(power*levels), 0.35))));
		}
		level = MiscUtil.getEnchantmentLevel(UEUtils.ADEPT, player.getItemBySlot(EquipmentSlot.HEAD));
		if(level > 0)
		{
			boolean inWater = player.isEyeInFluidType(ForgeMod.WATER_TYPE.get());
			event.setNewSpeed(event.getNewSpeed() * (player.isOnGround() ? 1F : 5F) * (inWater && !EnchantmentHelper.hasAquaAffinity(player) ? 5F : 1F));
			if(inWater || !player.isOnGround())
			{
				event.setNewSpeed((float)(event.getNewSpeed() * (1+(-Math.pow(0.5, Adept.SPEED_SCALE.get(level))))));
			}
		}
		level = ench.getInt(UEUtils.REINFORCED);
		if(level > 0)
		{
			event.setNewSpeed((float)(event.getNewSpeed() * Math.pow(1-Reinforced.BASE_REDUCTION.get(), level)));
		}
	}
	
	@SubscribeEvent
	public void onItemUseStart(LivingEntityUseItemEvent.Start event)
	{
		int level = MiscUtil.getEnchantmentLevel(UEUtils.SAGES_SOUL, event.getItem());
		if(level > 0)
		{
			double power = SagesSoul.getEnchantPower(event.getItem(), level);
			int levels = StackUtils.getInt(event.getItem(), SagesSoul.STORED_XP, 0);
			event.setDuration((int)(event.getDuration() / Math.log10(10+Math.pow(SagesSoul.DRAW_SPEED.get(power*levels), 0.25))));
		}
	}
	
	@SubscribeEvent
	public void onBlockBroken(BreakEvent event)
	{
		if(event.getPlayer() == null)
		{
			return;
		}
		Player player = event.getPlayer();
		ItemStack held = player.getMainHandItem();
		int level = MiscUtil.getEnchantmentLevel(UEUtils.THICK_PICK, held);
		if(level > 0)
		{
			int amount = StackUtils.getInt(held, ThickPick.TAG, 0);
			if(amount > 0)
			{
				int i = 0;
				if(MiscUtil.isTranscendent(player, held, UEUtils.THICK_PICK) && event.getLevel().getRandom().nextDouble() < (player.getDigSpeed(event.getState(), event.getPos())/Math.sqrt(30*event.getState().getDestroySpeed(player.getLevel(), event.getPos())))) 
				{
					held.setDamageValue(held.getDamageValue()-(++i));
				}
				StackUtils.setInt(held, ThickPick.TAG, amount - (1+i));
			}
		}
	}
	
	@SubscribeEvent
	public void onItemAirClick(RightClickItem event)
	{
		ItemStack stack = event.getItemStack();
		if(stack.getItem() == Items.FIREWORK_ROCKET && event.getEntity() != null && !event.getEntity().isFallFlying())
		{
			Object2IntMap.Entry<ItemStack> result = MiscUtil.getEquipment(event.getEntity(), UEUtils.ROCKET_MAN, CurioSlot.BACK);
			if(result.getKey().getItem() instanceof ElytraItem && result.getIntValue() > 0)
			{
				if(!event.getLevel().isClientSide)
				{
					event.getLevel().addFreshEntity(new FireworkRocketEntity(event.getLevel(), stack, event.getEntity()));
				}
				else
				{
					Player player = event.getEntity();
					event.getEntity().teleportTo(player.getX(), player.getY() + 0.5D, player.getZ());
				}
				((EntityMixin)event.getEntity()).setFlag(7, true);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinLevelEvent event)
	{
		if(event.getEntity() instanceof FireworkRocketEntity rocket)
		{
			try
			{
				LivingEntity entity = ((FireworkMixin)rocket).getRidingEntity();
				if(entity != null)
				{
					Object2IntMap.Entry<ItemStack> result = MiscUtil.getEquipment(entity, UEUtils.ROCKET_MAN, CurioSlot.BACK);
					if(result.getKey().getItem() instanceof ElytraItem && result.getIntValue() > 0)
					{
						int lvl = result.getIntValue();
						CompoundTag nbt = new CompoundTag();
						rocket.addAdditionalSaveData(nbt);
						int time = nbt.getInt("LifeTime");
						time += time * RocketMan.FLIGHT_TIME.getAsDouble(lvl) * MathCache.LOG_ADD_MAX.get(lvl);
						nbt.putInt("LifeTime", time);
						rocket.readAdditionalSaveData(nbt);
					}
				}
			}
			catch(Exception e)
			{
				UEBase.LOGGER.warn("Rocketman Enchantment failed to apply bonus Lifetime");
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityAttack(LivingAttackEvent event)
	{
		Entity entity = event.getSource().getEntity();
		if(entity instanceof LivingEntity living)
		{
			if(!entity.level.isClientSide())
			{
				int points = UEUtils.FAMINES_UPGRADE.getPoints(living.getMainHandItem());
				if(points > 0)
				{
					RandomSource random = living.getRandom();
					int num = (int) Math.log(1+points)*20;
					do
					{
						MobEffect effect = getRandomNegativeEffect(random);
						if(effect != null)
						{
							event.getEntity().addEffect(new MobEffectInstance(effect, num));
						}
					} while(random.nextDouble() <= (num-=100)/100);
				}
			}
		}
		if(event.getAmount() > 0F)
		{
			for(Player player : getPlayers(event.getEntity()))
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
		AttributeMap attribute = event.getEntity().getAttributes();
		Multimap<Attribute, AttributeModifier> mods = createModifiersFromStack(event.getFrom(), event.getEntity(), event.getSlot(), true);
		if(!mods.isEmpty())
		{
			attribute.removeAttributeModifiers(mods);
		}
		mods = createModifiersFromStack(event.getTo(), event.getEntity(), event.getSlot(), false);
		if(!mods.isEmpty())
		{
			attribute.addTransientAttributeModifiers(mods);
		}
	}
	
	private Multimap<Attribute, AttributeModifier> createModifiersFromStack(ItemStack stack, LivingEntity living, EquipmentSlot slot, boolean remove)
	{
		Multimap<Attribute, AttributeModifier> mods = HashMultimap.create();
		Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(stack);
		int level = ench.getInt(UEUtils.REINFORCED);
		if(level > 0)
		{
			double base = Reinforced.BASE_REDUCTION.get();
			mods.put(Attributes.ATTACK_SPEED, new AttributeModifier(Reinforced.SPEED_MOD, "Reinforced Boost", Math.pow(1D - base, level), Operation.MULTIPLY_BASE));
		}
		level = ench.getInt(UEUtils.SAGES_SOUL);
		if(level > 0)
		{
			int levels = StackUtils.getInt(stack, SagesSoul.STORED_XP, 0);
			double power = SagesSoul.getEnchantPower(stack, level);
			mods.put(Attributes.ATTACK_SPEED, new AttributeModifier(SagesSoul.ATTACK_MOD.getId(slot), "Sage Attack Boost", Math.log10(10+Math.pow(SagesSoul.ATTACK_SPEED.get(power*levels), 0.25))-1, Operation.MULTIPLY_TOTAL));
			mods.put(Attributes.ARMOR, new AttributeModifier(SagesSoul.ARMOR_MOD.getId(slot), "Sages Armor Boost", Math.log10(10+Math.pow(SagesSoul.ARMOR_SCALE.get(power*levels), 0.125))-1, Operation.MULTIPLY_TOTAL));
			mods.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(SagesSoul.TOUGHNESS_MOD.getId(slot), "Sages Toughness Boost", Math.log10(10+Math.pow(SagesSoul.TOUGHNESS_SCALE.get(power*levels), 0.125))-1, Operation.MULTIPLY_TOTAL));
		}
		level = UEUtils.ROCKET_UPGRADE.getCombinedPoints(living);
		if(level > 0 || remove)
		{
			mods.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(RocketUpgrade.SPEED_MOD, "Rocket Upgrade", Math.log(1+level)/100, Operation.MULTIPLY_TOTAL));
		}
		return mods;
	}
	
	public List<Player> getPlayers(Entity original)
	{
		List<Player> players = new ObjectArrayList<>();
		for(Entity entity : original.getIndirectPassengers())
		{
			if(entity instanceof Player)
				players.add((Player)entity);
		}
		return players;
	}
	
	public static void damageShield(float damage, Player player)
	{
		if(damage >= 3.0F && player.getUseItem().is(Tags.Items.TOOLS_SHIELDS))
		{
			ItemStack copyBeforeUse = player.getUseItem().copy();
			int i = 1 + Mth.floor(damage);
			player.getUseItem().hurtAndBreak(i, player, T -> T.broadcastBreakEvent(player.getUsedItemHand()));
			if(player.getUseItem().isEmpty())
			{
				InteractionHand hand = player.getUsedItemHand();
				net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, hand);
				
				if(hand == InteractionHand.MAIN_HAND)
				{
					player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				}
				else
				{
					player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
				}
				
				player.stopUsingItem();
				player.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + player.level.random.nextFloat() * 0.4F);
			}
		}
	}
	
	public static boolean canBlockDamageSource(DamageSource damageSourceIn, Player player)
	{
		if(!damageSourceIn.isBypassArmor() && player.isBlocking())
		{
			if(MiscUtil.getEnchantmentLevel(UEUtils.MOUNTING_AEGIS, player.getUseItem()) <= 0)
				return false;
			Vec3 Vector3d = damageSourceIn.getSourcePosition();
			
			if(Vector3d != null)
			{
				Vec3 Vector3d1 = player.getViewVector(1.0F);
				Vec3 Vector3d2 = Vector3d.vectorTo(new Vec3(player.getX(), player.getY(), player.getZ())).normalize();
				Vector3d2 = new Vec3(Vector3d2.x, 0.0D, Vector3d2.z);
				if(Vector3d2.dot(Vector3d1) < 0.0D)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private MobEffect getRandomNegativeEffect(RandomSource rand)
	{
		List<MobEffect> effects = UEUtils.NEGATIVE_EFFECTS;
		return effects.isEmpty() ? null : effects.get(rand.nextInt(effects.size()));
	}
}
