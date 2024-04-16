package uniquebase.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniquebase.BaseConfig;
import uniquebase.UEBase;
import uniquebase.api.ColorConfig;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.gui.EnchantmentGui;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquebase.utils.Triple;
import uniquebase.utils.mixin.common.entity.ArrowMixin;

public class BaseHandler
{
	public static final BaseHandler INSTANCE = new BaseHandler();
	List<Tuple<Enchantment, String[]>> tooltips = new ObjectArrayList<>();
	List<Triple<Enchantment, ToIntFunction<ItemStack>, String>> anvilHelpers = new ObjectArrayList<>();
	List<TrackedAbsorber> activeAbsorbers = new ArrayList<>(); //Using non FastUtil list due to FastUtils removeIf implementation being slower then Javas ArrayList
	int tooltipCounter = 0;
	int globalAbsorber = 0;
	Random rand = new Random();
	
	public void registerStorageTooltip(Enchantment ench, String translation, String tag)
	{
		tooltips.add(new Tuple<Enchantment, String[]>(ench, new String[]{translation, tag}));
	}
	
	public void registerAnvilHelper(Enchantment ench, ToIntFunction<ItemStack> helper, String tag)
	{
		anvilHelpers.add(Triple.create(ench, helper, tag));
	}
	
	@SubscribeEvent
	public void onBucketAction(FillBucketEvent event)
	{
		ItemStack stack = event.getEmptyBucket();
		if(stack.getItem() != Items.BUCKET || MiscUtil.getEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) <= 0) return;
		HitResult ray = event.getTarget();
		if(ray.getType() == Type.MISS || ray.getType() != Type.BLOCK) return;
		Level world = event.getLevel();
		Player player = event.getEntity();
		BlockHitResult raytrace = (BlockHitResult)ray;
		BlockPos blockpos = raytrace.getBlockPos();
		Direction direction = raytrace.getDirection();
		BlockPos blockpos1 = blockpos.relative(direction);
		if (world.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction, stack))
		{
			BlockState state = world.getBlockState(blockpos);
			if(state.getBlock() instanceof BucketPickup bucket)
			{
				bucket.pickupBlock(world, blockpos, state);
				ItemStack copy = stack.copy();
				copy.setCount(1);
				event.setFilledBucket(copy);
				event.setResult(Result.ALLOW);
			}
		}
	}
	
	@SubscribeEvent
	public void onProjectileImpact(ProjectileImpactEvent event) {
		if(event.getEntity() instanceof AbstractArrow) {
			Entity e = event.getProjectile().getOwner();
			if(e instanceof LivingEntity ent) {
				ItemStack stack = event.getProjectile() instanceof ThrownTrident trident ? ((ArrowMixin)trident).getArrowItem() : ent.getMainHandItem();
				if(stack.getItem() instanceof BowItem) return;
				
				Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
				AbstractArrow pent = (AbstractArrow)event.getEntity();
				Vec3 temp = event.getRayTraceResult().getLocation();
				int level = enchantments.getInt(Enchantments.POWER_ARROWS);
				if(level > 0) {
					pent.setBaseDamage(pent.getBaseDamage() + 0.5 * level + 0.5);
				}
				int flame = enchantments.getInt(Enchantments.FLAMING_ARROWS);
				int punch = enchantments.getInt(Enchantments.PUNCH_ARROWS);
				List<LivingEntity> list = flame > 0 || punch > 0 ? event.getEntity().level.getEntitiesOfClass(LivingEntity.class, new AABB(new BlockPos(temp.x, temp.y, temp.z))) : Collections.emptyList();
				if(flame > 0) {
					int time = 5*flame;
					for(LivingEntity entry : list) {
						entry.setSecondsOnFire(time);
			        }
				}
				if(punch > 0) {
					Vec3 vec = pent.position().subtract(event.getRayTraceResult().getLocation());
					for(LivingEntity entry : list) {
						entry.knockback(punch, vec.x(), vec.z());
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onNock(ArrowNockEvent e) {
		ItemStack stack = e.getBow();
		if(MiscUtil.getEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0) {
			e.getEntity().startUsingItem(e.getHand());
			e.setAction(InteractionResultHolder.consume(stack));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityDamaged(LivingDamageEvent event) {
		LivingEntity target = event.getEntity();
		Entity ent = event.getSource().getEntity();
		
		if(target.isInWaterRainOrBubble() && ent instanceof LivingEntity ) 
		impalingLabel: {
			LivingEntity source = (LivingEntity)ent;
			int level = MiscUtil.getEnchantmentLevel(Enchantments.IMPALING, source.getMainHandItem().isEmpty() ? source.getOffhandItem() : source.getMainHandItem());
			if(level > 0) {
				if(target.fireImmune()) {
					float dmg = (event.getAmount() + 1.5f*level)/2;
					MiscUtil.doNewDamageInstance(target, DamageSource.explosion(source).bypassArmor(), dmg);
					event.setAmount(dmg);
					break impalingLabel;
				}
				event.setAmount(event.getAmount()+1.5f*level);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityHurt(ProjectileImpactEvent event) {
		if(event.getEntity() instanceof LivingEntity entity) {
			int enchantmentLevel = MiscUtil.getCombinedEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, entity);
			if(enchantmentLevel > 0 && rand.nextDouble() < (1-Math.pow(0.9, enchantmentLevel))) {
				event.setCanceled(true);
				event.getProjectile().getDeltaMovement().scale(-1d);
			}
		}
	}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent event)
	{
		if(event.phase == Phase.START) return;
		if(activeAbsorbers.isEmpty()) return;
		if(globalAbsorber++ >= 100)
		{
			globalAbsorber = 0;
			activeAbsorbers.removeIf(this::process);
		}
	}
	
	private boolean process(TrackedAbsorber absorber)
	{
		if(absorber == null) return true;
		return absorber.process();
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onClientTick(ClientTickEvent event)
	{
		if(event.phase == Phase.START) return;
		Minecraft mc = Minecraft.getInstance();
		if(mc.level == null || mc.player == null) return;
		UEBase.PROXY.update();
		if(UEBase.ENCHANTMENT_GUI.test(mc.player) && mc.screen instanceof AbstractContainerScreen && !(mc.screen instanceof EnchantmentGui))
		{
			Slot slot = ((AbstractContainerScreen<?>)mc.screen).getSlotUnderMouse();
			if(slot != null && slot.hasItem() && EnchantmentHelper.getEnchantments(slot.getItem()).size() > 0)
			{
				tooltipCounter++;
				if(tooltipCounter >= BaseConfig.TOOLTIPS.viewCooldowns.get()) {
					mc.setScreen(new EnchantmentGui(slot.getItem().copy(), mc.player, mc.screen));
				}
			}
			else
			{
				tooltipCounter = 0;
			}
		}
		else
		{
			tooltipCounter = 0;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onToolTipRenderEvent(RenderTooltipEvent.Color event) {
		ItemStack stack = event.getItemStack();
		if(stack.isEmpty()) return;

		if(stack.isEnchanted() || stack.getItem() == Items.ENCHANTED_BOOK) {
			Object2IntMap.Entry<Enchantment> ench = MiscUtil.getFirstEnchantment(stack);
			if(ench == null) return;
			ColorConfig config = BaseConfig.BOOKS.getEnchantmentColor(ench.getKey());
			if(config.isDefault()) return;
			if(config.getBorderStartColor() != -1) event.setBorderStart(config.getBorderStartColor());
			if(config.getBorderEndColor() != -1) event.setBorderEnd(config.getBorderEndColor());
			if(config.getBackgroundColor() != -1) event.setBackground(config.getBackgroundColor());
		}
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onToolTipEvent(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();
		int flags = BaseConfig.TOOLTIPS.flags.get();
		//JEI Detection
		if(stack.hasTag() && (stack.getTag().getInt("HideFlags") & ItemStack.TooltipPart.ENCHANTMENTS.getMask()) != 0)
		{
			return;
		}
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		boolean hasEnchantments = (stack.getItem() == Items.ENCHANTED_BOOK || BaseConfig.TOOLTIPS.showOnTools.get()) && EnchantmentHelper.getEnchantments(stack).size() > 0;
		if((flags & 1) != 0 && hasEnchantments && BaseConfig.TOOLTIPS.showDescription.get() && !Screen.hasShiftDown())
		{
			event.getToolTip().add(Component.translatable("unique.base.desc").withStyle(ChatFormatting.DARK_GRAY));
		}
		if((flags & 2) != 0 && hasEnchantments && BaseConfig.ICONS.isEnabled.get() && !UEBase.ENCHANTMENT_ICONS.test(event.getEntity())) {
			event.getToolTip().add(Component.translatable("unique.base.icon", UEBase.ENCHANTMENT_ICONS.getKeyName().copy().withStyle(ChatFormatting.LIGHT_PURPLE)).withStyle(ChatFormatting.DARK_GRAY));
		}
		for(int i = 0,m=tooltips.size();i<m;i++)
		{
			Tuple<Enchantment, String[]> entry = tooltips.get(i);
			if(enchantments.getInt(entry.getA()) > 0)
			{
				String[] names = entry.getB();
				event.getToolTip().add(Component.translatable(names[0], StackUtils.getInt(stack, names[1], 0)).withStyle(ChatFormatting.GOLD));
			}
		}
		for(EnchantedUpgrade upgrade : EnchantedUpgrade.getAllUpgrades())
		{
			upgrade.addToolTip(event.getToolTip(), stack);
		}
		Screen screen = Minecraft.getInstance().screen;
		if(screen instanceof AbstractContainerScreen && !(screen instanceof EnchantmentGui))
		{
			Slot slot = ((AbstractContainerScreen<?>)screen).getSlotUnderMouse();
			if(slot != null && slot.hasItem() && EnchantmentHelper.getEnchantments(stack).size() > 0)
			{
				if(tooltipCounter > 0)
				{
					StringBuilder builder = new StringBuilder();
					int i = 0;
					int max = (int)((tooltipCounter / (double)BaseConfig.TOOLTIPS.viewCooldowns.get()) * 40);
					for(;i<max;i++)
					{
						builder.append("|");
					}
					builder.append(ChatFormatting.DARK_GRAY);
					for(;i<40;i++)
					{
						builder.append("|");
					}
					event.getToolTip().add(Component.literal(builder.toString()));
				}
				else if((flags & 4) != 0)
				{
					event.getToolTip().add(Component.translatable("unique.base.jei.press_gui", UEBase.ENCHANTMENT_GUI.getKeyName().copy().withStyle(ChatFormatting.LIGHT_PURPLE)).withStyle(ChatFormatting.GRAY));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinLevelEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof ItemEntity && ((ItemEntity) entity).getItem().isEnchanted())
		{
			ItemEntity ent = (ItemEntity)entity;

			Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(ent.getItem());
			if(ench.getInt(Enchantments.ALL_DAMAGE_PROTECTION) > 0)
			{
				ent.lifespan *= Math.pow(MiscUtil.getEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION, ((ItemEntity)entity).getItem())+1, 2);
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getEntity().isShiftKeyDown())
		{
			BlockState state = event.getLevel().getBlockState(event.getPos());
			if(state.getBlock() instanceof AnvilBlock)
			{
				ItemStack stack = event.getItemStack();
				Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
				for(int i = 0,m=anvilHelpers.size();i<m;i++)
				{
					Triple<Enchantment, ToIntFunction<ItemStack>, String> entry = anvilHelpers.get(i);
					if(enchantments.getInt(entry.getKey()) > 0)
					{
						int found = StackUtils.consumeItems(event.getEntity(), entry.getValue(), Integer.MAX_VALUE);
						if(found > 0)
						{
							StackUtils.setInt(stack, entry.getExtra(), found + StackUtils.getInt(stack, entry.getExtra(), 0));
							event.setCancellationResult(InteractionResult.SUCCESS);
							event.setCanceled(true);
						}
					}
				}
			}
			if(state.getEnchantPowerBonus(event.getLevel(), event.getPos()) > 0 && event.getItemStack().isEnchanted()) {
				MiscUtil.toggleTrancendence(event.getItemStack());
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityClick(EntityInteract event)
	{
		if(BaseConfig.TWEAKS.attribute.contains(event.getItemStack().getItem()) && event.getTarget() instanceof ItemFrame)
		{
			ItemFrame frame = (ItemFrame)event.getTarget();
			ItemStack stack = frame.getItem();
			if(stack.isEmpty()) return;
			Level world = event.getLevel();
			BlockPos pos = frame.getPos().relative(frame.getDirection().getOpposite());
			if(world.getBlockState(pos).is(Blocks.CONDUIT))
			{
				List<EnchantedUpgrade> upgrades = new ObjectArrayList<>();
				for(EnchantedUpgrade entry : EnchantedUpgrade.getAllUpgrades())
				{
					if(entry.isValid(stack)) upgrades.add(entry);
				}
				if(upgrades.isEmpty()) return;
				activeAbsorbers.add(new TrackedAbsorber(frame, world, pos, upgrades));
				event.getItemStack().shrink(1);
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}
}
