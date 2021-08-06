package uniquebase.handler;

import java.util.List;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.Tuple;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
		tooltips.add(new Tuple<>(ench, new String[]{translation, tag}));
	}
	
	public void registerAnvilHelper(Enchantment ench, ToIntFunction<ItemStack> helper, String tag)
	{
		anvilHelpers.add(Triple.create(ench, helper, tag));
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
				event.getToolTip().add(I18n.format(names[0], StackUtils.getInt(stack, names[1], 0)));
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
	
	public static int getXP(EntityPlayer player)
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
