package uniquebase.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
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
import uniquebase.api.BaseUEMod;

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
			for(BaseUEMod mod : BaseUEMod.getMods())
			{
				for(String entry : mod.getConfig().getCategoryNames())
				{
					if(entry.contains("."))
					{
						continue;
					}
					elements.add(new UECategoryElement(UEEntry.class, entry.substring(0, 1).toUpperCase() + entry.substring(1), mod.getConfig()));
				}
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
			List<IConfigElement> elements = new ObjectArrayList<>();
			if(configElement instanceof UECategoryElement)
			{
				cat = ((UECategoryElement)configElement).category;
				elements.addAll(new UEConfigElement(((UECategoryElement)configElement).config.getCategory(cat.toLowerCase())).getChildElements());
			}
			return new GuiConfig(owningScreen, elements, this.owningScreen.modID, cat, false, false, cat);
		}
	}
	
	public static class UEConfigElement extends ConfigElement
	{
		ConfigCategory category;
		public UEConfigElement(ConfigCategory category)
		{
			super(category);
			this.category = category;
		}
		
	    public UEConfigElement(Property prop)
	    {
	    	super(prop);
	    }
		
		@Override
		public String getComment()
		{
	        return isProperty() ? super.getComment() : I18n.format(getLanguageKey() + ".desc");
		}
		
		@Override
		public List<IConfigElement> getChildElements()
		{
	        if (!isProperty())
	        {
	            List<IConfigElement> elements = new ArrayList<IConfigElement>();
	            Iterator<ConfigCategory> ccI = category.getChildren().iterator();
	            Iterator<Property> pI = category.getOrderedValues().iterator();

                while (ccI.hasNext())
                {
                    ConfigElement temp = new UEConfigElement(ccI.next());
                    if (temp.showInGui()) // don't bother adding elements that shouldn't show
                        elements.add(temp);
                }
	            while (pI.hasNext())
	            {
	                ConfigElement temp = new UEConfigElement(pI.next());
	                if (temp.showInGui())
	                    elements.add(temp);
	            }
	            return elements;
	        }
	        return null;
		}
	}
	
	public static class UECategoryElement extends DummyConfigElement
	{
		String category;
		Configuration config;

		public UECategoryElement(Class<? extends IConfigEntry> customListEntryClass, String cat, Configuration config)
		{
			super(cat, null, ConfigGuiType.CONFIG_CATEGORY, cat);
			this.childElements = new ArrayList<IConfigElement>();
			this.configEntryClass = customListEntryClass;
			isProperty = false;
			category = cat;
			this.config = config;
		}
	}
}
