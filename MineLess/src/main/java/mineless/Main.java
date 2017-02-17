package mineless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandMap;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import mineless.commnads.addMineCommand;
import task.BlockPlaceTask;

public class Main extends PluginBase implements Listener {
	DataBase db = null;
	static List<Player> Minner = new ArrayList<>();
	static List<Player> Deller = new ArrayList<>();

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		db = new DataBase(this);
		this.getLogger().info(DataBase.message("플러그인이 정상적으로 실행되었습니다"));
		this.registers();
	}

	@Override
	public void onDisable() {
		db.save();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent event) {
		if (db.isMine(event.getBlock())) {

			Block block = db.mineing(event.getBlock());
			block.setLevel(event.getBlock().getLevel());
			block.position(event.getBlock());

			this.getServer().getScheduler().scheduleDelayedTask(
					new BlockPlaceTask(this, Block.get(block.getId(), block.getDamage(), block)), 5);
		}
	}

	@EventHandler
	public void addMine(PlayerInteractEvent event) {
		if (Main.Minner.contains(event.getPlayer())) {
			db.addMine(event.getBlock());
			event.getPlayer().sendMessage(db.message(db.toString((Position) event.getBlock()) + "위치에 광산이 만들어졌습니다"));
		}
	}

	@EventHandler
	public void addDeller(PlayerInteractEvent event) {
		if (Main.Deller.contains(event.getPlayer())) {
			db.delMine(event.getBlock());
			if (db.isMine(event.getBlock())) {
				event.getPlayer().sendMessage(db.alert(db.toString((Position) event.getBlock()) + "위치에 광산이 삭제되었습니다"));
			}
		}
	}

	public static boolean isMinner(CommandSender player) {
		if (player.isPlayer()) {
			if (Main.Minner.contains((Player) player))
				return true;
		}
		return false;
	}

	public static boolean isDeller(CommandSender player) {
		if (player.isPlayer()) {
			if (Main.Deller.contains((Player) player))
				return true;
		}
		return false;
	}

	public static void addMinner(CommandSender player) {
		if (player.isPlayer()) {
			Main.Minner.add((Player) player);
		}
		return;
	}

	public static void addDeller(CommandSender player) {
		if (player.isPlayer()) {
			Main.Deller.add((Player) player);
		}
		return;
	}

	public static void delDeller(CommandSender player) {
		if (player.isPlayer()) {
			Main.Deller.remove((Player) player);
		}
		return;
	}

	public static void delMinner(CommandSender player) {
		if (player.isPlayer()) {
			Main.Minner.remove(player);
		}
		return;
	}

	public void registers() {
		this.getServer().getCommandMap().register("광산", new addMineCommand(this));
	}
}
