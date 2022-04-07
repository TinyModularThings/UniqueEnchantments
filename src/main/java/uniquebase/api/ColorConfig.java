package uniquebase.api;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.ResourceLocation;
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
			builder.append(T == null ? "" : T).append(";");
			list.add(builder.toString());
			builder.setLength(0);
		});
		return list;
	}
	
	public static void setupDefaultColors(IColorConfigBuilder acceptor) 
	{
		acceptor.addColor(Enchantments.SHARPNESS, "#32f094", null, null, null);
		acceptor.addColor(Enchantments.BINDING_CURSE, null, null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(Enchantments.VANISHING_CURSE, null, null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquee:deaths_odium"), null, null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquee:pestilences_odium"), null, null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquee:grimoire"), null, null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquee:pestilences_odium"), null, null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniqueutil:phanes_regret"), null, null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniqueutil:rocketman"), null, null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniqueutil:famines_odium"), null, null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquebattle:ifrits_judgement"), null, null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquebattle:lunatic_despair"), null, null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniquebattle:wars_odium"), null, null, "#C22237FF", "#EA0087FF");
		acceptor.addColor(new ResourceLocation("uniqueapex:absolute_protection"), null, null, "#EAB8A3FF", "#EAB8A3FF");
		acceptor.addColor(new ResourceLocation("uniqueapex:blessed_blade"), null, null, "#EAB8A3FF", "#EAB8A3FF");
		acceptor.addColor(new ResourceLocation("uniqueapex:second_life"), null, null, "#EAB8A3FF", "#EAB8A3FF");
		acceptor.addColor(new ResourceLocation("uniqueapex:aeons_fragment"), null, null, "#EAB8A3FF", "#EAB8A3FF");
	}
	
	public static interface IColorConfigBuilder
	{
		public void addColor(ResourceLocation location, String textColor, String background, String startColor, String endColor);
		public default void addColor(Enchantment ench, String textColor, String background, String startColor, String endColor) {
			addColor(ench.getRegistryName(), textColor, background, startColor, endColor);
		}
	}
}
