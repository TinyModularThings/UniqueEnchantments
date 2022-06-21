package uniquebase.jei;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.UEBase;
import uniquebase.utils.MiscUtil;

@JeiPlugin
public class JEIView implements IModPlugin
{
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		if(UEBase.DISABLE_JEI.get()) return;
		List<ItemStack> items = new ObjectArrayList<>();
		NonNullList<ItemStack> helper = NonNullList.create();
		for(Item item : ForgeRegistries.ITEMS) {
			item.fillItemCategory(ItemGroup.TAB_SEARCH, helper);
			items.addAll(helper);
			helper.clear();
		}
		items.removeIf(ItemStack::isEmpty);
		long time = System.nanoTime();
		List<WrappedEnchantment> enchantments = new ArrayList<>(ForgeRegistries.ENCHANTMENTS.getValues()).parallelStream().filter(this::isEnabled).map(T -> new WrappedEnchantment(T, items)).collect(Collectors.toCollection(ObjectArrayList::new));
		time = System.nanoTime() - time;
		UEBase.LOGGER.info("JEI Plugin LoadTime: "+(time/1000000)+"ms");
		enchantments.sort(null);
		registration.addRecipes(enchantments, new ResourceLocation("uniquebase", "ue_enchantments"));
	}
	
	private boolean isEnabled(Enchantment ench) 
	{
		return !MiscUtil.isDisabled(ench);
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		if(UEBase.DISABLE_JEI.get()) return;
		registry.addRecipeCategories(new EnchantmentCategory(registry.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation("uniquebase", "core");
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		if(UEBase.DISABLE_JEI.get()) return;
		NonNullList<ItemStack> list = NonNullList.create();
		Items.ENCHANTED_BOOK.fillItemCategory(ItemGroup.TAB_SEARCH, list);
		list.sort(Comparator.comparing(this::getLevel).reversed());
		Set<Enchantment> ench = new HashSet<>();
		list.removeIf(T -> {
			Enchantment en = getFirstEnchantment(T);
			return en == null || ench.add(en);
		});
		jeiRuntime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, list);
	}
	
	public int getLevel(ItemStack item) {
		int maxLevel = 0;
		
		for(int level : EnchantmentHelper.getEnchantments(item).values()) {
			maxLevel = Math.max(maxLevel, level);
		}
		return maxLevel;
	}
	
	public Enchantment getFirstEnchantment(ItemStack item) {
		Iterator<Enchantment> it = EnchantmentHelper.getEnchantments(item).keySet().iterator();
		return it.hasNext() ? it.next() : null;
	}
	
}