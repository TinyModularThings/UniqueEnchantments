package uniquee.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uniquee.UniqueEnchantments;

@SideOnly(Side.CLIENT)
public class ConfigHandler implements IModGuiFactory
{
	
	@Override
	public void initialize(Minecraft minecraftInstance)
	{
		
	}
	
	@Override
	public boolean hasConfigGui()
	{
		return true;
	}
	
	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen)
	{
		return new UEGuiConfig(parentScreen);
	}
	
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
	{
		return null;
	}
	
	public static class UEGuiConfig extends GuiConfig
	{
		
		public UEGuiConfig(GuiScreen parentScreen)
		{
			super(parentScreen, getElements(), "UE", false, false, "UE Classic Config");
		}
		
		static List<IConfigElement> getElements()
		{
			List<IConfigElement> elements = new ArrayList<IConfigElement>();
			for(String entry : UniqueEnchantments.CONFIG.getCategoryNames())
			{
				elements.add(new UECategoryElement(UEEntry.class, entry.substring(0, 1).toUpperCase() + entry.substring(1)));
			}
			return elements;
		}
		
	}
	
	public static class UEEntry extends CategoryEntry
	{
		public UEEntry(GuiConfig par1, GuiConfigEntries par2, IConfigElement par3)
		{
			super(par1, par2, par3);
		}
		
		@Override
		protected GuiScreen buildChildScreen()
		{
			String cat = "general";
			if(configElement instanceof UECategoryElement)
			{
				cat = ((UECategoryElement)configElement).category;
			}
			String displayCat = cat;
			cat = cat.toLowerCase();
			return new GuiConfig(owningScreen, new ConfigElement(UniqueEnchantments.CONFIG.getCategory(cat)).getChildElements(), this.owningScreen.modID, displayCat, false, false, displayCat);
		}
	}
	
	public static class UECategoryElement extends DummyConfigElement
	{
		String category;
		
		public UECategoryElement(Class<? extends IConfigEntry> customListEntryClass, String cat)
		{
			super(cat, null, ConfigGuiType.CONFIG_CATEGORY, cat);
			this.childElements = new ArrayList<IConfigElement>();
			this.configEntryClass = customListEntryClass;
			isProperty = false;
			category = cat;
		}
		
	}
}
