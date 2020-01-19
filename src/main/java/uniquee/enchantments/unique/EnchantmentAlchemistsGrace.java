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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.ForgeRegistries;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IGraceEnchantment;
import uniquee.utils.IntStat;
import uniquee.utils.MiscUtil;

public class EnchantmentAlchemistsGrace extends UniqueEnchantment implements IGraceEnchantment
{
	public static List<List<EffectInstance>> EFFECTS = new ObjectArrayList<List<EffectInstance>>();
	public static IntStat SECONDS = new IntStat(4, "seconds");
	static ConfigValue<List<? extends String>> EFFECT_CONFIG;
	
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
							base.addPotionEffect(new EffectInstance(effect.getPotion(), effect.getDuration() * time, effect.getAmplifier()));
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
		config.comment("Which Potion Effects should be applied. Format: MinimumEnchantLevel;Potion;PotionLevel;BaseDuration");
		EFFECT_CONFIG = config.defineList("effects", ObjectArrayList.wrap(new String[]{"1;minecraft:speed;1;4", "2;minecraft:haste;1;6", "2;minecraft:speed;2;6", "3;minecraft:resistance;1;8", "3;minecraft:haste;2;8", "4;minecraft:strength;2;10", "4;minecraft:resistance;2;10"}), (T) -> true);
	}
	
	@Override
	public void onConfigChanged()
	{
		EFFECTS.clear();
		List<? extends String> list = EFFECT_CONFIG.get();
		for(int i = 0,m=list.size();i<m;i++)
		{
			String[] split = list.get(i).split(";");
			if(split.length < 4)
			{
				continue;
			}
			Effect p = ForgeRegistries.POTIONS.getValue(new ResourceLocation(split[1]));
			if(p != null)
			{
				try
				{
					int index = Integer.parseInt(split[0]);
					while(EFFECTS.size() <= index)
					{
						EFFECTS.add(new ObjectArrayList<EffectInstance>());
					}
					EFFECTS.get(index).add(new EffectInstance(p, Integer.parseInt(split[3]) * 20, Integer.parseInt(split[2])));	
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
