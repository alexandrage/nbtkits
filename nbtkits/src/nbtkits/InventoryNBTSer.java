package nbtkits;


import net.minecraft.server.v1_7_R4.NBTCompressedStreamTools;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.io.Files;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class InventoryNBTSer {	
	
	@SuppressWarnings("deprecation")
	public static void getKit(Player p, File folder, String name, boolean b) throws IOException {
		if(!new File(folder+"/kits/"+name.toLowerCase()+".kit").exists()) {
			p.sendMessage("§4Такого набора нет!");
			p.playSound(p.getLocation(), "random.explode", 1.0F, 2.0F);
			return;
		}
    	
		byte[] bytes = Files.toByteArray(new File(folder+"/kits/"+name.toLowerCase()+".kit"));
		String[] full = new String(bytes).split("-");
		String time = full[1];
		String Inv  = full[0];
		
		if(!b) {
			if(new File(folder+"/players/"+name.toLowerCase()+"-"+p.getName()).exists()) {
				byte[] bytesp = Files.toByteArray(new File(folder+"/players/"+name.toLowerCase()+"-"+p.getName()));
				String tpl = new String(bytesp);
				long  calc = (System.currentTimeMillis()-(Long.parseLong(tpl)))/1000;
				Time t = new Time(Long.parseLong((time))-calc);
				if(calc<Long.parseLong((time))) {
					p.sendMessage("§4Вы не можете получить этот набор, раньше чем через §c"+t.getFormat());
					p.playSound(p.getLocation(), "random.break", 1.0F, 0.0F);
					return;
				}
			}
		}
	
		ItemStack[] st = fromString(Inv).getContents();
		for(ItemStack s : st) {
			if(s != null) {
				HashMap<Integer, ItemStack> over = p.getInventory().addItem(s);
				for(Entry<Integer, ItemStack> entry : over.entrySet()) {   
				    p.getWorld().dropItemNaturally(p.getLocation(), entry.getValue());
				}
			}
		}
		p.sendMessage("§6Получен набор §c"+name.toLowerCase());
		p.playSound(p.getLocation(), "random.anvil_land", 1.0F, 0.0F);
		new File(folder+"/players/").mkdirs();
		Files.write(Long.toString(System.currentTimeMillis()).getBytes(), new File(folder+"/players/"+name.toLowerCase()+"-"+p.getName()));
	}
	
	@SuppressWarnings("deprecation")
	public static void setKit(Player p, File folder, String name, long time) throws IOException {
		new File(folder+"/kits").mkdirs(); 
		String Inv = toString(p.getInventory().getContents());
		String full = Inv+"-"+time;
		byte[] bytes = full.getBytes();
		Files.write(bytes, new File(folder+"/kits/"+name.toLowerCase()+".kit"));
		p.sendMessage("§2Набор сохранен.");
		p.playSound(p.getLocation(), "random.anvil_break", 1.0F, 1.0F);
	}
	
	@SuppressWarnings("deprecation")
	public static void setTime(Player p, File folder, String name, long time) throws IOException {
		if(!new File(folder+"/kits/"+name.toLowerCase()+".kit").exists()) {
			p.sendMessage("§4Такого набора нет!");
			p.playSound(p.getLocation(), "random.explode", 1.0F, 2.0F);
			return;
		}
		byte[] bytes = Files.toByteArray(new File(folder+"/kits/"+name.toLowerCase()+".kit"));
		String[] full = new String(bytes).split("-");
		String Inv  = full[0];
		String full2 = Inv+"-"+time;
		byte[] bytes2 = full2.getBytes();
		Files.write(bytes2, new File(folder+"/kits/"+name.toLowerCase()+".kit"));
		p.sendMessage("§2Время изменено.");
		p.playSound(p.getLocation(), "random.anvil_break", 1.0F, 1.0F);
	}
	
    private static String toString(ItemStack[] inventory) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < inventory.length; i++) {
            NBTTagCompound outputObject = new NBTTagCompound();
            boolean b = false;
            if(inventory[i] != null) {
            	b = inventory[i].getType() == Material.AIR;
            }
            if(b) {
            	inventory[i] = null;
            }
            CraftItemStack craft = getCraftVersion(inventory[i]);
            if (craft != null)
                CraftItemStack.asNMSCopy(craft).save(outputObject);

            itemList.add(outputObject);
        }

        NBTTagCompound whole = new NBTTagCompound();
        whole.set("Inventory", itemList);
        NBTCompressedStreamTools.a(whole, outputStream);

        return new BigInteger(1, outputStream.toByteArray()).toString(32);
    }

    private static Inventory fromString(String data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
        NBTTagList itemList = NBTCompressedStreamTools.a(inputStream).getList("Inventory", 10);
        Inventory inventory = new CraftInventoryCustom(null, itemList.size());

        for (int i = 0; i < itemList.size(); i++) {
            NBTTagCompound inputObject = itemList.get(i);
            if (!inputObject.isEmpty()) {
                inventory.setItem(i, CraftItemStack.asBukkitCopy(net.minecraft.server.v1_7_R4.ItemStack.createStack(inputObject)));
            }
        }

        return inventory;
    }

    private static CraftItemStack getCraftVersion(ItemStack stack) {
        if (stack instanceof CraftItemStack)
            return (CraftItemStack) stack;
        else if (stack != null)
            return CraftItemStack.asCraftCopy(stack);
        else
            return null;
    }

	@SuppressWarnings("deprecation")
	public static void delKit(File folder, String name, Player p) {
		File k = new File(folder+"/kits/"+name.toLowerCase()+".kit");
		if(k.exists()) {
			k.delete();
			p.sendMessage("§2Набор удален.");
			p.playSound(p.getLocation(), "random.anvil_break", 1.0F, 1.0F);
		} else {
			p.sendMessage("§4Такого набора нет!");
			p.playSound(p.getLocation(), "random.explode", 1.0F, 0.0F);
		}
	}
	
	  public static List<String> getLogs(File folder) {
		  List<String> l = new ArrayList<String>();
		  File Logs = new File(folder+"/kits"); 
		  Logs.mkdirs();
	  	  for (File file : Logs.listFiles()) {
	  		  String kit = file.toString().substring(file.toString().lastIndexOf(File.separator)+1);
	  		  if(kit.contains(".kit")) {
	  			  l.add(kit.replace(".kit", ""));
	  		  }
	  	  }
		return l;
	  }
	  
		public static void setInv(Player p, File folder) throws IOException {
			File folderinv = new File(folder+"/saveinv"); 
			folderinv.mkdirs();
			String Arm = toString(p.getInventory().getArmorContents());
			String Inv = toString(p.getInventory().getContents());
			String full = Arm+"-"+Inv;
			byte[] bytes = full.getBytes();
			Files.write(bytes, new File(folderinv+"/"+p.getName()));
	        p.getInventory().clear();
			ItemStack[] in = p.getInventory().getArmorContents();
	        for (int i = 0; i < in.length; i++) {
	            in[i] = new ItemStack(Material.AIR, 0);
	        }
	        p.getInventory().setArmorContents(in);  
		}
		
		public static void getInv(Player p, File folder) throws IOException {
			File folderinv = new File(folder+"/saveinv"); 
			folderinv.mkdirs();
			if(!new File(folderinv+"/"+p.getName()).exists()) {
				return;
			}
			byte[] bytes = Files.toByteArray(new File(folderinv+"/"+p.getName()));
			String full = new String(bytes);
			String[] spl = full.split("-");
			new File(folderinv+"/"+p.getName()).delete();
			p.getInventory().setArmorContents(fromString(spl[0]).getContents());
			p.getInventory().setContents(fromString(spl[1]).getContents());
		}  
}