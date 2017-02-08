package nbtkits;

import net.minecraft.server.v1_7_R4.NBTCompressedStreamTools;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class InventoryNBTSer {

	public static void setInv(Player p, File folder){
		File folderinv = new File(folder+"/saveinv"); 
		folderinv.mkdirs();
		NBTTagList Arm = toNBTTagList(p.getInventory().getArmorContents());
		NBTTagList Inv = toNBTTagList(p.getInventory().getContents());
		NBTTagCompound NBT = new NBTTagCompound();
		NBT.set("Arm", Arm);
		NBT.set("Inv", Inv);
		try {
			NBTCompressedStreamTools.a(NBT, new FileOutputStream(folderinv+"/"+p.getName()));
			p.getInventory().clear();
			ItemStack[] in = p.getInventory().getArmorContents();
			for (int i = 0; i < in.length; i++) {
				in[i] = new ItemStack(Material.AIR, 0);
			}
			p.getInventory().setArmorContents(in);  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void getInv(Player p, File folder) {
		File folderinv = new File(folder+"/saveinv"); 
		folderinv.mkdirs();
		if(!new File(folderinv+"/"+p.getName()).exists()) {
			return;
		}
		NBTTagCompound NBT = fromNBTTagCompound(folderinv+"/"+p.getName());
		p.getInventory().setArmorContents(fromInventory(NBT.getList("Arm", 10)).getContents());
		p.getInventory().setContents(fromInventory(NBT.getList("Inv", 10)).getContents());
		new File(folderinv+"/"+p.getName()).delete();
	}

	public static void getKit(Player p, File folder, String name, boolean b) throws IOException {
		if(!new File(folder+"/kits/"+name.toLowerCase()+".kit").exists()) {
			p.sendMessage("§4Такого набора нет!");
			p.playSound(p.getLocation(), Sound.EXPLODE, 1, 2);
			return;
		}
		NBTTagCompound NBT = fromNBTTagCompound(folder+"/kits/"+name.toLowerCase()+".kit");
		long time = NBT.getLong("time");
		NBTTagList Inv  = NBT.getList("kit", 10);

		if(!b) {
			if(new File(folder+"/players/"+name.toLowerCase()+"-"+p.getName()).exists()) {
				NBTTagCompound temp = fromNBTTagCompound(folder+"/players/"+name.toLowerCase()+"-"+p.getName());
				long tpl = temp.getLong("time");
				long  calc = (System.currentTimeMillis()-tpl)/1000;
				Time t = new Time(time-calc);
				if(calc<time) {
					p.sendMessage("§4Вы не можете получить этот набор, раньше чем через §c"+t.getFormat());
					p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 1, 0);
					return;
				}
			}
		}

		ItemStack[] st = fromInventory(Inv).getContents();
		for(ItemStack s : st) {
			if(s != null) {
				HashMap<Integer, ItemStack> over = p.getInventory().addItem(s);
				for(Entry<Integer, ItemStack> entry : over.entrySet()) {   
				    p.getWorld().dropItemNaturally(p.getLocation(), entry.getValue());
				}
			}
		}
		p.sendMessage("§6Получен набор §c"+name.toLowerCase());
		p.playSound(p.getLocation(), Sound.ANVIL_LAND, 1, 0);
		new File(folder+"/players/").mkdirs();
		NBTTagCompound temp = new NBTTagCompound();
		temp.setLong("time", System.currentTimeMillis());
		NBTCompressedStreamTools.a(temp, new FileOutputStream(folder+"/players/"+name.toLowerCase()+"-"+p.getName()));
	}

	public static void setKit(Player p, File folder, String name, long time) {
		new File(folder+"/kits").mkdirs();
		NBTTagCompound NBT = new NBTTagCompound();
		NBTTagList kit = toNBTTagList(p.getInventory().getContents());
		NBT.set("kit", kit);
		NBT.setLong("time", time);
		try {
			NBTCompressedStreamTools.a(NBT, new FileOutputStream(folder+"/kits/"+name.toLowerCase()+".kit"));
			p.sendMessage("§2Набор сохранен.");
			p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 1, 1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void setTime(Player p, File folder, String name, long time) {
		if(!new File(folder+"/kits/"+name.toLowerCase()+".kit").exists()) {
			p.sendMessage("§4Такого набора нет!");
			p.playSound(p.getLocation(), Sound.EXPLODE, 1, 2);
			return;
		}
		NBTTagCompound NBT = fromNBTTagCompound(folder+"/kits/"+name.toLowerCase()+".kit");
		NBT.setLong("time", time);
		try {
			NBTCompressedStreamTools.a(NBT, new FileOutputStream(folder+"/kits/"+name.toLowerCase()+".kit"));
			p.sendMessage("§2Время изменено.");
			p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 1, 1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void delKit(File folder, String name, Player p) {
		File k = new File(folder+"/kits/"+name.toLowerCase()+".kit");
		if(k.exists()) {
			k.delete();
			p.sendMessage("§2Набор удален.");
			p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 1, 1);
		} else {
			p.sendMessage("§4Такого набора нет!");
			p.playSound(p.getLocation(), Sound.EXPLODE, 1, 0);
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

	private static NBTTagList toNBTTagList(ItemStack[] inventory) {
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
		return itemList;
	}

	public static NBTTagCompound fromNBTTagCompound(String p){
		try {
			return NBTCompressedStreamTools.a(new FileInputStream(p));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			NBTTagCompound nul = new NBTTagCompound();
			return nul;
		}
	}

	private static Inventory fromInventory(NBTTagList itemList) {
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
}