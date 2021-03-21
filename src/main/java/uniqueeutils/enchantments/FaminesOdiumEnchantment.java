package uniqueeutils.enchantments;

import it.unimi.dsi.fastutil.ints.AbstractInt2FloatMap.BasicEntry;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;
import uniquee.utils.IntStat;

public class FaminesOdiumEnchantment extends UniqueEnchantment
{
	public static final IntStat DELAY = new IntStat(1200, "delay");
	public static final DoubleStat NURISHMENT = new DoubleStat(0.2F, "nurishment");
	public static final DoubleStat DAMAGE = new DoubleStat(0.00625F, "nurishment");
	
	public FaminesOdiumEnchantment()
	{
		super(new DefaultData("famines_odium", Rarity.UNCOMMON, 1, false, 10, 4, 40), UniqueEnchantments.ALL_TYPES, EquipmentSlotType.values());
		setCategory("utils");
	}
	
	@Override
	public ITextComponent getDisplayName(int level)
	{
		return ((IFormattableTextComponent)super.getDisplayName(level)).mergeStyle(TextFormatting.RED);
	}
	
	@Override
	public boolean isCurse()
	{
		return true;
	}
	
	@Override
	public void loadData(ForgeConfigSpec.Builder config)
	{
		DELAY.handleConfig(config);
		NURISHMENT.handleConfig(config);
		DAMAGE.handleConfig(config);
		System.out.println("Testing");
	}
	
	public static Int2FloatMap.Entry consumeRandomItem(IInventory inventory, float effect)
	{
		for(int i = 0,m=inventory.getSizeInventory();i<m;i++)
		{
			ItemStack stack = inventory.getStackInSlot(i);
			if(stack.getItem().isFood())
			{
				Food food = stack.getItem().getFood();
				BasicEntry entry = new BasicEntry(food.getHealing() / 4, food.getSaturation() * effect);
				if(stack.getItem().hasContainerItem(stack))
				{
					ItemStack container = stack.getItem().getContainerItem(stack);
					stack.shrink(1);
					inventory.setInventorySlotContents(i, container);
				}
				else
				{
					stack.shrink(1);
					inventory.setInventorySlotContents(i, stack);
				}
				return entry;
			}
		}
		return null;
	}
	
}
