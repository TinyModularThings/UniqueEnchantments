package uniquebase.jei;

import java.util.Collections;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnchantmentWrapper implements IRecipeWrapper
{
	WrappedEnchantment enchantment;
	
	public EnchantmentWrapper(WrappedEnchantment enchantment)
	{
		this.enchantment = enchantment;
	}
	
	@Override
	public void getIngredients(IIngredients ingridients)
	{
		List<List<ItemStack>> list = new ObjectArrayList<>();
		for(int i = 0;i<7;i++) list.add(new ObjectArrayList<>());
		for(int i = 0,m=enchantment.validItems.size();i<m;i++)
		{
			list.get(1 + (i % 6)).add(enchantment.validItems.get(i));
		}
		for(int i = enchantment.ench.getMinLevel(),m=enchantment.ench.getMaxLevel();i<=m;i++)
		{
			ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
			ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchantment.ench, i));
			list.get(0).add(stack);
		}
		ingridients.setInputLists(VanillaTypes.ITEM, list);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		FontRenderer font = minecraft.fontRenderer;
		String s = TextFormatting.UNDERLINE+I18n.format(enchantment.ench.getName());
		font.drawString(s, 85 - (font.getStringWidth(s) / 2), 3, 0);
		font.drawString("Max Level: "+TextFormatting.WHITE.toString()+enchantment.ench.getMaxLevel(), 3, 18, 0x1CD679);
		font.drawString("Treasure: "+isTrue(enchantment.ench.isTreasureEnchantment()), 3, 28, 0xBA910D);
		font.drawString("Curse: "+isTrue(enchantment.ench.isCurse()), 3, 38, 0xA11E15);
		font.drawString("Rarity: "+getFormatting(enchantment.ench.getRarity())+toPascalCase(enchantment.ench.getRarity().name().toLowerCase()), 3, 48, 0xCB19D1);
		s = getDescription();
		if((font.getWordWrappedHeight(s, 95) / font.FONT_HEIGHT) > 7)
		{
			GlStateManager.scale(0.5D, 0.5D, 1D);
			font.drawSplitString(s, 138, 126, 190, 0);
			GlStateManager.scale(2D, 2D, 1D);
		}
		else font.drawSplitString(s, 69, 63, 95, 0);
		s = getIncompats();
		GlStateManager.scale(0.5D, 0.5D, 1D);
		font.drawSplitString(s, 4, 126, 122, 0);
		GlStateManager.scale(2D, 2D, 1D);
		minecraft.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/beacon.png"));
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.scale(0.5D, 0.5D, 1D);
		Gui.drawModalRectWithCustomSizedTexture(3 + (font.getStringWidth("Treasure: ") * 2), 55, 90 + (enchantment.ench.isTreasureEnchantment() ? 0 : 22), 220, 18, 18, 256, 256);
		Gui.drawModalRectWithCustomSizedTexture(3 + (font.getStringWidth("Curse: ") * 2), 74, 90 + (enchantment.ench.isCurse() ? 0 : 22), 220, 18, 18, 256, 256);
		GlStateManager.scale(2D, 2D, 1D);
	}
	
	private String getIncompats()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Incompatible with: \n\n");
		if(enchantment.incompats.isEmpty())
		{
			builder.append("- Nothing");
		}
		else
		{
			for(Enchantment ench : enchantment.incompats)
			{
				builder.append("- ").append(I18n.format(ench.getName())).append("\n");
			}
		}
		return builder.toString();
	}
	
	private String getDescription()
	{
		String s = I18n.format("enchantment."+enchantment.ench.getRegistryName().getNamespace()+"."+enchantment.ench.getRegistryName().getPath()+".desc");
		if(s.startsWith("enchantment.")) return "No Description";
		return s;
	}
	
	private String isTrue(boolean value)
	{
		return "";
//		return value ? TextFormatting.RED+"Yes" : TextFormatting.GREEN+"No";
	}
	
	private TextFormatting getFormatting(Rarity rarity)
	{
		switch(rarity)
		{
			case COMMON: return TextFormatting.WHITE;
			case RARE: return TextFormatting.AQUA;
			case UNCOMMON: return TextFormatting.YELLOW;
			case VERY_RARE: return TextFormatting.LIGHT_PURPLE;
			default: return TextFormatting.OBFUSCATED;
		}
	}
	
	public static String toPascalCase(String input)
	{
		StringBuilder builder = new StringBuilder();
		for(String s : input.replaceAll("_", " ").replaceAll("-", " ").split(" "))
		{
			builder.append(firstLetterUppercase(s)).append(" ");
		}
		return builder.substring(0, builder.length() - 1);
	}
	
	public static String firstLetterUppercase(String string) {
		if(string == null || string.isEmpty()) {
			return string;
		}
		String first = Character.toString(string.charAt(0));
		return string.replaceFirst(first, first.toUpperCase());
	}
	
	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY)
	{
		return Collections.emptyList();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton)
	{
		return false;
	}
}
