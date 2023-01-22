import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringJoiner;

public class Test {
	
	public static void main(String...args)
    {
        Path path = Paths.get("C:\\Users\\Xaiki\\eclipse2\\UniqueEnchantments\\src\\main\\resources\\assets\\uniquebase\\textures\\entity\\banner");
//        Path out = Paths.get("C:\\Users\\Xaiki\\eclipse2\\UniqueEnchantments\\src\\main\\resources\\assets\\uniquebase\\models\\item");
//        Path outtag = Paths.get("C:\\Users\\Xaiki\\eclipse2\\UniqueEnchantments\\src\\main\\resources\\data\\uniquebase\\tags\\banner_pattern\\pattern_item");
        
        try
        {
            StringJoiner joiner = new StringJoiner("\n");
            for(Path entry : Files.walk(path).filter(T -> !T.getFileName().toString().contains("color")).toList())
            {
                String name = entry.getFileName().toString().replace(".png", "");
//                String name1 = name.concat("_banner_pattern.json");
//                String name2 = name.concat("_color_banner_pattern.json");
//                String model = ("{\r\n"
//                		+ "  \"parent\": \"minecraft:item/generated\",\r\n"
//                		+ "  \"textures\": {\r\n"
//                		+ "    \"layer0\": \"uniquebase:item/unique_banner_pattern\"\r\n"
//                		+ "  }\r\n"
//                		+ "}");
//                
//                String name3 = name.concat(".json");
//                String name4 = name.concat("_color.json");
//                String tag1 = "{\r\n"
//                		+ "    \"values\": [\r\n"
//                		+ "      \"uniquebase:"+name+"\"\r\n"
//                		+ "    ]\r\n"
//                		+ "}";
//                String tag2 = "{\r\n"
//                		+ "    \"values\": [\r\n"
//                		+ "      \"uniquebase:"+name+"_color"+"\"\r\n"
//                		+ "    ]\r\n"
//                		+ "}";
//                
//                File file1 = new File(out.toString()+"\\"+name1);
//                FileWriter writer1 = new FileWriter(file1);
//                writer1.write(model);
//                writer1.close();
//                
//                File file2 = new File(out.toString()+"\\"+name2);
//                FileWriter writer2 = new FileWriter(file2);
//                writer2.write(model);
//                writer2.close();
//                
//                File file3 = new File(outtag.toString()+"\\"+name3);
//                FileWriter writer3 = new FileWriter(file3);
//                writer3.write(tag1);
//                writer3.close();
//                
//                File file4 = new File(outtag.toString()+"\\"+name4);
//                FileWriter writer4 = new FileWriter(file4);
//                writer4.write(tag2);
//                writer4.close();
                
                joiner.add("registerPattern(\"uniquebase\", \""+name+"\", \"mc"+name.replaceAll("[aeiou_]", "")+"\", Rarity.RARE);");
                joiner.add("registerPattern(\"uniquebase\", \""+name+"_color"+"\", \"mc"+name.replaceAll("[aeiou_]", "")+"clr"+"\", Rarity.EPIC);");
                joiner.add("");
            }
            
            System.out.println(joiner);
            System.out.println("");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
}
