package mineless;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

public class DataBase {
	private static Main plugin;

	private static DataBase instance = null;
	public static LinkedHashMap<String, Integer> mine = new LinkedHashMap<>();
	public static LinkedHashMap<String, Integer> nether = new LinkedHashMap<>();

	List<String> mines = new ArrayList<>();
	List<String> nethers = new ArrayList<>();

	public static DataBase getInstance() {
		return instance;
	}

	@SuppressWarnings({ "deprecation", "serial", "unchecked" })
	public DataBase(Main plugin) {
		plugin.getDataFolder().mkdirs();
		Config mine = new Config(plugin.getDataFolder() + "/mine.json", Config.JSON,
				new ConfigSection(new LinkedHashMap<String, Object>() {
					{
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						map1.put(Block.DIAMOND_ORE + "", "5");
						map1.put(Block.EMERALD_ORE + "", "5");
						map1.put(Block.GOLD_ORE + "", "50");
						map1.put(Block.IRON_ORE + "", "175");
						map1.put(Block.COAL_ORE + "", "375");
						put("MINE", map1);

						LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
						map2.put(Block.OBSIDIAN + "", "100");
						map2.put(Block.GLOWSTONE_BLOCK + "", "150");
						map2.put(Block.QUARTZ_ORE + "", "200");
						put("NETHER", map2);
					}

				}));
		Config positions = new Config(new File(plugin.getDataFolder(), "position.json"), Config.JSON,
				new ConfigSection(new LinkedHashMap<String, Object>() {

					{
						put("MINE", new ArrayList<String>());
						put("NETHER", new ArrayList<String>());
					}
				}));
		mine.save();
		positions.save();

		DataBase.mine = this.toMine((Map<String, Object>) mine.get("MINE"));
		DataBase.nether = this.toMine((Map<String, Object>) mine.get("NETHER"));

		mines = positions.getList("MINE");
		nethers = positions.getList("NETHER");
		DataBase.plugin = plugin;
	}

	public LinkedHashMap<String, Integer> getMine() {
		return mine;
	}

	public Block mineing(Position pos) {
		if (mines.contains(toString((Position) pos))) {
			int ran = new Random().nextInt(1000);
			for (String b : mine.keySet()) {
				if (ran < (int) mine.get(b)) {
					return toBlock(b);
				}
			}
			return Block.get(Block.COBBLESTONE);
		} else if (nethers.contains(toString(pos))) {
			int ran = new Random().nextInt(1000);
			for (String b : nether.keySet()) {
				if (ran < (int) nether.get(b)) {
					return toBlock(b);
				}
			}
			return Block.get(Block.NETHERRACK);
		}
		return Block.get(Block.GOLD_BLOCK);
	}

	public void addMine(Position pos) {
		mines.add(toString(pos));
		return;
	}

	public void addNetherMine(Position pos) {
		nethers.add(toString(pos));
		return;
	}

	public void delMine(Position pos) {
		mines.remove(toString((Position) pos));
		return;
	}

	public void removeMine(Block block) {
		mine.remove(toString(block));
	}

	public void putMine(Block block, int i) {
		mine.put(toString(block), i);
	}

	public LinkedHashMap<String, Integer> toMine(Map<String, Object> m) {
		LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
		ArrayList<String> slist = new ArrayList<>();
		slist.addAll(m.keySet());
		Collections.sort(slist, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				int i = toInteger((String) m.get(s1));
				int i2 = toInteger((String) m.get(s2));

				if (i - i2 > 0) {
					return 1;
				} else if (i - i2 == 0) {
					return 0;
				} else {
					return -1;
				}
			}
		});

		for (String s : slist) {
			map.put(s, toInteger((String) m.get(s)));
		}

		return map;
	}

	public int toInteger(String str) {
		int i = 0;
		try {
			i = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return i;
	}

	public String toString(Block block) {
		if (block.getDamage() == 0) {
			return block.getId() + "";
		}
		return block.getId() + ":" + block.getDamage();
	}

	public Block toBlock(String str) {
		if (str.contains(":")) {
			String[] strs = str.split(":");
			return Block.get(toInteger(strs[0]), toInteger(strs[1]));
		}
		return Block.get(toInteger(str));
	}

	public String toString(Item item) {
		StringBuilder str = new StringBuilder();
		str.append(item.getId());

		if (item.getDamage() != 0) {
			str.append(":" + item.getDamage());
		}
		if (item.getCount() == 1) {
			str.append(":" + item.getCount());
		}
		return str.toString();

	}

	public Item toItem(String str) {
		if (str.contains(":")) {
			String[] strs = str.split(":");

			switch (strs.length - 1) {
			case 1:
				return Item.get(toInteger(strs[0]), toInteger(strs[1]));
			case 2:
				return Item.get(toInteger(strs[0]), toInteger(strs[1]), toInteger(strs[2]));
			}
		}
		return Item.get(toInteger(str));

	}

	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public String toString(Position pos) {
		StringBuilder str = new StringBuilder();
		str.append(pos.getFloorX()).append(":").append(pos.getFloorY()).append(":").append(pos.getFloorZ()).append(":")
				.append(pos.getLevel().getFolderName());
		return str.toString();

	}

	public Position toPosition(String s) {
		String[] str = s.split(":");
		return new Position(Integer.parseInt(str[0]), Integer.parseInt(str[1]), Integer.parseInt(str[2]),
				Server.getInstance().getLevelByName(str[3]));
	}

	public static String message(String message) {
		return "§a§l[알림] §r§7" + message;
	}

	public static String alert(String message) {
		return "§c§l[알림] §r§7" + message;
	}

	public static String command(String message) {
		return "§l§6[알림]§r§7 " + message;
	}

	public String lengthString(int length, String string) {
		StringBuilder str = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			str.append(" ");
		}
		str.replace(0, string.length(), string);
		return str.toString();
	}

	public boolean isMine(Position pos) {
		String str = toString(pos);
		return mines.contains(str);
	}

	public void save() {
		LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
		for (String block : mine.keySet()) {
			map1.put(block, mine.get(block) + "");
		}
		Config config = new Config(new File(plugin.getDataFolder(), "mine.json"), Config.JSON);
		config.set("MINE", map1);
		LinkedHashMap<String, Object> map12 = new LinkedHashMap<>();
		for (String block1 : nether.keySet()) {
			map12.put(block1, nether.get(block1) + "");
		}
		config.set("NETHER", map12);
		config.save();
		Config config1 = new Config(new File(plugin.getDataFolder(), "position.json"), Config.JSON);
		config1.set("MINE", mines);
		config1.set("NETHER", nethers);
		config1.save();
	}
}
