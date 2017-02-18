package mineless.commnads;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.permission.Permission;
import mineless.DataBase;
import mineless.Main;

public class addMineCommand extends Command {
	Main plugin = null;

	public addMineCommand(Main plugin) {
		super("광산", "서버내 광산을 관리합니다", DataBase.command("/광산 <추가모드|제거모드|일반모드|확률조정>"));
		this.plugin = plugin;

		LinkedHashMap<String, CommandParameter[]> parameter = new LinkedHashMap<>();
		// parameter.put("광산", new CommandParameter[] { new
		// CommandParameter("<추가모드|제거모드|일반모드|확류설정>", true) });
		parameter.put("추가모드", new CommandParameter[] {
				new CommandParameter("<추가모드|일반모드|제거모드>", CommandParameter.ARG_TYPE_STRING_ENUM, true) });
		parameter.put("목록",
				new CommandParameter[] { new CommandParameter("확률조정", true), new CommandParameter("목록", true) });
		parameter.put("확률조정",
				new CommandParameter[] { new CommandParameter("확률조정", false), new CommandParameter("조정", false),
						new CommandParameter("확률(최대 1000)", CommandParameter.ARG_TYPE_INT, true) });
		// parameter.put("확률설정", optaion);
		// parameter.put("광산", parameters);
		this.setCommandParameters(parameter);
		this.setPermission(Permission.DEFAULT_OP);

	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(this.getUsage());
			return true;

		}
		if (sender.isOp()) {
			switch (args[0]) {
			case "추가모드":
				if (Main.isMinner(sender)) {
					sender.sendMessage(DataBase.command("당신은 이미 광산 추가모드 상태입니다"));
					return true;
				}
				if (Main.isDeller(sender)) {
					sender.sendMessage(DataBase.command("당신은 광산 삭제모드입니다 일반모드로 변경 후 다시 시도해주세요"));
					return true;
				}
				sender.sendMessage(DataBase.command("\"광산추가\"모드가 되었습니다 광산을 만들고 싶은 장소를 터치하여 주세요"));
				Main.addMinner(sender);
				return true;

			case "제거모드":
				if (Main.isDeller(sender)) {
					sender.sendMessage(DataBase.command("당신은 이미 광산 삭제모드 상태입니다"));
					return true;
				}
				if (Main.isMinner(sender)) {
					sender.sendMessage(DataBase.command("당신은 광산 추가모드입니다 일반모드로 변경 후 다시 시도해주세요"));
					return true;
				}
				sender.sendMessage(DataBase.command("\"광산제거\"모드가 되었습니다 광산을 제거하고 싶은 장소를 터치하여 주세요"));
				Main.addDeller(sender);
				return true;
			case "일반모드":
				if (Main.isDeller(sender)) {
					Main.delDeller(sender);
				}
				if (Main.isMinner(sender)) {
					Main.delMinner(sender);
				}
				sender.sendMessage(DataBase.command("정상적으로 처리되었습니다"));
				return true;
			case "확률조정":
				if (args.length <= 1) {
					sender.sendMessage(getUsage());
					return true;
				}
				if (args[1].equals("조정")) {
					if (args.length <= 2) {
						sender.sendMessage(getUsage());
						return true;
					}
					if (!((Player) sender).getInventory().getItemInHand().canBePlaced()) {
						sender.sendMessage(DataBase.alert("해당 아이템은 설치 할 수 없습니다"));
						return true;
					}
					if (!DataBase.isInt(args[2])) {
						sender.sendMessage(DataBase.alert("숫자를 입력하여 주세요"));
						return true;
					}

					int pers = DataBase.getInstance().toInteger(args[2]);
					Block block = ((Player) sender).getInventory().getItemInHand().getBlock();

					if (pers > 1000) {
						sender.sendMessage(DataBase.alert("확률이 1000을 넘을 순 없습니다"));
						return true;
					}

					if (pers == 0) {
						if (DataBase.getInstance().getMine().keySet()
								.contains(DataBase.getInstance().toString(block))) {
							DataBase.getInstance().removeMine(block);
							sender.sendMessage(DataBase.message("광산이 정상적으로 제거되었습니다"));
							return true;
						}

					}

					else if (DataBase.getInstance().getMine().keySet()
							.contains(DataBase.getInstance().toString(block))) {

						sender.sendMessage(DataBase.message("정상적으로 확률이 변경되었습니다"));
						sender.sendMessage(DataBase.success("[§b+§7] §f" + block.getName() + "§7:§2"
								+ DataBase.getInstance().mine.get(DataBase.getInstance().toString(block)) + " §7->§l§c"
								+ pers));
						DataBase.getInstance().mine.replace(DataBase.getInstance().toString(block), pers);
						LinkedHashMap<String, Integer> map = DataBase.getInstance()
								.toMines(DataBase.getInstance().mine);
						DataBase.getInstance().mine = map;
						return true;
					}
					DataBase.getInstance().mine.put(DataBase.getInstance().toString(block), pers);
					LinkedHashMap<String, Integer> map = DataBase.getInstance().toMines(DataBase.getInstance().mine);
					DataBase.getInstance().mine = map;
					sender.sendMessage(DataBase.message("정상적으로 광산품목이 추가되었습니다"));
					sender.sendMessage(DataBase.success("[§b+§7] §f" + block.getName() + ":" + pers));
					return true;

				} else if (args[1].equals("목록")) {
					this.getList((Player) sender);
					return true;
				}

				sender.sendMessage(getUsage());
				return true;

			default:
				sender.sendMessage(this.getUsage());
				return true;
			}

		}
		sender.sendMessage(this.getUsage());
		return true;
	}

	private void getList(Player player) {
		StringBuilder str = new StringBuilder();
		for (String s : DataBase.getInstance().mine.keySet()) {
			str.append(DataBase.message(
					DataBase.getInstance().lengthString(25, s) + "(" + DataBase.getInstance().toBlock(s).getName()
							+ ") : " + DataBase.getInstance().mine.get(s) + "\n"));
		}
		player.sendMessage(str.toString());
	}
}
