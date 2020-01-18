package uniquee.enchantments.unique;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IGraceEnchantment;
import uniquee.utils.IntStat;
import uniquee.utils.MiscUtil;

public class EnchantmentAlchemistsGrace extends UniqueEnchantment implements IGraceEnchantment
{
	public static List<List<EffectInstance>> EFFECTS = new ObjectArrayList<List<EffectInstance>>();
	public static IntStat SECONDS = new IntStat(4, "seconds");
	
	public EnchantmentAlchemistsGrace()
	{
		super(new DefaultData("alchemistsgrace", Rarity.VERY_RARE, true, 20, 3, 18), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 4;
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem || EnchantmentType.BOW.canEnchantItem(stack.getItem());
	}
		
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentWarriorsGrace || ench instanceof EnchantmentNaturesGrace ? false : super.canApplyTogether(ench);
	}
	
	public static void applyToEntity(Entity entity)
	{
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			Object2IntMap.Entry<EquipmentSlotType> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.ALCHEMISTS_GRACE, base);
			if(slot.getIntValue() > 0)
			{
				int level = slot.getIntValue();
				int time = SECONDS.get() * 20;
				Set<Effect> potions = new ObjectOpenHashSet<Effect>();
				for(int i = level;i>=0;i--)
				{
					if(EFFECTS.size() <= i)
					{
						continue;
					}
					for(EffectInstance effect : EFFECTS.get(i))
					{
						if(potions.add(effect.getPotion()))
						{
							base.addPotionEffect(new EffectInstance(effect.getPotion(), effect.getDuration() * level * time, effect.getAmplifier()));
						}
					}
				}
			}
		}
	}
	
	@Override
	public void loadData(Builder config)
	{
		SECONDS.handleConfig(config);
	}
}
