package uniquebase.api;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.UEBase;
import uniquebase.utils.MiscUtil;

public class ColorConfig
{
	private final int textColor;
	private final int backgroundColor;
	private final int borderStartColor;
	private final int borderEndColor;
	
	public ColorConfig()
	{
		this(-1, -1, -1, -1);
	}
	
	public ColorConfig(int textColor, int backgroundColor, int borderStartColor, int borderEndColor)
	{
		this.textColor = textColor | 0xFF000000;
		this.backgroundColor = backgroundColor;
		this.borderStartColor = borderStartColor;
		this.borderEndColor = borderEndColor;
	}
	
	public boolean isDefault() {
		return textColor == -1 && backgroundColor == -1 && borderStartColor == -1 && borderEndColor == -1;
	}
	
	public static ColorConfig fromText(String[] colors)
	{
		int size = colors.length;
		try
		{
			ColorConfig config = new ColorConfig(MiscUtil.parseColor(size >= 2 ? colors[1] : null, -1), MiscUtil.parseColor(size >= 3 ? colors[2] : null, -1), MiscUtil.parseColor(size >= 4 ? colors[3] : null, -1), MiscUtil.parseColor(size >= 5 ? colors[4] : null, -1));
			return config.isDefault() ? null : config;
		}
		catch(Exception e)
		{
			UEBase.LOGGER.info("Error Parsing Color"+ObjectArrayList.wrap(colors));
			UEBase.LOGGER.catching(e);
		}
		return null;
	}
	
	public int getTextColor()
	{
		return textColor;
	}

	public int getBackgroundColor()
	{
		return backgroundColor;
	}

	public int getBorderStartColor()
	{
		return borderStartColor;
	}

	public int getBorderEndColor()
	{
		return borderEndColor;
	}
	
	//Helper tools
	
	public static List<String> createColorConfig() {
		List<String> list = new ObjectArrayList<>();
		StringBuilder builder = new StringBuilder();
		setupDefaultColors((E, M, B, S, T) -> {
			builder.append(E.toString()).append(";");
			builder.append(M == null ? "" : M).append(";");
			builder.append(B == null ? "" : B).append(";");
			builder.append(S == null ? "" : S).append(";");
			builder.append(T == null ? "" : T);
			list.add(builder.toString());
			builder.setLength(0);
		});
		return list;
	}
	
