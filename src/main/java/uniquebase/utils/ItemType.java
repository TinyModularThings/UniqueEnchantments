package uniquebase.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;

public class ItemType
{
    static final Map<String, ItemType> REGISTRY = Object2ObjectMaps.synchronize(new Object2ObjectLinkedOpenHashMap<>());
    public static final ItemType ALL = create("all", T -> true);
    public static final ItemType SWORD = create("sword", SwordItem.class::isInstance);
    public static final ItemType TRIDENT = create("trident", TridentItem.class::isInstance);
    public static final ItemType BOW = create("bow", BowItem.class::isInstance);
    public static final ItemType CROSSBOW = create("crossbow", CrossbowItem.class::isInstance);
    public static final ItemType TOOL = create("tool", ToolItem.class::isInstance);
    public static final ItemType AXE = create("axe", AxeItem.class::isInstance);
    public static final ItemType SHOVEL = create("shovel", ShovelItem.class::isInstance);
    public static final ItemType HOE = create("hoe", HoeItem.class::isInstance);
    public static final ItemType PICKAXE = create("pickaxe", PickaxeItem.class::isInstance);
    public static final ItemType ARMOR = create("armor", ArmorItem.class::isInstance);
    public static final ItemType ELYTRA = create("elytra", ElytraItem.class::isInstance);
    public static final ItemType HELMET = new ItemType("helmet", T -> T.getItem() instanceof ArmorItem && MobEntity.getEquipmentSlotForItem(T) == EquipmentSlotType.HEAD);
    public static final ItemType CHEST = new ItemType("chest", T -> T.getItem() instanceof ArmorItem && MobEntity.getEquipmentSlotForItem(T) == EquipmentSlotType.CHEST);
    public static final ItemType LEGS = new ItemType("legs", T -> T.getItem() instanceof ArmorItem && MobEntity.getEquipmentSlotForItem(T) == EquipmentSlotType.LEGS);
    public static final ItemType BOOTS = new ItemType("boots", T -> T.getItem() instanceof ArmorItem && MobEntity.getEquipmentSlotForItem(T) == EquipmentSlotType.FEET);
    
    String id;
    Predicate<ItemStack> tester;
    
    public ItemType(String id, Predicate<ItemStack> tester)
    {
        this.id = id;
        this.tester = tester;
        REGISTRY.put(id, this);
    }
    
    public static ItemType create(String id, Predicate<Item> test)
    {
        return new ItemType(id, T -> test.test(T.getItem()));
    }
    
   public static List<ItemType> getAllTypes() {
	   return new ObjectArrayList<>(REGISTRY.values());
   }
    
    public static ItemType byId(String id)
    {
        return REGISTRY.get(id);
    }
    public static Set<ItemType> byIds(String...ids)
    {
    	Set<ItemType> result = new HashSet<ItemType>();
    	for(String id : ids) {
    		result.add(REGISTRY.get(id));
    	}
        return result;
    }
    
    public boolean isValid(ItemStack stack)
    {
        return !stack.isEmpty() && tester.test(stack);
    }
    
    public static Set<ItemType> getValidTypes(ItemStack stack, List<ItemType> list)
    {
        Set<ItemType> result = new ObjectLinkedOpenHashSet<>();
        for(ItemType type : list)
        {
            if(type.isValid(stack)) result.add(type);
        }
        return result;
    }
}