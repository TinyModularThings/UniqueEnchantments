package uniqueeutils.handler;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import uniquebase.UniqueEnchantmentsBase;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.handler.MathCache;
import uniquebase.networking.EntityPacket;
import uniquebase.utils.HarvestEntry;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniqueeutils.UniqueEnchantmentsUtils;
import uniqueeutils.enchantments.complex.AlchemistsBlessing;
import uniqueeutils.enchantments.complex.AlchemistsBlessing.ConversionEntry;
import uniqueeutils.enchantments.complex.Ambrosia;
import uniqueeutils.enchantments.complex.Climber;
import uniqueeutils.enchantments.complex.EssenceOfSlime;
import uniqueeutils.enchantments.complex.SleipnirsGrace;
import uniqueeutils.enchantments.curse.FaminesOdium;
import uniqueeutils.enchantments.curse.PhanesRegret;
import uniqueeutils.enchantments.curse.RocketMan;
import uniqueeutils.enchantments.simple.Adept;
import uniqueeutils.enchantments.simple.ThickPick;
import uniqueeutils.enchantments.unique.AnemoiFragment;
import uniqueeutils.enchantments.unique.DemetersSoul;
import uniqueeutils.enchantments.unique.PegasusSoul;
import uniqueeutils.enchantments.unique.Reinforced;
import uniqueeutils.enchantments.unique.Resonance;
import uniqueeutils.enchantments.unique.SagesSoul;
import uniqueeutils.misc.RenderEntry;

