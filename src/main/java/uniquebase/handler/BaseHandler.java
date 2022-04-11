package uniquebase.handler;

import java.util.List;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniquebase.UEBase;
import uniquebase.api.ColorConfig;
import uniquebase.gui.EnchantmentGui;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquebase.utils.Triple;

public class BaseHandler
{
	public static final BaseHandler INSTANCE = new BaseHandler();
	List<Tuple<Enchantment, String[]>> tooltips = new ObjectArrayList<>();
	List<Triple<Enchantment, ToIntFunction<ItemStack>, String>> anvilHelpers = new ObjectArrayList<>();
	int tooltipCounter = 0;
	
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
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onToolTipRenderEvent(RenderTooltipEvent.Color event) {
		ItemStack stack = event.getStack();
		if(stack.isEmpty()) return;

		if(stack.isEnchanted() || stack.getItem() == Items.ENCHANTED_BOOK) {
			ColorConfig config = UEBase.COLOR_SETTINGS.get(MiscUtil.getFirstEnchantment(stack).getKey());
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
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		boolean hasEnchantments = (stack.getItem() == Items.ENCHANTED_BOOK || UEBase.SHOW_NON_BOOKS.get()) && EnchantmentHelper.getEnchantments(stack).size() > 0;
		if(hasEnchantments && UEBase.SHOW_DESCRIPTION.get() && !Screen.hasShiftDown())
		{
			event.getToolTip().add(new TranslationTextComponent("unique.base.desc").withStyle(TextFormatting.DARK_GRAY));
		}
		if(hasEnchantments && !UEBase.ENCHANTMENT_ICONS.test(event.getPlayer())) {
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
				else
				{
					event.getToolTip().add(new TranslationTextComponent("unique.base.jei.press_gui", UEBase.ENCHANTMENT_GUI.getKeyName().copy().withStyle(TextFormatting.LIGHT_PURPLE)).withStyle(TextFormatting.GRAY));
				}
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
}
