package uniquebase.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniquebase.UEBase;
import uniquebase.api.ColorConfig;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.gui.EnchantmentGui;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquebase.utils.Triple;

public class BaseHandler
{
	public static final BaseHandler INSTANCE = new BaseHandler();
	List<Tuple<Enchantment, String[]>> tooltips = new ObjectArrayList<>();
	List<Triple<Enchantment, ToIntFunction<ItemStack>, String>> anvilHelpers = new ObjectArrayList<>();
	List<TrackedAbsorber> activeAbsorbers = new ArrayList<>(); //Using non FastUtil list due to FastUtils removeIf implementation being slower then Javas ArrayList
	int tooltipCounter = 0;
	int globalAbsorber = 0;
	
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
		RayTraceResult ray = event.getTarget();
		if(ray.getType() == Type.MISS || ray.getType() != Type.BLOCK) return;
		World world = event.getWorld();
		PlayerEntity player = event.getPlayer();
		BlockRayTraceResult raytrace = (BlockRayTraceResult)ray;
		BlockPos blockpos = raytrace.getBlockPos();
		Direction direction = raytrace.getDirection();
		BlockPos blockpos1 = blockpos.relative(direction);
		if (world.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction, stack))
		{
			BlockState state = world.getBlockState(blockpos);
			if(state.getBlock() instanceof IBucketPickupHandler)
			{
				((IBucketPickupHandler)state.getBlock()).takeLiquid(world, blockpos, state);
				ItemStack copy = stack.copy();
				copy.setCount(1);
				event.setFilledBucket(copy);
				event.setResult(Result.ALLOW);
			}
		}
	}
	
	@SubscribeEvent
	public void onProjectileImpact(ProjectileImpactEvent event) {
		if(event.getEntity() instanceof AbstractArrowEntity) {
			ItemStack stack = StackUtils.getArrowStack((AbstractArrowEntity)event.getEntity());
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
			AbstractArrowEntity ent = (AbstractArrowEntity)event.getEntity();
			Vector3d temp = event.getRayTraceResult().getLocation();
			int level = enchantments.getInt(Enchantments.POWER_ARROWS);
			if(level > 0) {
				ent.setBaseDamage(ent.getBaseDamage() + 0.5 * level + 0.5);
			}
			int flame = enchantments.getInt(Enchantments.FLAMING_ARROWS);
			int punch = enchantments.getInt(Enchantments.PUNCH_ARROWS);
			List<LivingEntity> list = flame > 0 || punch > 0 ? event.getEntity().level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(new BlockPos(temp.x, temp.y, temp.z))) : Collections.emptyList();
			if(flame > 0) {
				int time = 100*flame;
				for(LivingEntity entry : list) {
					entry.setSecondsOnFire(time);
		        }
			}
			if(punch > 0) {
				Vector3d vec = ent.position().subtract(event.getRayTraceResult().getLocation());
				for(LivingEntity entry : list) {
					entry.knockback(punch, vec.x(), vec.z());
				}
			}
		}
		
	}
	
	@SubscribeEvent
	public void onEntityDamaged(LivingDamageEvent event) {
		LivingEntity target = event.getEntityLiving();
		Entity ent = event.getSource().getEntity();
		
		if(target.isInWaterRainOrBubble() && ent instanceof LivingEntity ) {
			LivingEntity source = (LivingEntity)ent;
			int level = MiscUtil.getEnchantmentLevel(Enchantments.IMPALING, source.getMainHandItem().isEmpty() ? source.getOffhandItem() : source.getMainHandItem());
			if(level > 0) {
				event.setAmount(event.getAmount()+2.5f*level);
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
		if(UEBase.ENCHANTMENT_GUI.test(mc.player) && mc.screen instanceof ContainerScreen && !(mc.screen instanceof EnchantmentGui))
		{
			Slot slot = ((ContainerScreen<?>)mc.screen).getSlotUnderMouse();
			if(slot != null && slot.hasItem() && EnchantmentHelper.getEnchantments(slot.getItem()).size() > 0)
			{
				tooltipCounter++;
				if(tooltipCounter >= UEBase.VIEW_COOLDOWN.get()) {
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
		ItemStack stack = event.getStack();
		if(stack.isEmpty()) return;

		if(stack.isEnchanted() || stack.getItem() == Items.ENCHANTED_BOOK) {
			Object2IntMap.Entry<Enchantment> ench = MiscUtil.getFirstEnchantment(stack);
			if(ench == null) return;
			ColorConfig config = UEBase.getEnchantmentColor(ench.getKey());
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
		int flags = UEBase.TOOLTIPS_FLAGS.get();
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		boolean hasEnchantments = (stack.getItem() == Items.ENCHANTED_BOOK || UEBase.SHOW_NON_BOOKS.get()) && EnchantmentHelper.getEnchantments(stack).size() > 0;
		if((flags & 1) != 0 && hasEnchantments && UEBase.SHOW_DESCRIPTION.get() && !Screen.hasShiftDown())
		{
			event.getToolTip().add(new TranslationTextComponent("unique.base.desc").withStyle(TextFormatting.DARK_GRAY));
		}
		if((flags & 2) != 0 && hasEnchantments && UEBase.ICONS.get() && !UEBase.ENCHANTMENT_ICONS.test(event.getPlayer())) {
			event.getToolTip().add(new TranslationTextComponent("unique.base.icon", UEBase.ENCHANTMENT_ICONS.getKeyName().copy().withStyle(TextFormatting.LIGHT_PURPLE)).withStyle(TextFormatting.DARK_GRAY));
		}
		for(int i = 0,m=tooltips.size();i<m;i++)
		{
			Tuple<Enchantment, String[]> entry = tooltips.get(i);
			if(enchantments.getInt(entry.getA()) > 0)
			{
				String[] names = entry.getB();
				event.getToolTip().add(new TranslationTextComponent(names[0], StackUtils.getInt(stack, names[1], 0)).withStyle(TextFormatting.GOLD));
			}
		}
		for(EnchantedUpgrade upgrade : EnchantedUpgrade.getAllUpgrades())
		{
			int points = upgrade.getPoints(stack);
			if(points > 0)
			{
				event.getToolTip().add(new TranslationTextComponent(upgrade.getName(), points).withStyle(TextFormatting.GOLD));
			}
		}
		Screen screen = Minecraft.getInstance().screen;
		if(screen instanceof ContainerScreen && !(screen instanceof EnchantmentGui))
		{
			Slot slot = ((ContainerScreen<?>)screen).getSlotUnderMouse();
			if(slot != null && slot.hasItem() && EnchantmentHelper.getEnchantments(stack).size() > 0)
			{
				if(tooltipCounter > 0)
				{
					StringBuilder builder = new StringBuilder();
					int i = 0;
					int max = (int)((tooltipCounter / (double)UEBase.VIEW_COOLDOWN.get()) * 40);
					for(;i<max;i++)
					{
						builder.append("|");
					}
					builder.append(TextFormatting.DARK_GRAY);
					for(;i<40;i++)
					{
						builder.append("|");
					}
					event.getToolTip().add(new StringTextComponent(builder.toString()));
				}
				else if((flags & 4) != 0)
				{
					event.getToolTip().add(new TranslationTextComponent("unique.base.jei.press_gui", UEBase.ENCHANTMENT_GUI.getKeyName().copy().withStyle(TextFormatting.LIGHT_PURPLE)).withStyle(TextFormatting.GRAY));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event) {
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
		if(event.getPlayer().isShiftKeyDown())
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
						int found = StackUtils.consumeItems(event.getPlayer(), entry.getValue(), Integer.MAX_VALUE);
						if(found > 0)
						{
							StackUtils.setInt(stack, entry.getExtra(), found + StackUtils.getInt(stack, entry.getExtra(), 0));
							event.setCancellationResult(ActionResultType.SUCCESS);
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityClick(EntityInteract event)
	{
		if(event.getItemStack().getItem() == Items.BELL && event.getTarget() instanceof ItemFrameEntity)
		{
			ItemFrameEntity frame = (ItemFrameEntity)event.getTarget();
			ItemStack stack = frame.getItem();
			if(stack.isEmpty()) return;
			World world = event.getWorld();
			BlockPos pos = frame.getPos().relative(frame.getDirection().getOpposite());
			if(world.getBlockState(pos).getBlock() == Blocks.ENCHANTING_TABLE)
			{
				List<EnchantedUpgrade> upgrades = new ObjectArrayList<>();
				for(EnchantedUpgrade entry : EnchantedUpgrade.getAllUpgrades())
				{
					if(entry.isValid(stack)) upgrades.add(entry);
				}
				if(upgrades.isEmpty()) return;
				activeAbsorbers.add(new TrackedAbsorber(frame, world, pos, upgrades));
				event.getItemStack().shrink(1);
				event.setCancellationResult(ActionResultType.SUCCESS);
				event.setCanceled(true);
			}
		}
	}
}
