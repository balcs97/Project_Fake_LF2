package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ClientCenter implements Runnable {
	private int state;
	private int Tid, myTid;
	private String function;
	private int source;
	private int dest;
	private int type;
	private int role, role_data[] = new int[4];
	private double X, Y, position[][] = new double[4][2];
	private int direction;
	private String Stype, myName;
	private Socket sock;
	private BufferedReader reader;
	private PrintStream writer;
	private String ta[];
	private Stage primaryStage;
	private Client client;
	private Stage stage;
	private boolean room_boolean[] = new boolean[4];
	private int room_Tid_data[] = new int[4];
	private int myTid_int = 99, dest_int = 99;

	public ClientCenter(Client client, Socket socket, String ip, String name) {
		try {
			for (int i = 0; i < 4; i++) {
				room_boolean[i] = false;
			}
			EstablishConnection(ip, 8888);
			this.client = client;
			myName = name;
		} catch (Exception ex) {
			System.out.println("連接失敗 in ClientCenter");
		}
	}

	private void EstablishConnection(String ip, int port) {
		try {
			// 請求建立連線
			sock = new Socket(ip, port);
			// 建立I/O資料流
			InputStreamReader streamReader =
					// 取得Socket的輸入資料流
					new InputStreamReader(sock.getInputStream());
			// 放入暫存區
			reader = new BufferedReader(streamReader);
			// 取得Socket的輸出資料流

			writer = new PrintStream(sock.getOutputStream());
			// 連線成功
			System.out.println("網路建立-連線成功");

		} catch (IOException ex) {
			System.out.println("建立連線失敗");
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String message;
		try {
			while ((message = reader.readLine()) != null) {
				System.out.println("收到" + message);
				if (message.contains("#")) {
					// 這是因為暫時測試用的的client會把名字:打在前面
					decoder(message);
					handle();
					if (Tid == -1) {
						writer.println(("0#" + myTid + "#connect#-1#-1#-1#-1.0#-1.0#0#" + myName));
						writer.flush();
					}
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Event Handler

	// region

	// selectRole -> select_role_method -> Write to Server
	// selectedRole -> SetImage
	// lockRole -> lock_role_method -> Write to Server

	public void handle() {

		if (state == 0) {
			// state 0
			switch (function) {
			case "connect":
				for (int i = 0; i < 4; i++) {
					if (room_boolean[i] == false) {
						switch (i) {
						// 觸發Tid
						// TODO: 依照Tid設定房間內的角色.名字排序
						case 1:
							Platform.runLater(() -> {
								try {
									client.label_room_name1.setText(myName);
								} catch (Exception ex) {

								}
							});
							break;
						case 2:
							Platform.runLater(() -> {
								try {
									client.label_room_name2.setText(myName);
								} catch (Exception ex) {

								}
							});
							break;
						case 3:
							Platform.runLater(() -> {
								try {
									client.label_room_name3.setText(myName);
								} catch (Exception ex) {

								}
							});
							break;
						case 4:
							Platform.runLater(() -> {
								try {
									client.label_room_name4.setText(myName);
								} catch (Exception ex) {

								}
							});
							break;
						}
						room_boolean[i] = true;
						room_Tid_data[i] = myTid;
						break;
					}
				}
				break;
			case "connected":
				for (int i = 0; i < 4; i++) {
					if (room_boolean[i] == false) {
						switch (i) {
						// 觸發Tid
						// TODO:
						case 1:
							Platform.runLater(() -> {
								try {
									client.label_room_name1.setText(Stype);
								} catch (Exception ex) {

								}
							});
							break;
						case 2:
							Platform.runLater(() -> {
								try {
									client.label_room_name2.setText(Stype);
								} catch (Exception ex) {

								}
							});
							break;
						case 3:
							Platform.runLater(() -> {
								try {
									client.label_room_name3.setText(Stype);
								} catch (Exception ex) {

								}
							});
							break;
						case 4:
							Platform.runLater(() -> {
								try {
									client.label_room_name4.setText(Stype);
								} catch (Exception ex) {

								}
							});
							break;
						}
						room_boolean[i] = true;
						room_Tid_data[i] = type;
						break;
					}
				}
				break;
			case "disconnected":
				switch (type) {
				// 觸發Tid
				// TODO: 依照Tid顯示誰斷線
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				}
				break;
			case "choosed":
				for (int i = 0; i < 4; i++) {
					if (room_Tid_data[i] == dest) {
						dest_int = i;
					}
				}
				switch (dest_int) {
				case 1:
					switch (type) {
					case 1:
						selectedRole("role_1.png", client.label_room_headpicture1);
						break;
					case 2:
						selectedRole("role_2.png", client.label_room_headpicture1);
						break;
					case 3:
						selectedRole("role_3.png", client.label_room_headpicture1);
						break;
					case 4:
						selectedRole("role_4.png", client.label_room_headpicture1);
						break;
					case 5:
						selectedRole("role_5.png", client.label_room_headpicture1);
						break;
					}
					break;
				case 2:
					switch (type) {
					case 1:
						selectedRole("role_1.png", client.label_room_headpicture2);
						break;
					case 2:
						selectedRole("role_2.png", client.label_room_headpicture2);
						break;
					case 3:
						selectedRole("role_3.png", client.label_room_headpicture2);
						break;
					case 4:
						selectedRole("role_4.png", client.label_room_headpicture2);
						break;
					case 5:
						selectedRole("role_5.png", client.label_room_headpicture2);
						break;
					}
					break;
				case 3:
					switch (type) {
					case 1:
						selectedRole("role_1.png", client.label_room_headpicture3);
						break;
					case 2:
						selectedRole("role_2.png", client.label_room_headpicture3);
						break;
					case 3:
						selectedRole("role_3.png", client.label_room_headpicture3);
						break;
					case 4:
						selectedRole("role_4.png", client.label_room_headpicture3);
						break;
					case 5:
						selectedRole("role_5.png", client.label_room_headpicture3);
						break;
					}
					break;
				case 4:
					switch (type) {
					case 1:
						selectedRole("role_1.png", client.label_room_headpicture4);
						break;
					case 2:
						selectedRole("role_2.png", client.label_room_headpicture4);
						break;
					case 3:
						selectedRole("role_3.png", client.label_room_headpicture4);
						break;
					case 4:
						selectedRole("role_4.png", client.label_room_headpicture4);
						break;
					case 5:
						selectedRole("role_5.png", client.label_room_headpicture4);
						break;
					}
					break;
				}
				break;
			case "locked":
				for (int i = 0; i < 4; i++) {
					if (room_Tid_data[i] == dest) {
						dest_int = i;
					}
				}
				role_data[dest - 1] = type;
				switch (dest_int) {
				case 1:
					switch (type) {
					// 觸發Tid
					// TODO: 依照Tid設定誰鎖定角色
					case 1:
						lockedRole("lock_role_1.png", client.label_room_headpicture1);
						break;
					case 2:
						lockedRole("lock_role_2.png", client.label_room_headpicture1);
						break;
					case 3:
						lockedRole("lock_role_3.png", client.label_room_headpicture1);
						break;
					case 4:
						lockedRole("lock_role_4.png", client.label_room_headpicture1);
						break;
					case 5:
						lockedRole("lock_role_5.png", client.label_room_headpicture1);
						break;
					}
					break;
				case 2:
					switch (type) {
					// 觸發Tid
					// TODO: 依照Tid設定誰鎖定角色
					case 1:
						lockedRole("lock_role_1.png", client.label_room_headpicture2);
						break;
					case 2:
						lockedRole("lock_role_2.png", client.label_room_headpicture2);
						break;
					case 3:
						lockedRole("lock_role_3.png", client.label_room_headpicture2);
						break;
					case 4:
						lockedRole("lock_role_4.png", client.label_room_headpicture2);
						break;
					case 5:
						lockedRole("lock_role_5.png", client.label_room_headpicture2);
						break;
					}
					break;
				case 3:
					switch (type) {
					// 觸發Tid
					// TODO: 依照Tid設定誰鎖定角色
					case 1:
						lockedRole("lock_role_1.png", client.label_room_headpicture3);
						break;
					case 2:
						lockedRole("lock_role_2.png", client.label_room_headpicture3);
						break;
					case 3:
						lockedRole("lock_role_3.png", client.label_room_headpicture3);
						break;
					case 4:
						lockedRole("lock_role_4.png", client.label_room_headpicture3);
						break;
					case 5:
						lockedRole("lock_role_5.png", client.label_room_headpicture3);
						break;
					}
					break;
				case 4:
					switch (type) {
					// 觸發Tid
					// TODO: 依照Tid設定誰鎖定角色
					case 1:
						lockedRole("lock_role_1.png", client.label_room_headpicture4);
						break;
					case 2:
						lockedRole("lock_role_2.png", client.label_room_headpicture4);
						break;
					case 3:
						lockedRole("lock_role_3.png", client.label_room_headpicture4);
						break;
					case 4:
						lockedRole("lock_role_4.png", client.label_room_headpicture4);
						break;
					case 5:
						lockedRole("lock_role_5.png", client.label_room_headpicture4);
						break;
					}
					break;
				}

			case "go1":
				// 鎖定畫面
				Platform.runLater(() -> {
					try {
						client.button_room_ready.setDisable(true);
					} catch (Exception ex) {
					}
				});
				break;

			case "go2":
				client.toGame(stage);
				break;

			case "initial":
				switch (dest) {
				// 觸發Tid
				// TODO: 依照Tid設定誰鎖定角色
				case 1:
					position[0][0] = X;
					position[0][1] = Y;
					break;
				case 2:
					position[1][0] = X;
					position[1][1] = Y;
					break;
				case 3:
					position[2][0] = X;
					position[2][1] = Y;
					break;
				case 4:
					position[3][0] = X;
					position[3][1] = Y;
					break;
				}
				break;
			}

		} else if (state == 1)

		{
			switch (function) {
			case "atk2":
				switch (type) {
				// 觸發Tid
				// TODO: 依照Tid設定誰發動攻擊
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				}
				break;
			case "atked":
				switch (type) {
				// 觸發Tid
				// TODO: 依照Tid設定誰被攻擊
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				}
				break;
			case "death":
				switch (type) {
				// 觸發Tid
				// TODO: 依照Tid設定誰死亡
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				}
				break;
			case "movedup":
				switch (type) {
				// 觸發Tid
				// TODO: 依照Tid設定誰往上
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				}
				break;
			case "moveddown":
				switch (type) {
				// 觸發Tid
				// TODO: 依照Tid設定誰往下
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				}
				break;
			case "movedleft":
				switch (type) {
				// 觸發Tid
				// TODO: 依照Tid設定誰往左
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				}
				break;
			case "movedright":
				switch (type) {
				// 觸發Tid
				// TODO: 依照Tid設定誰往右
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				}
				break;
			}
		} else if (state == 2) {
			switch (function) {
			case "win":
				switch (type) {
				// 觸發Tid
				// TODO:
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				}
				break;
			case "back1":
				switch (type) {
				// 觸發Tid
				// TODO:
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				}
				break;
			case "back2":
				switch (type) {
				// 觸發Tid
				// TODO:
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				}
				break;
			}
		}
	}

	public void selecteRole(int role) {
		Platform.runLater(() -> {
			try {
				for (int i = 0; i < 4; i++) {
					if (room_Tid_data[i] == myTid) {
						myTid_int = i;
					}
				}
				switch (myTid_int) {
				case 1:
					switch (role) {
					case 1:
						select_role_method(client.image_room_player_1, "role_1.png", client.label_room_headpicture1);
						this.role = role;
						break;
					case 2:
						select_role_method(client.image_room_player_1, "role_2.png", client.label_room_headpicture1);
						this.role = role;
						break;
					case 3:
						select_role_method(client.image_room_player_1, "role_3.png", client.label_room_headpicture1);
						this.role = role;
						break;
					case 4:
						select_role_method(client.image_room_player_1, "role_4.png", client.label_room_headpicture1);
						this.role = role;
						break;
					case 5:
						select_role_method(client.image_room_player_1, "role_5.png", client.label_room_headpicture1);
						this.role = role;
						break;
					}
					break;
				case 2:
					switch (role) {
					case 1:
						select_role_method(client.image_room_player_2, "role_1.png", client.label_room_headpicture2);
						this.role = role;
						break;
					case 2:
						select_role_method(client.image_room_player_2, "role_2.png", client.label_room_headpicture2);
						this.role = role;
						break;
					case 3:
						select_role_method(client.image_room_player_2, "role_3.png", client.label_room_headpicture2);
						this.role = role;
						break;
					case 4:
						select_role_method(client.image_room_player_2, "role_4.png", client.label_room_headpicture2);
						this.role = role;
						break;
					case 5:
						select_role_method(client.image_room_player_2, "role_5.png", client.label_room_headpicture2);
						this.role = role;
						break;
					}
					break;
				case 3:
					switch (role) {
					case 1:
						select_role_method(client.image_room_player_3, "role_1.png", client.label_room_headpicture3);
						this.role = role;
						break;
					case 2:
						select_role_method(client.image_room_player_3, "role_2.png", client.label_room_headpicture3);
						this.role = role;
						break;
					case 3:
						select_role_method(client.image_room_player_3, "role_3.png", client.label_room_headpicture3);
						this.role = role;
						break;
					case 4:
						select_role_method(client.image_room_player_3, "role_4.png", client.label_room_headpicture3);
						this.role = role;
						break;
					case 5:
						select_role_method(client.image_room_player_3, "role_5.png", client.label_room_headpicture3);
						this.role = role;
						break;
					}
					break;
				case 4:
					switch (role) {
					case 1:
						select_role_method(client.image_room_player_4, "role_1.png", client.label_room_headpicture4);
						this.role = role;
						break;
					case 2:
						select_role_method(client.image_room_player_4, "role_2.png", client.label_room_headpicture4);
						this.role = role;
						break;
					case 3:
						select_role_method(client.image_room_player_4, "role_3.png", client.label_room_headpicture4);
						this.role = role;
						break;
					case 4:
						select_role_method(client.image_room_player_4, "role_4.png", client.label_room_headpicture4);
						this.role = role;
						break;
					case 5:
						select_role_method(client.image_room_player_4, "role_5.png", client.label_room_headpicture4);
						this.role = role;
						break;
					}
					break;
				}

			} catch (Exception ex) {
				System.out.println("送出資料失敗");
			}
		});
	}

	public void selectedRole(String png, Label label) {

		ImageView image = new ImageView(png);

		Platform.runLater(() -> {
			label.setGraphic(image);
		});
		System.out.println("selectedRole_finish");
	}

	public void lockRole(Stage primaryStage) {
		stage = primaryStage;
		Platform.runLater(() -> {
			try {
				role_data[myTid - 1] = role;
				switch (myTid) {
				case 1:
					switch (role) {
					case 1:
						lock_role_method(client.image_room_player_1, "lock_role_1.png", client.label_room_headpicture1);
						break;
					case 2:
						lock_role_method(client.image_room_player_1, "lock_role_2.png", client.label_room_headpicture1);
						break;
					case 3:
						lock_role_method(client.image_room_player_1, "lock_role_3.png", client.label_room_headpicture1);
						break;
					case 4:
						lock_role_method(client.image_room_player_1, "lock_role_4.png", client.label_room_headpicture1);
						break;
					case 5:
						lock_role_method(client.image_room_player_1, "lock_role_5.png", client.label_room_headpicture1);
						break;
					}
					break;
				case 2:
					switch (role) {
					case 1:
						lock_role_method(client.image_room_player_2, "lock_role_1.png", client.label_room_headpicture2);
						break;
					case 2:
						lock_role_method(client.image_room_player_2, "lock_role_2.png", client.label_room_headpicture2);
						break;
					case 3:
						lock_role_method(client.image_room_player_2, "lock_role_3.png", client.label_room_headpicture2);
						break;
					case 4:
						lock_role_method(client.image_room_player_2, "lock_role_4.png", client.label_room_headpicture2);
						break;
					case 5:
						lock_role_method(client.image_room_player_2, "lock_role_5.png", client.label_room_headpicture2);
						break;
					}
					break;
				case 3:
					switch (role) {
					case 1:
						lock_role_method(client.image_room_player_3, "lock_role_1.png", client.label_room_headpicture3);
						break;
					case 2:
						lock_role_method(client.image_room_player_3, "lock_role_2.png", client.label_room_headpicture3);
						break;
					case 3:
						lock_role_method(client.image_room_player_3, "lock_role_3.png", client.label_room_headpicture3);
						break;
					case 4:
						lock_role_method(client.image_room_player_3, "lock_role_4.png", client.label_room_headpicture3);
						break;
					case 5:
						lock_role_method(client.image_room_player_3, "lock_role_5.png", client.label_room_headpicture3);
						break;
					}
					break;
				case 4:
					switch (role) {
					case 1:
						lock_role_method(client.image_room_player_4, "lock_role_1.png", client.label_room_headpicture4);
						break;
					case 2:
						lock_role_method(client.image_room_player_4, "lock_role_2.png", client.label_room_headpicture4);
						break;
					case 3:
						lock_role_method(client.image_room_player_4, "lock_role_3.png", client.label_room_headpicture4);
						break;
					case 4:
						lock_role_method(client.image_room_player_4, "lock_role_4.png", client.label_room_headpicture4);
						break;
					case 5:
						lock_role_method(client.image_room_player_4, "lock_role_5.png", client.label_room_headpicture4);
						break;
					}
					break;
				}
			} catch (Exception e) {

			}
		});
	}

	public void lockedRole(String png, Label label) {

		ImageView image = new ImageView(png);

		Platform.runLater(() -> {
			label.setGraphic(image);
		});
		System.out.println("LockRole_finish");
	}

	// endregion

	// method

	// region
	public void select_role_method(ImageView image, String png, Label label) {
		image = new ImageView(png);
		label.setGraphic(image);
		initSelect();
		writer.println(encoder());
		writer.flush();
		refreshInst();
		System.out.println("SelecteRole_finish");
	}

	public void lock_role_method(ImageView image, String png, Label label) {
		image = new ImageView(png);
		label.setGraphic(image);
		initLock();
		writer.println(encoder());
		writer.flush();
		refreshInst();
		System.out.println("Lock_finish");

	}

	public void setGameView() {
		String c1_png = "", c2_png = "", c3_png = "", c4_png = "";
		switch (role_data[0]) {
		case 1:
			c1_png = "img_game_role1.png";
			break;
		case 2:
			c1_png = "img_game_role2.png";
			break;
		case 3:
			c1_png = "img_game_role3.png";
			break;
		case 4:
			c1_png = "img_game_role4.png";
			break;
		default:
			c1_png = "img_game_role1.png";
			break;
		}
		switch (role_data[1]) {
		case 1:
			c2_png = "img_game_role1.png";
			break;
		case 2:
			c2_png = "img_game_role2.png";
			break;
		case 3:
			c2_png = "img_game_role3.png";
			break;
		case 4:
			c2_png = "img_game_role4.png";
			break;
		default:
			c2_png = "img_game_role1.png";
			break;
		}
		switch (role_data[2]) {
		case 1:
			c3_png = "img_game_role1.png";
			break;
		case 2:
			c3_png = "img_game_role2.png";
			break;
		case 3:
			c3_png = "img_game_role3.png";
			break;
		case 4:
			c3_png = "img_game_role4.png";
			break;
		default:
			c3_png = "img_game_role1.png";
			break;
		}
		switch (role_data[3]) {
		case 1:
			c4_png = "img_game_role1.png";
			break;
		case 2:
			c4_png = "img_game_role2.png";
			break;
		case 3:
			c4_png = "img_game_role3.png";
			break;
		case 4:
			c4_png = "img_game_role4.png";
			break;
		default:
			c4_png = "img_game_role1.png";
			break;
		}
		client.game_setupUI(position[0][0], position[0][1], position[1][0], position[1][1], position[2][0],
				position[2][1], position[3][0], position[3][1], c1_png, c2_png, c3_png, c4_png);
	}
	// endregion

	// coder

	// region
	public String encoder() {
		String ta[] = new String[10];
		ta[0] = Integer.toString(state);
		ta[1] = Integer.toString(myTid);
		ta[2] = function;
		ta[3] = Integer.toString(source);
		ta[4] = Integer.toString(dest);
		ta[5] = Integer.toString(type);
		ta[6] = Double.toString(X);
		ta[7] = Double.toString(Y);
		ta[8] = Integer.toString(direction);
		ta[9] = Stype;
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < ta.length; i++) {
			strBuilder.append(ta[i]);
			if (i != ta.length - 1) {
				strBuilder.append("#");
			}
		}
		String message = strBuilder.toString();
		return message;
	}

	public void decoder(String message) {
		// 把message用#分開
		ta = message.split("#");
		// System.out.print("解開訊息:");
		// for (int i = 0; i < ta.length; i++) {
		// System.out.print(ta[i] + "+");
		// }
		// System.out.println();
		// 把所有拆開的訊息依序填入thread中的參數
		state = Integer.parseInt(ta[0]);
		Tid = Integer.parseInt(ta[1]);
		function = ta[2];
		source = Integer.parseInt(ta[3]);
		dest = Integer.parseInt(ta[4]);
		type = Integer.parseInt(ta[5]);
		X = Double.parseDouble(ta[6]);
		Y = Double.parseDouble(ta[7]);
		direction = Integer.parseInt(ta[8]);
		Stype = ta[9];
		if (function.equals("connect"))
			myTid = Integer.parseInt(ta[5]);
		System.out.println("state: " + state);
		System.out.println("Tid: " + Tid);
		System.out.println("function: " + function);
		System.out.println("source: " + source);
		System.out.println("dest: " + dest);
		System.out.println("type: " + type);
		System.out.println("X: " + X);
		System.out.println("Y: " + Y);
		System.out.println("direction: " + direction);
		System.out.println("Stype: " + Stype);
		System.out.println("myTid: " + myTid);
	}
	// endregion

	// init

	// region
	public void initSelect() {
		state = 0;
		myTid = myTid;
		function = "choose";
		source = -1;
		dest = -1;
		type = role;
		X = -1.0;
		Y = -1.0;
		direction = 0;
		Stype = "@";
	}

	public void initLock() {
		state = 0;
		myTid = myTid;
		function = "lock";
		source = -1;
		dest = -1;
		type = role;
		X = -1.0;
		Y = -1.0;
		direction = 0;
		Stype = "@";
	}
	// endregion

	// refresh

	// region
	public void refreshInst() {

		state = -1;
		Tid = -1;
		function = "";
		source = -1;
		dest = -1;
		type = -1;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = "@";
	}
	// endregion
}
