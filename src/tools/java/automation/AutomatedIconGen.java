package automation;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class AutomatedIconGen
{
	public static void main(String...args) {
		Path langPath = Paths.get(args[0]);
		Path iconPath = Paths.get(args[1]);
		try(BufferedReader reader = Files.newBufferedReader(iconPath)) {
			List<LangFile> list = new ObjectArrayList<>();
			Map<String, LangFile> map = new Object2ObjectOpenHashMap<>();
			for(Iterator<Path> iter = Files.walk(langPath).iterator();iter.hasNext();) {
				Path path = iter.next();
				if(!isValidFile(path)) continue;
				LangFile file = LangFile.map(path);
				if(file == null) continue;
				list.add(file);
				file.fillMap(map);
			}
			JsonArray icons = new JsonParser().parse(reader).getAsJsonObject().getAsJsonArray("icons");
			for(int i = 0,m=icons.size();i<m;i++) {
				String s = icons.get(i).getAsString();
				if(s.isEmpty()) continue;
				LangFile file = map.get(s+".desc");
				if(file == null) {
					System.out.println("["+s+".desc] wasn't found in the language file, Skipping");
					continue;
				}
				file.addLangEntry(s+".icon", i+1);
			}
			for(LangFile file : list) {
				file.save();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isValidFile(Path path) {
		return path.getFileName().endsWith(".json") && path.getParent().getFileName().toString().equals("lang");
	}
	
	public static class LangFile
	{
		Path path;
		JsonObject obj;
		Set<String> keys;
		
		public LangFile(Path path, JsonObject obj)
		{
			this.path = path;
			this.obj = obj;
			keys = new ObjectAVLTreeSet<>();
			for(Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				keys.add(entry.getKey());
			}
		}
		
		public void fillMap(Map<String, LangFile> keyMap) {
			for(String s : keys) {
				keyMap.put(s, this);
			}
		}
		
		public void addLangEntry(String s, int character) {
			keys.add(s);
			obj.addProperty(s, String.valueOf((char)character));
		}
		
		public void save() {
			JsonObject newObject = new JsonObject();
			for(String s : keys) {
				newObject.add(s, obj.get(s));
			}
			try(JsonWriter writer = new JsonWriter(Files.newBufferedWriter(path))) {
				writer.setIndent("\t");
				Streams.write(newObject, writer);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		public static LangFile map(Path path) {
			try(BufferedReader reader = Files.newBufferedReader(path)) {
				return new LangFile(path, new JsonParser().parse(reader).getAsJsonObject());
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
