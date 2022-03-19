package uniquebase.handler;

import java.util.List;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uniquebase.UniqueEnchantmentsBase;
import uniquebase.gui.EnchantmentGui;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquebase.utils.Triple;

public class BaseHandler
{
	public static final BaseHandler INSTANCE = new BaseHandler();
	List<Tuple<Enchantment, String[]>> tooltips = new ObjectArrayList<>();
	List<Triple<Enchantment, ToIntFunction<ItemStack>, String>> anvilHelpers = new ObjectArrayList<>();
	int tooltipCounter;
	
	public void registerStorageTooltip(Enchantment ench, String translation, String tag)
	{
		tooltips.add(new Tuple<>(ench, new String[]{translation, tag}));
	}
	
	public void registerAnvilHelper(Enchantment ench, ToIntFunction<ItemStack> helper, String tag)
	{
		anvilHelpers.add(Triple.create(ench, helper, tag));
	}
	
	@SubscribeEvent
	public void onEntityInteraction(EntityInteract interact)
	{
		if(interact.getItemStack().getItem() == Items.STICK)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			interact.getTarget().writeToNBT(nbt);
			interact.getEntityPlayer().sendStatusMessage(new TextComponentString(nbt.toString()), false);
			interact.setCancellationResult(EnumActionResult.SUCCESS);
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientTick(ClientTickEvent event)
	{
		if(event.phase == Phase.START) return;
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.world == null || mc.player == null) return;
		UniqueEnchantmentsBase.PROXY.update();
		if(UniqueEnchantmentsBase.ENCHANTMENT_GUI.test(mc.player) && mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof EnchantmentGui))
		{
			Slot slot = ((GuiContainer)mc.currentScreen).getSlotUnderMouse();
			if(slot != null && slot.getHasStack() && EnchantmentHelper.getEnchantments(slot.getStack()).size() > 0)
			{
				tooltipCounter++;
				if(tooltipCounter >= 40) {
					mc.displayGuiScreen(new EnchantmentGui(slot.getStack().copy()));
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
	@SideOnly(Side.CLIENT)
	public void onToolTipEvent(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		for(int i = 0,m=tooltips.size();i<m;i++)
		{
			Tuple<Enchantment, String[]> entry = tooltips.get(i);
			if(enchantments.getInt(entry.getFirst()) > 0)
			{
				String[] names = entry.getSecond();
				event.getToolTip().add(TextFormatting.GOLD+I18n.format(names[0], StackUtils.getInt(stack, names[1], 0)));
			}
		}
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if(screen instanceof GuiContainer && !(screen instanceof EnchantmentGui))
		{
			Slot slot = ((GuiContainer)screen).getSlotUnderMouse();
			if(slot != null && slot.getHasStack() && EnchantmentHelper.getEnchantments(stack).size() > 0)
			{
				if(tooltipCounter > 0)
				{
					StringBuilder builder = new StringBuilder();
					int i = 0;
					for(;i<tooltipCounter;i++)
					{
						builder.append("|");
					}
					builder.append(TextFormatting.DARK_GRAY);
					for(;i<40;i++)
					{
						builder.append("|");						
					}
					event.getToolTip().add(1, builder.toString());
				}
				else
				{
					event.getToolTip().add(1, I18n.format("unique.base.jei.press_gui", UniqueEnchantmentsBase.ENCHANTMENT_GUI.getKeyName()));					
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getEntityPlayer().isSneaking())
		{
			IBlockState state = event.getWorld().getBlockState(event.getPos());
			if(state.getBlock() instanceof BlockAnvil)
			{
				ItemStack stack = event.getItemStack();
				Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
				for(int i = 0,m=anvilHelpers.size();i<m;i++)
				{
					Triple<Enchantment, ToIntFunction<ItemStack>, String> entry = anvilHelpers.get(i);
					if(enchantments.getInt(entry.getKey()) > 0)
					{
						int found = StackUtils.consumeItems(event.getEntityPlayer(), entry.getValue(), Integer.MAX_VALUE);
						if(found > 0)
						{
							StackUtils.setInt(stack, entry.getExtra(), found + StackUtils.getInt(stack, entry.getExtra(), 0));
							event.setCancellationResult(EnumActionResult.SUCCESS);
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}
}