	public static void setupDefaultColors(IColorConfigBuilder acceptor) 
	{
		for(Enchantment ench : ForgeRegistries.ENCHANTMENTS) {
			ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(ench);
			if(!(id.getNamespace().equals("minecraft")) || !(id.getNamespace().equals("uniquee")) || !(id.getNamespace().equals("uniquebattle")) || !(id.getNamespace().equals("uniqueutil"))) {
				acceptor.addColor(id, MiscUtil.toHex(MiscUtil.getColorFromText(id.getPath())), null, null, null);
			}
		}
		
		acceptor.addColor(Enchantments.AQUA_AFFINITY, "#4696e5", null, null, null);
		acceptor.addColor(Enchantments.BANE_OF_ARTHROPODS, "#373f52", null, null, null);
		acceptor.addColor(Enchantments.BLAST_PROTECTION, "#a6b5ba", null, null, null);
		acceptor.addColor(Enchantments.CHANNELING, "#3486e5", null, null, null); 
		acceptor.addColor(Enchantments.BINDING_CURSE, "#FF0000", null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(Enchantments.VANISHING_CURSE, "#FF0000", null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(Enchantments.DEPTH_STRIDER, "#4696e5", null, null, null);
		acceptor.addColor(Enchantments.BLOCK_EFFICIENCY, "#899aa0", null, null, null);
		acceptor.addColor(Enchantments.FALL_PROTECTION, "#899aa0", null, null, null);
		acceptor.addColor(Enchantments.FIRE_ASPECT, "#ee6c2a", null, null, null);
		acceptor.addColor(Enchantments.FIRE_PROTECTION, "#ee6c2a", null, null, null);
		acceptor.addColor(Enchantments.FLAMING_ARROWS, "#ee6c2a", null, null, null);
		acceptor.addColor(Enchantments.BLOCK_FORTUNE, "#e4ca3a", null, null, null);
		acceptor.addColor(Enchantments.FROST_WALKER, "#a6b5ba", null, null, null);
		acceptor.addColor(Enchantments.IMPALING, "#3486e5", null, null, null); 
		acceptor.addColor(Enchantments.INFINITY_ARROWS, "#373f52", null, null, null);
		acceptor.addColor(Enchantments.KNOCKBACK, "#32f094", null, null, null);
		acceptor.addColor(Enchantments.MOB_LOOTING, "#8f4c03", null, null, null);
		acceptor.addColor(Enchantments.LOYALTY, "#3486e5", null, null, null); 
		acceptor.addColor(Enchantments.FISHING_LUCK, "#4696e5", null, null, null);
		acceptor.addColor(Enchantments.FISHING_SPEED, "#4696e5", null, null, null);
		acceptor.addColor(Enchantments.MENDING, "#32f094", null, null, null); 
		acceptor.addColor(Enchantments.MULTISHOT, "#8f4c03", null, null, null); 
		acceptor.addColor(Enchantments.PIERCING, "#373f52", null, null, null);
		acceptor.addColor(Enchantments.POWER_ARROWS, "#b64b6e", null, null, null);
		acceptor.addColor(Enchantments.PROJECTILE_PROTECTION, "#b64b6e", null, null, null);
		acceptor.addColor(Enchantments.ALL_DAMAGE_PROTECTION, "#a6b5ba", null, null, null);
		acceptor.addColor(Enchantments.PUNCH_ARROWS, "#c25e6e", null, null, null);
		acceptor.addColor(Enchantments.QUICK_CHARGE, "#373f52", null, null, null);
		acceptor.addColor(Enchantments.RESPIRATION, "#4696e5", null, null, null);
		acceptor.addColor(Enchantments.RIPTIDE, "#3486e5", null, null, null); 
		acceptor.addColor(Enchantments.SHARPNESS, "#899aa0", null, null, null);
		acceptor.addColor(Enchantments.SILK_TOUCH, "#1b8a43", null, null, null);
		acceptor.addColor(Enchantments.SMITE, "#20b63a", null, null, null);
		acceptor.addColor(Enchantments.SOUL_SPEED, "#63667d", null, null, null);
		acceptor.addColor(Enchantments.SWEEPING_EDGE, "#a6b5ba", null, null, null);
		acceptor.addColor(Enchantments.SWIFT_SNEAK, "#32f094", null, null, null); //SwiftSneak 1.19
		acceptor.addColor(Enchantments.THORNS, "#1b8a43", null, null, null);
		acceptor.addColor(Enchantments.UNBREAKING, "#b64b6e", null, null, null);

		acceptor.addColor(new ResourceLocation("uniquee:ameliorated_sharpness"), "#e4ca3a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:ameliorated_bane_of_arthropods"), "#e4ca3a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:ameliorated_smite"), "#e4ca3a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:berserker"), "#b64b6e", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:bone_crusher"), "#a6b5ba", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:ender_eyes"), "#8d38d4", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:focused_impact"), "#373f52", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:range"), "#899aa0", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:sages_blessing"), "#20b63a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:swift"), "#373f52", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:treasurers_eyes"), "#e4b83a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:vitae"), "#c1cfd4", null, null, null);

		acceptor.addColor(new ResourceLocation("uniquee:ender_mending"), "#b54ee9", null, null, null); 
		acceptor.addColor(new ResourceLocation("uniquee:climate_tranquility"), "#e4ee3a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:momentum"), "#75e580", null, null, null); 
		acceptor.addColor(new ResourceLocation("uniquee:perpetual_strike"), "#899aa0", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:smart_ass"), "#373f52", null, null, null); 
		acceptor.addColor(new ResourceLocation("uniquee:spartan_weapon"), "#899aa0", null, null, null); 
		acceptor.addColor(new ResourceLocation("uniquee:swift_blade"), "#e4ca3a", null, null, null);
		
		acceptor.addColor(new ResourceLocation("uniquee:alchemists_grace"), "#e4ca3a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:ares_blessing"), "#20b63a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:cloud_walker"), "#c1cfd4", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:ecological"), "#50cf66", null, null, null); 
		acceptor.addColor(new ResourceLocation("uniquee:ender_librarian"), "#b54ee9", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:ender_marksmen"), "#b54ee9", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:endest_reap"), "#b54ee9", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:fast_food"), "#793303", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:icarus_aegis"), "#899aa0", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:ifrits_grace"), "#e4ca3a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:midas_blessing"), "#20b63a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:natures_grace"), "#e4ca3a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:phoenixs_blessing"), "#20b63a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquee:warriors_grace"), "#e4ca3a", null, null, null);
		
		acceptor.addColor(new ResourceLocation("uniquebattle:ares_grace"), "#e4ca3a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquebattle:hecates_blessing"), "#20b63a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquebattle:fury"), "#880604", null, null, null); 
		acceptor.addColor(new ResourceLocation("uniquebattle:golem_soul"), "#899aa0", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquebattle:iron_bird"), "#76888f", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquebattle:snare"), "#373f52", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquebattle:streakers_will"), "#75e580", null, null, null);
		
		acceptor.addColor(new ResourceLocation("uniquebattle:ares_fragment"), "#a53e6e", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquebattle:artemis_soul"), "#899aa0", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquebattle:deep_wounds"), "#b64b6e", null, null, null); 
		acceptor.addColor(new ResourceLocation("uniquebattle:ifrits_blessing"), "#20b63a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniquebattle:granis_soul"), "#899aa0", null, null, null);
		
		acceptor.addColor(new ResourceLocation("uniqueutil:adept"), "#e4ca3a", null, null, null); 
		acceptor.addColor(new ResourceLocation("uniqueutil:thick_pick"), "#899aa0", null, null, null);
		
		acceptor.addColor(new ResourceLocation("uniqueutil:alchemists_blessing"), "#20b63a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniqueutil:climber"), "#899aa0", null, null, null); 
		acceptor.addColor(new ResourceLocation("uniqueutil:essence_of_slime"), "#4eba75", null, null, null);
		acceptor.addColor(new ResourceLocation("uniqueutil:demeters_blessing"), "#20b63a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniqueutil:sleipnirs_grace"), "#e4ca3a", null, null, null);
		
		acceptor.addColor(new ResourceLocation("uniqueutil:ambrosia"), "#e4ca3a", null, null, null);
		acceptor.addColor(new ResourceLocation("uniqueutil:anemoi_fragment"), "#a6b5ba", null, null, null);
		acceptor.addColor(new ResourceLocation("uniqueutil:demeters_soul"), "#899aa0", null, null, null);
		acceptor.addColor(new ResourceLocation("uniqueutil:mounting_aegis"), "#899aa0", null, null, null); 
		acceptor.addColor(new ResourceLocation("uniqueutil:pegasus_soul"), "#899aa0", null, null, null);
		acceptor.addColor(new ResourceLocation("uniqueutil:poseidons_soul"), "#899aa0", null, null, null);
		acceptor.addColor(new ResourceLocation("uniqueutil:reinforced"), "#a53e6e", null, null, null); 
		acceptor.addColor(new ResourceLocation("uniqueutil:resonance"), "#373f52", null, null, null);
		acceptor.addColor(new ResourceLocation("uniqueutil:sages_soul"), "#899aa0", null, null, null);
		
		

		acceptor.addColor(new ResourceLocation("uniquee:deaths_odium"), "#FF0000", null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquee:pestilences_odium"), "#FF0000", null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquee:grimoire"), "#FF0000", null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquee:combo_star"), "#FF0000", null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquee:pestilences_odium"), "#FF0000", null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniqueutil:phanes_regret"), "#FF0000", null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniqueutil:rocket_man"), "#FF0000", null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniqueutil:famines_odium"), "#FF0000", null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquebattle:ifrits_judgement"), "#FF0000", null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquebattle:lunatic_despair"), "#FF0000", null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquebattle:wars_odium"), "#FF0000", null, "#C22237FF", "#EA0087FF");
		
		acceptor.addColor(new ResourceLocation("uniqueapex:absolute_protection"), "#e4b83a", null, "#EAB8A3FF", "#EAB8A3FF");
		acceptor.addColor(new ResourceLocation("uniqueapex:blessed_blade"), "#e4b83a", null, "#EAB8A3FF", "#EAB8A3FF");
		acceptor.addColor(new ResourceLocation("uniqueapex:second_life"), "#e4b83a", null, "#EAB8A3FF", "#EAB8A3FF");
		acceptor.addColor(new ResourceLocation("uniqueapex:aeons_fragment"), "#e4b83a", null, "#EAB8A3FF", "#EAB8A3FF");;
		acceptor.addColor(new ResourceLocation("uniqueapex:gaias_fragment"), "#e4b83a", null, "#EAB8A3FF", "#EAB8A3FF");;
		acceptor.addColor(new ResourceLocation("uniqueapex:pickaxe404"), "#e4b83a", null, "#EAB8A3FF", "#EAB8A3FF");
	}
	
	public static interface IColorConfigBuilder
	{
		public void addColor(ResourceLocation location, String textColor, String background, String startColor, String endColor);
		public default void addColor(Enchantment ench, String textColor, String background, String startColor, String endColor) {
			addColor(ForgeRegistries.ENCHANTMENTS.getKey(ench), textColor, background, startColor, endColor);
		}
	}
}
