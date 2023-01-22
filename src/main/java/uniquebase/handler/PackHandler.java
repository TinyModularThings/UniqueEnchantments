package uniquebase.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.Pack.PackConstructor;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.locating.IModFile;
import uniquebase.UEBase;

public class PackHandler
{
	public static void loaded()
	{
		Minecraft.getInstance().getResourcePackRepository().addPackFinder(new PackFinder());
		if(UEBase.SHOULD_LOAD_RESOURCEPACK)
		{
			List<String> list = Minecraft.getInstance().options.resourcePacks;
			if(!list.contains("mod:uepack")) list.add("mod:uepack");
		}
	}
	
	public static class PackFinder implements RepositorySource
	{
		@Override
		public void loadPacks(Consumer<Pack> acceptor, PackConstructor factory)
		{
			acceptor.accept(Pack.create("mod:uepack", false, () -> new DeveloperPack(ModList.get().getModFileById("uniquebase").getFile()), factory, Pack.Position.TOP, PackSource.BUILT_IN));
		}
	}
	
	public static class DeveloperPack extends AbstractPackResources
	{
	    private final IModFile modFile;
		
	    public DeveloperPack(IModFile modFile)
		{
			super(new File("dummy"));
			this.modFile = modFile;
		}
	    
		@Override
		public void close() {}
		
		@Override
		protected boolean hasResource(String name)
		{
	        return Files.exists(modFile.findResource(name.replaceFirst("minecraft", "mc")));
		}
		
		@Override
		protected InputStream getResource(String name) throws IOException
		{
			name = name.replaceFirst("minecraft", "mc");
	        final Path path = modFile.findResource(name);
	        if(!Files.exists(path)) throw new FileNotFoundException(name);
	        return Files.newInputStream(path, StandardOpenOption.READ);		
		}
		
		@Override
		public Collection<ResourceLocation> getResources(PackType type, String namespaceIn, String pathIn, Predicate<ResourceLocation> filterIn)
		{
			if(type == PackType.SERVER_DATA) return Collections.emptyList();
            Path root = modFile.findResource(type.getDirectory(), namespaceIn.replaceFirst("minecraft", "mc")).toAbsolutePath();
            Path inputPath = root.getFileSystem().getPath(pathIn.replaceFirst("minecraft", "mc"));
            try { return Files.walk(root).map(path -> root.relativize(path.toAbsolutePath())).filter(path -> !path.toString().endsWith(".mcmeta")).filter(path -> path.startsWith(inputPath)).filter(path -> filterIn.test(new ResourceLocation(namespaceIn, path.getFileName().toString()))).map(path -> new ResourceLocation(namespaceIn, Joiner.on('/').join(path))).collect(Collectors.toList()); }
			catch(IOException e) { return Collections.emptyList(); }
		}
		
		@Override
		public Set<String> getNamespaces(PackType type)
		{
			if(type == PackType.SERVER_DATA) return Collections.emptySet();
			Set<String> set = new HashSet<>();
			set.add("minecraft");
			return set;
		}
		
		@Override
		public String getName()
		{
			return "Unique Enchantments nicer Enchanted Book";
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException
		{
			if(deserializer.getMetadataSectionName().equalsIgnoreCase("pack"))
			{
				return (T)new PackMetadataSection(Component.literal("UE ResourcePack that adds a nicer Enchanted book texture to the game"), 9);
			}
			return null;
		}
	}
}