@SuppressWarnings("deprecation")
public class UtilsHandler
{
	public static final UtilsHandler INSTANCE = new UtilsHandler();
	private List<RenderEntry> toRender = new ObjectArrayList<>();
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onTick(ClientTickEvent event)
	{
		if(event.phase == Phase.START) return;
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.world == null || mc.player == null) 
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
		int level = MiscUtil.getEnchantmentLevel(ench, mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
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
	@SideOnly(Side.CLIENT)	
	public void onRender(RenderWorldLastEvent event)
	{
		if(toRender.isEmpty()) return;
		Minecraft mc = Minecraft.getMinecraft();
		Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation("uniquee", "treasurers_eyes"));
		if(ench == null) return;
		int level = MiscUtil.getEnchantmentLevel(ench, mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
		if(level <= 0) return;
		ClippingHelper helper = ClippingHelperImpl.getInstance();
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder buffer = tes.getBuffer();
		buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
		for(int i = 0,m=toRender.size();i<m;i++)
		{
			toRender.get(i).render(helper, buffer);
		}
		EntityPlayer player = Minecraft.getMinecraft().player;
		
		float particalTicks = event.getPartialTicks();
		double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * particalTicks;
		double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * particalTicks;
		double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * particalTicks;
		GL11.glPushMatrix();
		GlStateManager.translate(-x, -y, -z);
		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		tes.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.enableDepth();
		GL11.glPopMatrix();
	}
	
	public void addDrawPosition(BlockPos pos)
	{
		toRender.add(new RenderEntry(pos, 0x33BBBBBB, 140));
	}
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event)
	{
		if(event.world.isRemote) return;
		Resonance.tick(event.world);
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START)
		{
			return;
		}
		EntityPlayer player = event.player;
		ItemStack armor = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if(!armor.isEmpty())
		{
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.ANEMOIS_FRAGMENT, armor);
			if(level > 0 && UniqueEnchantmentsUtils.PROXY.isBoostKeyDown(player))
			{
				int amount = StackUtils.getInt(armor, AnemoiFragment.STORAGE, 0);
				if(amount > 0)
				{
					amount -= (int)(AnemoiFragment.CONSUMPTION.get()/Math.sqrt(AnemoiFragment.CONSUMPTION_SCALE.get(level)));
					StackUtils.setInt(armor, AnemoiFragment.STORAGE, Math.max(0, amount));
					player.moveRelative(0F, (float)Math.log10(110+Math.pow(AnemoiFragment.BOOST.get(level*player.experienceLevel), 0.125))-2, 0F, 1F);
				}
			}
		}
		Entity ridden = player.getLowestRidingEntity();
		if(ridden instanceof EntityHorse)
		{
			NBTTagCompound nbt = ridden.getEntityData();
			EntityHorse horse = (EntityHorse)ridden;
			IAttributeInstance instance = horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.PEGASUS_SOUL, ridden.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(1));
			if(level > 0)
			{
				boolean active = UniqueEnchantmentsUtils.PROXY.isBoostKeyDown(player);
				if(active && !nbt.getBoolean(PegasusSoul.TRIGGER) && (!player.onGround || nbt.getBoolean(PegasusSoul.ENABLED)))
				{
					nbt.setBoolean(PegasusSoul.ENABLED, !nbt.getBoolean(PegasusSoul.ENABLED));
					nbt.setBoolean(PegasusSoul.TRIGGER, true);
					UniqueEnchantmentsBase.NETWORKING.sendToPlayer(new EntityPacket(ridden.getEntityId(), nbt), player);
				}
				else if(!active)
				{
					nbt.setBoolean(PegasusSoul.TRIGGER, false);
				}
				if(instance.getModifier(PegasusSoul.SPEED_MOD) == null)
				{
					instance.applyModifier(new AttributeModifier(PegasusSoul.SPEED_MOD, "Pegasus Boost", Math.sqrt(PegasusSoul.SPEED.get(level))*0.01D, 2));
				}
			}
			else if(!ridden.world.isRemote && instance.getModifier(PegasusSoul.SPEED_MOD) != null)
			{
				instance.removeModifier(PegasusSoul.SPEED_MOD);
			}
			if(nbt.getBoolean(PegasusSoul.ENABLED))
			{
				ridden.motionY = 0D;
				ridden.fall(ridden.fallDistance, 1F);
				ridden.fallDistance = 0F;
				ridden.onGround = true;
			}
		}
		if(event.side == Side.CLIENT) return;
		long time = player.world.getTotalWorldTime();
		if(player.isRiding() && time % 20 == 0)
		{
			Entity entity = player.getRidingEntity();
			if(entity instanceof EntityHorse)
			{
				EntityHorse horse = (EntityHorse)entity;
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.SLEIPNIRS_GRACE, entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(1));
				if(level > 0)
				{
					NBTTagCompound nbt = entity.getEntityData();
					long lastTime = nbt.getLong(SleipnirsGrace.HORSE_NBT);
					if(lastTime == 0)
					{
						nbt.setLong(SleipnirsGrace.HORSE_NBT, time);
						lastTime = time;
					}
					Block block = horse.world.getBlockState(new BlockPos(horse.posX, horse.posY - 0.20000000298023224D,  horse.posZ)).getBlock();
					double bonus = Math.min(SleipnirsGrace.CAP.get()+level, SleipnirsGrace.GAIN.get((time - lastTime) * level));
					IAttributeInstance attri = horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
					attri.removeModifier(SleipnirsGrace.SPEED_MOD);
					attri.applyModifier(new AttributeModifier(SleipnirsGrace.SPEED_MOD, "Sleipnirs Grace", Math.log10(10 + ((bonus / SleipnirsGrace.MAX.get()) * (block == Blocks.GRASS_PATH ? SleipnirsGrace.PATH_BONUS.get() : level)))-1D, 2));
				}
				else
				{
					IAttributeInstance attri = horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
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
			InventoryPlayer inv = player.inventory;
			// I dislike doing that because its causing enough lag IMO... Efficient but the operation is laggy no matter how much you optimize it
			for(int i = 0,m=inv.getSizeInventory();i<m;i++)
			{
				ItemStack stack = inv.getStackInSlot(i);
				if(stack.isEmpty()) continue;
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.REINFORCED, stack);
				if(level > 0)
				{
					int shield = StackUtils.getInt(stack, Reinforced.SHIELD, 0);
					if(shield > 0 && stack.getItemDamage() > 0 && (i >= inv.mainInventory.size() || i == inv.currentItem))
					{
						int damage = stack.getItemDamage();
						int left = Math.max(0, shield-damage);
						stack.setItemDamage(Math.max(0, damage-shield));
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
		NBTTagCompound nbt = player.getEntityData();
		if(nbt.hasKey(Climber.CLIMB_POS) && time >= nbt.getLong(Climber.CLIMB_START) + nbt.getLong(Climber.CLIMB_DELAY))
		{
			nbt.removeTag(Climber.CLIMB_DELAY);
			nbt.removeTag(Climber.CLIMB_START);
			BlockPos pos = BlockPos.fromLong(nbt.getLong(Climber.CLIMB_POS));
			nbt.removeTag(Climber.CLIMB_POS);
			player.setPositionAndUpdate(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
		}
		int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsUtils.FAMINES_ODIUM, player);
		if(level > 0)
		{
			int duration = (int)Math.max((FaminesOdium.DELAY.get() / Math.pow(level, 0.125D)), 1);
			if(time % duration == 0)
			{
				Int2FloatMap.Entry entry = FaminesOdium.consumeRandomItem(player.inventory, FaminesOdium.NURISHMENT.getFloat(level));
				if(entry != null)
				{
					double value = MathCache.LOG_ADD_MAX.get(level);
					player.getFoodStats().addStats(MathHelper.ceil(FaminesOdium.NURISHMENT.get(entry.getIntKey() * value)), (float)(entry.getFloatValue() * level * value));
					player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, player.world.rand.nextFloat() * 0.1F + 0.9F);
				}
				else
				{
					player.attackEntityFrom(DamageSource.MAGIC, FaminesOdium.DAMAGE.getFloat(duration * MathCache.LOG_ADD_MAX.getFloat(level)));
				}
			}
		}
		int delay = Math.max(1, MathHelper.ceil(DemetersSoul.DELAY.get() / Math.log(10 + DemetersSoul.SCALING.get(MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.DEMETERS_SOUL, player.getHeldItem(EnumHand.MAIN_HAND))))));
		if(player.world.getTotalWorldTime() % delay == 0)
		{
			HarvestEntry entry = DemetersSoul.getNextIndex(player);
			if(entry != null)
			{
				EnumActionResult result = entry.harvest(player.world, player);
				if(result == EnumActionResult.FAIL)
				{
					NBTTagList list = DemetersSoul.getCrops(player);
					for(int i = 0, m = list.tagCount();i < m;i++)
					{
						if(entry.matches(list.getCompoundTagAt(i)))
						{
							list.removeTag(i--);
							break;
						}
					}
				}
				else if(result == EnumActionResult.SUCCESS)
				{
					player.addExhaustion(0.06F);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onFall(LivingFallEvent event)
	{
		ItemStack stack = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.FEET);
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.ESSENCE_OF_SLIME, stack);
		if(level > 0)
		{
			if(event.getDistance() <= 1.3)
				return;
			int damage = MathHelper.floor(((event.getDistance() - 2) * EssenceOfSlime.DURABILITY_LOSS.get() / Math.sqrt(level + MiscUtil.getEnchantmentLevel(Enchantments.FEATHER_FALLING, stack))));
			if(damage > 0)
			{
				stack.damageItem(damage, event.getEntityLiving());
			}
			EntityLivingBase entity = event.getEntityLiving();
			entity.getEntityData().setDouble("bounce", entity.motionY/Math.log10(15.5D+EssenceOfSlime.FALL_DISTANCE_MULTIPLIER.get(entity.motionY)) * -1D);
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onEntityTick(LivingUpdateEvent event)
	{
		if(event.getEntityLiving() instanceof EntityPlayer == event.getEntityLiving().world.isRemote)
		{
			NBTTagCompound data = event.getEntityLiving().getEntityData();
			if(data.hasKey("bounce"))
			{
				event.getEntityLiving().motionY = data.getDouble("bounce");
				data.removeTag("bounce");
			}
		}
	}
	
	@SubscribeEvent
	public void onEaten(LivingEntityUseItemEvent.Finish event)
	{
		if(event.getEntityLiving() instanceof EntityPlayer)
		{
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.AMBROSIA, event.getItem());
			if(level > 0)
			{
				int duration = (int)(Ambrosia.BASE_DURATION.get() + (Math.log(MathCache.POW5.get(1 + ((EntityPlayer)event.getEntityLiving()).experienceLevel * level)) * Ambrosia.DURATION_MULTIPLIER.get()));
				((EntityPlayer)event.getEntityLiving()).getFoodStats().addStats(2000, 0);
				event.getEntityLiving().addPotionEffect(new PotionEffect(UniqueEnchantmentsUtils.SATURATION, duration, Math.min(20, level)));
			}
		}
	}
	
	@SubscribeEvent
	public void onHeal(LivingHealEvent event)
	{
		int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsUtils.PHANES_REGRET, event.getEntityLiving());
		if(level > 0)
		{
			event.setAmount((float)(event.getAmount() * (1 - PhanesRegret.REDUCTION.get(Math.log(2.8D+MathCache.POW3.get(level))))));
			if(event.getEntityLiving() instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)event.getEntityLiving();
				int toConsume = MathHelper.ceil(Math.sqrt(Math.abs(event.getAmount()*level)));
				if(toConsume > 0)
				{
					if(player.experienceTotal >= toConsume) MiscUtil.drainExperience(player, toConsume);
					else player.attackEntityFrom(DamageSource.GENERIC, (float)Math.sqrt(toConsume));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onItemClick(RightClickItem event)
	{
		if(event.getEntityPlayer() == null || !event.getEntityPlayer().isSneaking()) return;
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = event.getItemStack();
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.RESONANCE, stack);
		if(level > 0)
		{
			if(!event.getWorld().isRemote)
			{
				NBTTagCompound nbt = player.getEntityData();
				if(nbt.getLong(Resonance.COOLDOWN_TAG) > event.getWorld().getTotalWorldTime())
				{
					int used = nbt.getInteger(Resonance.OVERUSED_TAG);
					event.getEntityPlayer().attackEntityFrom(DamageSource.MAGIC, 0.5F*used);
					nbt.setInteger(Resonance.OVERUSED_TAG, used+1);
					if(!player.world.isRemote) player.sendMessage(new TextComponentTranslation("tooltip.uniqueeutil.resonance.cooldown", (event.getWorld().getTotalWorldTime() - nbt.getLong(Resonance.COOLDOWN_TAG)) / 20));
				}
				else
				{
					Resonance.addResonance(event.getWorld(), event.getPos(), (int)(Resonance.RANGE.getSqrt(level*Math.sqrt(event.getEntityPlayer().experienceLevel))));
					nbt.setLong(Resonance.COOLDOWN_TAG, event.getWorld().getTotalWorldTime()+600);
					nbt.setInteger(Resonance.OVERUSED_TAG, 1);
				}
			}
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getEntityPlayer().isSneaking())
		{
			IBlockState state = event.getWorld().getBlockState(event.getPos());
			if(state.getBlock().isLadder(state, event.getWorld(), event.getPos(), event.getEntityLiving()))
			{
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.CLIMBER, event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.LEGS));
				if(level > 0)
				{
					MutableBlockPos pos = new MutableBlockPos(event.getPos());
					List<Block> blocks = new ObjectArrayList<>();
					do
					{
						pos.move(EnumFacing.UP);
						state = event.getWorld().getBlockState(pos);
						blocks.add(state.getBlock());
					}
					while(state.getBlock().isLadder(state, event.getWorld(), pos, event.getEntityLiving()));
					if(!event.getWorld().getBlockState(pos).isBlockNormalCube() && !event.getWorld().getBlockState(pos.up()).isBlockNormalCube())
					{
						NBTTagCompound nbt = event.getEntityPlayer().getEntityData();
						boolean wasClimbing = nbt.getLong(Climber.CLIMB_START) != 0;
						nbt.setLong(Climber.CLIMB_POS, pos.toLong());
						nbt.setInteger(Climber.CLIMB_DELAY, Climber.getClimbTime(level, blocks));
						nbt.setLong(Climber.CLIMB_START, event.getWorld().getTotalWorldTime());
						if(!event.getWorld().isRemote) event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("tooltip.uniqueeutil.climb."+(wasClimbing ? "re" : "")+"start.name"), true);
						event.setCancellationResult(EnumActionResult.SUCCESS);
						event.setCanceled(true);
					}
					else
					{
						event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("tooltip.uniqueeutil.climb.fail.name"), true);
					}
				}
			}
			else if(state.getBlock() == Blocks.ANVIL)
			{
				ItemStack stack = event.getItemStack();
				EntityPlayer player = event.getEntityPlayer();
				Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(stack);
				int level = ench.getInt(UniqueEnchantmentsUtils.SAGES_SOUL);
				if(level > 0)
				{
					int stored = StackUtils.getInt(stack, SagesSoul.STORED_XP, 0);
					int required = MiscUtil.getXPForLvl((stored * 2) + 5);
					if(player.experienceTotal >= required)
					{
						MiscUtil.drainExperience(player, required);
						StackUtils.setInt(stack, SagesSoul.STORED_XP, stored+5);
						event.setCancellationResult(EnumActionResult.SUCCESS);
					}
					else
					{
						if(!player.world.isRemote) player.sendMessage(new TextComponentTranslation("tooltip.uniqueeutil.missing.xp"));
						event.setCancellationResult(EnumActionResult.FAIL);
					}
					event.setCanceled(true);
				}
			}
			else if(state.getBlock() == Blocks.BEACON)
			{
				Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(event.getItemStack());
				int level = ench.getInt(UniqueEnchantmentsUtils.ALCHEMISTS_BLESSING);
				if(level > 0 && !event.getWorld().isRemote)
				{
					TileEntity tile = event.getWorld().getTileEntity(event.getPos());
					if(tile instanceof TileEntityBeacon)
					{
						TileEntityBeacon beacon = (TileEntityBeacon)tile;
						if(beacon.getField(1) == -1) return;
						World world = beacon.getWorld();
						BlockPos pos = beacon.getPos().up();
						List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(beacon.getPos().up()));
						if(items.isEmpty()) return;
						int consumed = 0;
						int xpToSpawn = 0;
						for(EntityItem item : items)
						{
							ItemStack stack = item.getItem();
							consumed += stack.getCount();
							item.setDead();
							item.setInfinitePickupDelay();
							ConversionEntry entry = AlchemistsBlessing.RECIPES.get(stack.getItem());
							if(entry == null)
							{
								xpToSpawn += stack.getCount();
								continue;
							}
							ItemStack result = entry.generateOutput(world.rand, level-1, Math.min(world.rand.nextInt(Math.max(ench.getInt(Enchantments.FORTUNE), ench.getInt(Enchantments.LOOTING)) + 1), level));
							if(result.getCount() <= 0) continue;
							List<ItemStack> newDrops = new ObjectArrayList<ItemStack>();
							StackUtils.growStack(result, result.getCount() * stack.getCount(), newDrops);
							for(ItemStack drop : newDrops)
							{
								world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, drop));
							}
						}
						if(xpToSpawn > 0) world.spawnEntity(new EntityXPOrb(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, MathHelper.ceil(Math.sqrt(AlchemistsBlessing.BASE_XP_USAGE.get(level))+Math.sqrt(AlchemistsBlessing.LVL_XP_USAGE.get(level*world.rand.nextDouble()))) * xpToSpawn));
						consumed = MathHelper.ceil(Math.pow((level*consumed*AlchemistsBlessing.CONSUMTION.get()), 0.6505));
						int stored = StackUtils.getInt(event.getItemStack(), AlchemistsBlessing.STORED_REDSTONE, 0) - consumed;
						StackUtils.setInt(event.getItemStack(), AlchemistsBlessing.STORED_REDSTONE, Math.max(0, stored));
						if(stored < 0) event.getItemStack().damageItem(-stored, event.getEntityPlayer());
						event.setCancellationResult(EnumActionResult.SUCCESS);
						event.setCanceled(true);
					}
				}
			}
			else
			{
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.DEMETERS_SOUL, event.getItemStack());
				if(level > 0 && CropHarvestRegistry.INSTANCE.isValid(state.getBlock()) && !event.getWorld().isRemote)
				{
					HarvestEntry entry = new HarvestEntry(event.getWorld().provider.getDimension(), event.getPos().toLong());
					NBTTagList list = DemetersSoul.getCrops(event.getEntityPlayer());
					boolean found = false;
					for(int i = 0, m = list.tagCount();i < m;i++)
					{
						if(entry.matches(list.getCompoundTagAt(i)))
						{
							found = true;
							list.removeTag(i--);
							break;
						}
					}
					if(!found)
					{
						if(list.tagCount() >= DemetersSoul.CAP.get(level))
						{
							event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("tooltip.uniqueeutil.crops.full.name"), false);
							event.setCancellationResult(EnumActionResult.SUCCESS);
							event.setCanceled(true);
							return;
						}
						list.appendTag(entry.save());
					}
					event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("tooltip.uniqueeutil.crops." + (found ? "removed" : "added") + ".name"), false);
					event.setCancellationResult(EnumActionResult.SUCCESS);
					event.setCanceled(true);
					return;
				}
			}
		}
		else
		{
			Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(event.getItemStack());
			if(ench.getInt(UniqueEnchantmentsUtils.DETEMERS_BLESSING) > 0)
			{
				IBlockState state = event.getWorld().getBlockState(event.getPos());
				if(state.getBlock() instanceof BlockCrops)
				{
					BlockCrops crops = (BlockCrops)state.getBlock();
					if(crops.isMaxAge(state))
					{
						state.getBlock().dropBlockAsItem(event.getWorld(), event.getPos(), state, ench.getInt(Enchantments.FORTUNE));
						event.getWorld().setBlockState(event.getPos(), crops.withAge(0));
						event.getItemStack().damageItem(1, event.getEntityLiving());
					}
				}
				else if(state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.GRASS_PATH || state.getBlock() == Blocks.GRASS)
				{
					event.getItemStack().attemptDamageItem(-ench.getInt(UniqueEnchantmentsUtils.DETEMERS_BLESSING), event.getWorld().rand, null);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLeftHorse(EntityMountEvent event)
	{
		if(event.getEntityBeingMounted() instanceof EntityHorse)
		{
			EntityHorse horse = (EntityHorse)event.getEntityBeingMounted();
			horse.getEntityData().removeTag(SleipnirsGrace.HORSE_NBT);
			horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(SleipnirsGrace.SPEED_MOD);
		}
	}
	
	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		if(event.getEntityPlayer() == null)
		{
			return;
		}
		EntityPlayer player = event.getEntityPlayer();
		ItemStack held = player.getHeldItemMainhand();
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.ADEPT, player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
		if(level > 0)
		{
			event.setNewSpeed(event.getNewSpeed() * (player.onGround ? 1F : 5F) * (player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(player) ? 5F : 1F));
			event.setNewSpeed((float)(event.getNewSpeed() * -Math.sqrt(1/(1 + level*level * Adept.SPEED_SCALE.get()))+1));
		}
		Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(held);
		level = ench.getInt(UniqueEnchantmentsUtils.SAGES_SOUL);
		if(level > 0)
		{
			double power = SagesSoul.getEnchantPower(held, level);
			int levels = StackUtils.getInt(held, SagesSoul.STORED_XP, 0);
			event.setNewSpeed((float)(event.getNewSpeed() + Math.min(25.5D, (-0.5+Math.pow(Math.sqrt(0.25+SagesSoul.MINING_SPEED.get(2*levels)), power))) + (Math.max(25.5, Math.pow((-0.5+Math.sqrt(0.25+2*player.experienceLevel)), power))-25.5D)/16));
		}
		level = ench.getInt(UniqueEnchantmentsUtils.THICK_PICK);
		if(level > 0 && event.getState().getBlockHardness(player.world, event.getPos()) >= 20)
		{
			int amount = StackUtils.getInt(held, ThickPick.TAG, 0);
			if(amount > 0)
			{
				event.setNewSpeed(event.getNewSpeed() * ThickPick.MINING_SPEED.getAsFloat(level));
			}
		}
		level = ench.getInt(UniqueEnchantmentsUtils.REINFORCED);
		if(level > 0)
		{
			double base = Reinforced.BASE_REDUCTION.get();
			event.setNewSpeed((float)(event.getNewSpeed() * Math.pow((base+((1D-base)*0.01D)*level), Math.sqrt(level))));
		}
	}
	
	@SubscribeEvent
	public void onItemUseTick(LivingEntityUseItemEvent.Tick event)
	{
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.SAGES_SOUL, event.getItem());
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
		EntityPlayer player = event.getPlayer();
		ItemStack held = player.getHeldItemMainhand();
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.THICK_PICK, held);
		if(level > 0)
		{
			int amount = StackUtils.getInt(held, ThickPick.TAG, 0);
			if(amount > 0)
			{
				StackUtils.setInt(held, ThickPick.TAG, amount - 1);
			}
		}
	}
	
	@SubscribeEvent
	public void onItemAirClick(RightClickItem event)
	{
		ItemStack stack = event.getItemStack();
		if(stack.getItem() == Items.FIREWORKS && event.getEntityPlayer() != null && !event.getEntityPlayer().isElytraFlying())
		{
			ItemStack armor = event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.ROCKET_MAN, armor);
			if(armor.getItem() instanceof ItemElytra && level > 0)
			{
				if(!event.getWorld().isRemote)
				{
					event.getWorld().spawnEntity(new EntityFireworkRocket(event.getWorld(), stack, event.getEntityPlayer()));
				}
				else
				{
					EntityPlayer player = event.getEntityPlayer();
					event.getEntityPlayer().setPositionAndUpdate(player.posX, player.posY + 0.5D, player.posZ);
				}
				try
				{
					ReflectionHelper.findMethod(Entity.class, "setFlag", "func_70052_a", int.class, boolean.class).invoke(event.getEntityPlayer(), 7, true);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		if(event.getEntity() instanceof EntityFireworkRocket)
		{
			EntityFireworkRocket rocket = (EntityFireworkRocket)event.getEntity();
			try
			{
				EntityLivingBase entity = ObfuscationReflectionHelper.getPrivateValue(EntityFireworkRocket.class, rocket, "field_191513_e");
				if(entity != null)
				{
					ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
					if(stack.getItem() instanceof ItemElytra)
					{
						int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.ROCKET_MAN, stack);
						if(level > 0)
						{
							NBTTagCompound nbt = new NBTTagCompound();
							rocket.writeEntityToNBT(nbt);
							int time = nbt.getInteger("LifeTime");
							time += time * RocketMan.FLIGHT_TIME.getAsDouble(level) * MathCache.LOG_ADD_MAX.get(level);
							nbt.setInteger("LifeTime", time);
							rocket.readEntityFromNBT(nbt);
						}
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityAttack(LivingAttackEvent event)
	{
		if(event.getAmount() > 0F)
		{
			for(EntityPlayer player : event.getEntityLiving().getRecursivePassengersByType(EntityPlayer.class))
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
	public void onEntityDamage(LivingDamageEvent event)
	{
		if(event.getSource() == null || event.getSource().isMagicDamage()) return;
		int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsUtils.PHANES_REGRET, event.getEntityLiving());
		if(level > 0)
		{
			event.setAmount((float)(event.getAmount() * (1 - PhanesRegret.REDUCTION.get(Math.log(2.8D+MathCache.POW3.get(level))))));
			if(event.getEntityLiving() instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)event.getEntityLiving();
				int toConsume = MathHelper.ceil(Math.sqrt(Math.abs(event.getAmount()*level)));
				if(toConsume > 0)
				{
					if(player.experienceTotal >= toConsume) MiscUtil.drainExperience(player, toConsume);
					else event.setAmount(event.getAmount() * (float)(Math.log10(100+event.getAmount()*level)-1));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEquippementSwapped(LivingEquipmentChangeEvent event)
	{
		AbstractAttributeMap attribute = event.getEntityLiving().getAttributeMap();
		Multimap<String, AttributeModifier> mods = createModifiersFromStack(event.getFrom(), event.getEntityLiving(), event.getSlot());
		if(!mods.isEmpty())
		{
			attribute.removeAttributeModifiers(mods);
		}
		mods = createModifiersFromStack(event.getTo(),event.getEntityLiving(), event.getSlot());
		if(!mods.isEmpty())
		{
			attribute.applyAttributeModifiers(mods);
		}
	}
	
	private Multimap<String, AttributeModifier> createModifiersFromStack(ItemStack stack, EntityLivingBase living, EntityEquipmentSlot slot)
	{
		Multimap<String, AttributeModifier> mods = HashMultimap.create();
		Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(stack);
		int level = ench.getInt(UniqueEnchantmentsUtils.REINFORCED);
		if(level > 0)
		{
			double base = Reinforced.BASE_REDUCTION.get();
			mods.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(Reinforced.SPEED_MOD, "Reinforced Boost", Math.pow((base+((1D-base)*0.01D)*level), Math.sqrt(level)), 2));
		}
		level = ench.getInt(UniqueEnchantmentsUtils.SAGES_SOUL);
		if(level > 0)
		{
			int levels = StackUtils.getInt(stack, SagesSoul.STORED_XP, 0);
			double power = SagesSoul.getEnchantPower(stack, level);
			mods.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SagesSoul.ATTACK_MOD, "Sage Attack Boost", Math.pow(-0.5+Math.sqrt(0.25+SagesSoul.ATTACK_SPEED.get(2*levels)), power)/SagesSoul.ATTACK_SPEED.get(), 2));
			mods.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(SagesSoul.ARMOR_MOD, "Sages Armor Boost", Math.pow((-0.5+SagesSoul.ARMOR_SCALE.get(Math.sqrt(0.25+8*(levels*Math.sqrt(level))))), power)/SagesSoul.ARMOR_DIVIDOR.get(), 0));
			mods.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(SagesSoul.TOUGHNESS_MOD, "Sages Toughness Boost", Math.pow((-0.5+SagesSoul.TOUGHNESS_SCALE.get(Math.sqrt(0.25+1*(levels*Math.sqrt(level))))), power)/SagesSoul.TOUGHNESS_DIVIDOR.get() , 0));
		}
		return mods;
	}
	
	public static void damageShield(float damage, EntityPlayer player)
	{
		if(damage >= 3.0F && player.getActiveItemStack().getItem().isShield(player.getActiveItemStack(), player))
		{
			ItemStack copyBeforeUse = player.getActiveItemStack().copy();
			int i = 1 + MathHelper.floor(damage);
			player.getActiveItemStack().damageItem(i, player);
			if(player.getActiveItemStack().isEmpty())
			{
				EnumHand enumhand = player.getActiveHand();
				net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, enumhand);
				
				if(enumhand == EnumHand.MAIN_HAND)
				{
					player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
				}
				else
				{
					player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
				}
				
				player.resetActiveHand();
				player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + player.world.rand.nextFloat() * 0.4F);
			}
		}
	}
	
	public static boolean canBlockDamageSource(DamageSource damageSourceIn, EntityPlayer player)
	{
		if(!damageSourceIn.isUnblockable() && player.isActiveItemStackBlocking())
		{
			if(MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.MOUNTING_AEGIS, player.getActiveItemStack()) <= 0)
				return false;
			Vec3d vec3d = damageSourceIn.getDamageLocation();
			
			if(vec3d != null)
			{
				Vec3d vec3d1 = player.getLook(1.0F);
				Vec3d vec3d2 = vec3d.subtractReverse(new Vec3d(player.posX, player.posY, player.posZ)).normalize();
				vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);
				if(vec3d2.dotProduct(vec3d1) < 0.0D)
				{
					return true;
				}
			}
		}
		return false;
	}
}
