package uniquebase.handler;

import java.util.List;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquebase.utils.Triple;

public class BaseHandler
{
	public static final BaseHandler INSTANCE = new BaseHandler();
	List<Tuple<Enchantment, String[]>> tooltips = new ObjectArrayList<>();
	List<Triple<Enchantment, ToIntFunction<ItemStack>, String>> anvilHelpers = new ObjectArrayList<>();
		
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
				event.getToolTip().add(new TranslationTextComponent(names[0], StackUtils.getInt(stack, names[1], 0)).applyTextStyle(TextFormatting.GOLD));
			}
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
