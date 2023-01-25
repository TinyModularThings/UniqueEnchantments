package uniquebase.jei;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.plugins.vanilla.anvil.AnvilRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.UEBase;
import uniquebase.utils.MiscUtil;

@JeiPlugin
public class JEIView implements IModPlugin
{
	public static final RecipeType<WrappedEnchantment> REGISTRY = RecipeType.create("uniquebase", "ue_enchantments", WrappedEnchantment.class);
	
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		if(UEBase.DISABLE_JEI.get()) return;
		List<ItemStack> items = new ObjectArrayList<>();
		NonNullList<ItemStack> helper = NonNullList.create();
		for(Item item : ForgeRegistries.ITEMS) {
			item.fillItemCategory(CreativeModeTab.TAB_SEARCH, helper);
			items.addAll(helper);
			helper.clear();
		}
		items.removeIf(ItemStack::isEmpty);
		long time = System.nanoTime();
		List<WrappedEnchantment> enchantments = new ArrayList<>(ForgeRegistries.ENCHANTMENTS.getValues()).parallelStream().filter(this::isEnabled).map(T -> new WrappedEnchantment(T, items)).collect(Collectors.toCollection(ObjectArrayList::new));
		time = System.nanoTime() - time;
		UEBase.LOGGER.info("JEI Plugin LoadTime: "+(time/1000000)+"ms");
		enchantments.sort(null);
		registration.addRecipes(REGISTRY, enchantments);
		List<IJeiAnvilRecipe> recipes = new ObjectArrayList<>();		
		for(int i = 0,m=items.size();i<m;i++)
		{
			ItemStack stack = items.get(i);
			if(stack.getItem() instanceof ShieldItem) recipes.add(setRepairCost(stack, new ItemStack(Items.TUBE_CORAL_BLOCK)));
			if(net.minecraft.world.item.enchantment.EnchantmentCategory.ARMOR.canEnchant(stack.getItem())) recipes.add(setRepairCost(stack, new ItemStack(Items.BRAIN_CORAL_BLOCK)));
			if(net.minecraft.world.item.enchantment.EnchantmentCategory.BOW.canEnchant(stack.getItem()) || net.minecraft.world.item.enchantment.EnchantmentCategory.CROSSBOW.canEnchant(stack.getItem())) recipes.add(setRepairCost(stack, new ItemStack(Items.BUBBLE_CORAL_BLOCK)));
			if(net.minecraft.world.item.enchantment.EnchantmentCategory.WEAPON.canEnchant(stack.getItem())) recipes.add(setRepairCost(stack, new ItemStack(Items.FIRE_CORAL_BLOCK)));
			if(net.minecraft.world.item.enchantment.EnchantmentCategory.DIGGER.canEnchant(stack.getItem())) recipes.add(setRepairCost(stack, new ItemStack(Items.HORN_CORAL_BLOCK)));
		}
		registration.addRecipes(RecipeTypes.ANVIL, recipes);
	}
	
	private AnvilRecipe setRepairCost(ItemStack stack, ItemStack item)
	{
		ItemStack copy = stack.copy();
		copy.setRepairCost(5);
		return new AnvilRecipe(ObjectLists.singleton(copy), ObjectLists.singleton(item), ObjectLists.singleton(stack));
	}
	
	private boolean isEnabled(Enchantment ench) 
	{
		return !MiscUtil.isDisabled(ench);
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		if(UEBase.DISABLE_JEI.get()) return;
		registry.addRecipeCategories(new EnchantmentCategory(registry.getJeiHelpers().getGuiHelper(), REGISTRY));
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
		Items.ENCHANTED_BOOK.fillItemCategory(CreativeModeTab.TAB_SEARCH, list);
		list.sort(Comparator.comparing(this::getLevel).reversed());
		Set<Enchantment> ench = new HashSet<>();
		list.removeIf(T -> {
			Enchantment en = getFirstEnchantment(T);
			return en == null || ench.add(en);
		});
		jeiRuntime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, list);
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