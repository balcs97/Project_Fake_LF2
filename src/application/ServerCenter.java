package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.Vector;

public class ServerCenter implements Runnable {
	// �Ȧs��ƪ�Buffered
	BufferedReader reader;
	// �إߤ@��Socket�ܼ�
	Socket sock;
	private int myTid = -1;
	// �˼Ƭ���
	private int time = 5;
	// �w�]�S���W�r
	private String myName = "no name";
	// PrintStream�C��
	Vector<PrintStream> output;

	// Key:tid,Value:PrintStream
	// Tid��PrintStream
	Map<Integer, PrintStream> tmap;
	Map<PrintStream, Integer> pmap;
	// Map Tid��﨤, Tid��W�r
	Map<Integer, Integer> tchoose = new HashMap<>();
	Map<Integer, String> tname = new HashMap<>();
	// �����j�p
	private int sizex = 800;
	private int sizey = 600;
	// �C���}�l�F?
	boolean started = false;
	// setLive:�s�W�u��tid
	Set<Integer> setLive;
	// setLocked:�w�g��w��tid
	Set<Integer> setLocked;
	// setDeath:�w�g���`��tid
	Set<Integer> setDeath;
	// true:tellAll,false:tellOthers
	boolean tellWho = true;
	// ��Ѹ򵲦X�ϥΪ��T���ӧO�Ѽ�
	// For Instructions
	private int state;
	private int Tid;
	private String function;
	private int source;
	private int dest;
	private int type;
	private double X;
	private double Y;
	private int direction;
	private String Stype;

	public ServerCenter(Socket acceptSocket, int playerTid, Vector<PrintStream> x, Map<Integer, PrintStream> inserttMap,
			Map<PrintStream, Integer> insertpMap, Set<Integer> Live, Set<Integer> Locked, Set<Integer> Death,
			Map<Integer, Integer> tc, Map<Integer, String> tn, boolean flag) {
		try {
			sock = acceptSocket;
			// ���oSocket����J��Ƭy
			InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(isReader);

			setLive = Live;
			setLocked = Locked;
			setDeath = Death;
			tmap = inserttMap;
			pmap = insertpMap;
			myTid = playerTid;
			output = x;
			tchoose = tc;
			tname = tn;
			started = flag;
			setLive.add(myTid);
			System.out.print("Center �u�W�W�� for " + myTid + ":" + setLive);
			// �Ĥ@���s�u�^�ǵ�Client �L�M�ݪ�Thread id
			refreshInst();
			tidSend();
			if (setLive.size() > 1) {
				// �����e�H����T
				getChoose();
				getName();
			}
		} catch (Exception ex) {
			System.out.println("�s������ in Center");
		}
	}

	@Override
	public void run() {
		String message;
		try {
			// Ū�����
			while ((message = reader.readLine()) != null) {
				System.out.println("����" + message);
				if (message.contains("#")) {
					// �o�O�]���Ȯɴ��եΪ���client�|��W�r:���b�e��
					String a[] = message.split(":");
					System.out.println(a[1]);

					decoder(a[1]);
					handle();

					if (state == 0 && setLocked.size() == setLive.size() && !started) {
						// �p�G�j�a����w�F
						System.out.println("��w�w���C���ǳƶ}�l");
						// go1 �i�D�ۤv�ǳƭn�}�l
						inst6();
						tellAll();
						// initial�ǻ� �ۤv��X�Ӧۤvtid���üƦ�m���O�H
						calculateAll();
						// �}�l�˼�
						doCountDown();
						// �����˼�
						// �i�D�ۤv�C���}�l
						inst8();
						tellAll();
						started = true;
					} else if (setLive.size() > 1 && setLive.size() - 1 == setDeath.size() && setDeath.size() >= 1) {
						System.out.println("Ĺ�a�X�{");

						// �p�GĹ�a�X�{�F
						// win �i�D�ۤv��Ĺ�F
						inst13(whoWon());
						tellAll();
						// back1 �i�D�ۤv�}�l�˼Ʀ^��j�U
						inst14();
						tellAll();
						// �}�l�˼�
						doCountDown();
						// �����˼�
						// back2 �i�D�ۤv�^��j�U
						inst15();
						tellAll();
						// �w��w���M��M��->�j�a���s�﨤
						// �w�g���`���W��M��
						started = false;
						setLocked.clear();
						setDeath.clear();
					} else {
						tellOthers();
					}
				} else {
					// �O�d���`��ѫǥ\��debug �i����

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			dcHandle();
			System.out.println("Tid:" + myTid + "�s�����}" + ex.toString());
		}
	}

	// --------------------------------------------------------------------//
	// �T�����eSend Messages //
	// --------------------------------------------------------------------//
	public void tellAll() {
		// ����iterator�i�H�s�����X�����������
		// �o�O��ѫǥΪ��g�k �]�i�H�Ѧ� �O�e���Ҧ����H
		Iterator<PrintStream> it = output.iterator();
		while (it.hasNext()) {
			try {
				PrintStream writer = (PrintStream) it.next();
				if (setLive.contains(pmap.get(writer))) {
					writer.println(encoder(pmap.get(writer)));
					writer.flush();
					System.out.println(myTid + "tellAll");
				}

			} catch (Exception ex) {
				System.out.println("tellAll �Q��");
			}
		}
		// �e�������T���Ѽƪ�l��
		refreshInst();
	}

	public void tellOthers() {
		// ����iterator�i�H�s�����X�����������
		Iterator<PrintStream> it = output.iterator();
		while (it.hasNext()) {
			try {
				PrintStream writer = (PrintStream) it.next();
				// ��PS��tmap<Integer,PrintStream>�����쪺����Tid�h���
				// �p�G��n�O�ۤv���ܤ��o�e�T��
				if (writer != tmap.get(myTid)) {
					// ��pmap<PrintStream,Integer>�����쪺��PS�۹�����Tid����encoder
					// ��Encoder�h��g�����̪�Tid
					writer.println(encoder(pmap.get(writer)));
					// ��s�Ӧ�y���w�ġC
					writer.flush();
				} else {
					writer = null;
				}

			} catch (Exception ex) {
				System.out.println("tellOthers �Q��");
			}
		}
		// �e�������T���Ѽƪ�l��
		refreshInst();
	}

	// �����a�Ĥ@���s�u�ɭ�
	public void tidSend() {
		try {
			// ��map�hget PrintStream
			PrintStream writer = tmap.get(myTid);
			inst1(myTid);
			// �Ĥ@���ǰeTid��Client�٤����D�ۤv��tid
			String message = encoder(-1);
			writer.println(message);
			writer.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// �e�������T���Ѽƪ�l��
		refreshInst();
	}

	public void tellMyself() {
		Iterator<PrintStream> it = output.iterator();
		while (it.hasNext()) {
			try {
				PrintStream writer = (PrintStream) it.next();
				// ��PS��tmap<Integer,PrintStream>�����쪺����Tid�h���
				// �p�G��n�O�ۤv���ܴN�o�e�T��
				if (writer == tmap.get(myTid)) {
					// ��pmap<PrintStream,Integer>�����쪺��PS�۹�����Tid����encoder
					// ��Encoder�h��g�����̪�Tid
					writer.println(encoder(pmap.get(writer)));
					writer.flush();
				} else {
					writer = null;
				}

			} catch (Exception ex) {
				System.out.println("tellMyself �Q��");
			}
		}
		// �e�������T���Ѽƪ�l��
		refreshInst();
	}

	// --------------------------------------------------------------------//
	// �T���B�z��Deal Formats and Messages //
	// --------------------------------------------------------------------//
	public void decoder(String message) {
		// ��message��#���}
		String ta[] = message.split("#");
		// System.out.print("�Ѷ}�T��:");
		// for (int i = 0; i < ta.length; i++) {
		// System.out.print(ta[i] + "+");
		// }
		// System.out.println();
		// ��Ҧ���}���T���̧Ƕ�Jthread�����Ѽ�
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
	}

	// ��T�������榡
	public String encoder(int forwardTid) {
		// �����ѼƬO����̪�tid
		// �Τ@�Ӱ}�C��Ҧ��T����_��
		String ta[] = new String[10];
		ta[0] = Integer.toString(state);
		ta[1] = Integer.toString(forwardTid);
		ta[2] = function;
		ta[3] = Integer.toString(source);
		ta[4] = Integer.toString(dest);
		ta[5] = Integer.toString(type);
		ta[6] = Double.toString(X);
		ta[7] = Double.toString(Y);
		ta[8] = Integer.toString(direction);
		ta[9] = Stype;
		// �Q��StringBuilder��Ҧ��T���@�Ӥ@�Ӧ�_��
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < ta.length; i++) {
			strBuilder.append(ta[i]);
			if (i != ta.length - 1) {
				strBuilder.append("#");
			}
		}
		//
		String message = strBuilder.toString();
		return message;
	}

	// ���s�~���Ҧ��T����ѤU�Ӫ��Ѽ�
	public void refreshInst() {
		tellWho = true;
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

	// --------------------------------------------------------------------//
	// ��ƳB�z��Data Dealing //
	// --------------------------------------------------------------------//

	// �B�z�Ҧ������쪺�T��
	public void handle() {

		if (state == 0) {
			// state 0
			if (function.equals("connect")) {
				// �N�s�JTid�[�JLive����C
				// setLive.add(Tid);
				System.out.print("handle �u�W�W�� for " + myTid + ":" + setLive);
				//
				if (Stype.equals("@")) {
					myName = "no name" + Tid;
				} else {
					myName = Stype;
				}
				tname.put(Tid, Stype);
				inst2(Tid);

			} else if (function.equals("disconnect")) {
				// �N�_�uTid���hLive����C����
				setLive.remove(Tid);
				inst3(Tid);

			} else if (function.equals("choose")) {
				tchoose.put(Tid, type);
				inst4(Tid, type);
			} else if (function.equals("lock")) {
				setLocked.add(Tid);
				System.out.print("��w�W�� for " + myTid + ":" + setLocked);
				inst5(Tid, type);
			}

		} else if (state == 1) {
			if (function.equals("atk")) {
				inst9(Tid, X, Y, direction);
			} else if (function.equals("atk2")) {
				inst10(Tid, X, Y, direction);
			} else if (function.equals("atked")) {
				inst11(Tid, X, Y, type);
			} else if (function.equals("death")) {
				setDeath.add(Tid);
				inst12(Tid);
			}else if(function.equals("moveup")){
				instUp(Tid);
			}else if(function.equals("movedown")){
				instDown(Tid);
			}else if(function.equals("moveleft")){
				instLeft(Tid);
			}else if(function.equals("moveright")){
				instRight(Tid);
			}

		}
	}

	// --------------------------------------------------------------------//
	// �S�����p���B�z Special data dealing //
	// --------------------------------------------------------------------//
	// �p��X��Ĺ�F
	public int whoWon() {
		Iterator<Integer> it = setLocked.iterator();
		int wtid = -1;
		while (it.hasNext() && wtid == -1) {
			try {
				wtid = (int) it.next();
				if (setDeath.contains(wtid)) {
					wtid = -1;
				} else {
				}

			} catch (Exception ex) {
				System.out.println("�p���Ĺ�Q��");
			}
		}
		return wtid;
	}

	// �Q��Thread Sleep�ӭ˼Ƥ���
	public void doCountDown() {
		// Timer timer = new Timer();
		System.out.println("Delay:" + time + "��");
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(myTid + "���}TimerTask");
	}

	// �p��üư_�l��m
	public void randomPosition() {
		Random r1 = new Random();
		int x = r1.nextInt(sizex);
		int y = r1.nextInt(sizey);
		X = (double) x - sizex / 2;
		Y = (double) y - sizey / 2;
	}

	// ��Ҧ���������chooseŪ�J
	public void getChoose() {
		Iterator it = tchoose.entrySet().iterator();
		while (it.hasNext()) {
			try {
				PrintStream writer = tmap.get(myTid);
				Entry thisEntry = (Entry) it.next();
				int key = (int) thisEntry.getKey();
				if (key != myTid && setLive.contains(key)) {
					if (setLocked.contains(key)) {
						inst5(key, tchoose.get(key));
					} else {
						inst4(key, tchoose.get(key));
					}
					writer.println(encoder(myTid));
					// ��s�Ӧ�y���w�ġC
					writer.flush();
				}
			} catch (Exception ex) {
				System.out.println("getChoose �Q��");
			}
		}
		// �e�������T���Ѽƪ�l��
		refreshInst();
	}

	// ��Ҧ����O���U�Ӫ�Tid�����W�rŪ�J
	public void getName() {
		Iterator it = tname.entrySet().iterator();
		while (it.hasNext()) {
			try {
				PrintStream writer = tmap.get(myTid);
				Entry thisEntry = (Entry) it.next();
				int key = (int) thisEntry.getKey();
				if (key != myTid && setLive.contains(key)) {
					inst2(key);
					Stype = tname.get(key);
					writer.println(encoder(myTid));
					// ��s�Ӧ�y���w�ġC
					writer.flush();
				}
			} catch (Exception ex) {
				System.out.println("getName �Q��");
			}
		}
		// �e�������T���Ѽƪ�l��
		refreshInst();
	}

	// ��Ҧ��üưѼƨ̧ǰe��Live�̭����H
	public void calculateAll() {
		Iterator<Integer> it = setLocked.iterator();
		while (it.hasNext()) {
			try {
				int key = it.next();
				inst7(key);
				randomPosition();
				tellAll();
			} catch (Exception ex) {
				System.out.println("�p���Ĺ�Q��");
			}
		}
	}

	public void dcHandle() {
		// �u�W��Tid�W�氣���_�u��
		// �çi�D��L�H�ڦۤv�_�u�F
		System.out.println("�}�l�M�z" + myTid + "�_�u�ݦs���U��......");
		setLive.remove(myTid);
		setLocked.remove(myTid);
		setDeath.remove(myTid);
		tname.remove(myTid);
		tchoose.remove(myTid);
		output.remove(tmap.get(myTid));
		pmap.remove(tmap.get(myTid));
		tmap.remove(myTid);
		inst3(myTid);
		tellOthers();
		System.out.println(myTid+" dcHandle:");
		System.out.println("�u�W�W��:"+setLive);
		System.out.println("��w�W��:"+setLocked);
		System.out.println("�u�W�W��:"+setDeath);
	}

	// --------------------------------------------------------------------//
	// ��X���O�榡Forward Formats //
	// --------------------------------------------------------------------//
	// �o��Tid����-1����]�O�]���n�e�X�h���ɭ�Encoder�|�ۤv���w
	public void inst1(int givetid) {
		// Instuction #1 connect,first time connect give tid
		state = 0;
		Tid = -1;
		function = "connect";
		source = -1;
		dest = -1;
		type = givetid;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = "@";
	}

	public void inst2(int givetid) {
		// Instuction #2 connected,tell others name
		state = 0;
		Tid = -1;
		function = "connected";
		source = -1;
		dest = -1;
		type = givetid;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = myName;
	}

	public void inst3(int dctid) {
		// Instuction #3 disconnected,tell all
		state = 0;
		Tid = -1;
		function = "disconnected";
		source = -1;
		dest = -1;
		type = dctid;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = "@";
	}

	public void inst4(int who, int which) {
		// Instuction #4 choosed,tell all who choose which champions
		state = 0;
		Tid = -1;
		function = "choosed";
		source = -1;
		dest = who;
		type = which;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = "@";
	}

	public void inst5(int who, int which) {
		// Instuction #5 locked,tell all who locked which champions
		state = 0;
		Tid = -1;
		function = "locked";
		source = -1;
		dest = who;
		type = which;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = "@";
	}

	public void inst6() {
		// Instuction #6 go1,tell all to count down
		state = 0;
		Tid = -1;
		function = "go1";
		source = -1;
		dest = -1;
		type = time;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = "@";
	}

	public void inst7(int whose) {
		// Instuction #7 initial,tell all where i am
		// XY�ƭȽվ�brandomPosition�̭�
		state = 0;
		Tid = -1;
		function = "initial";
		source = -1;
		dest = whose;
		type = -1;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = "@";
	}

	public void inst8() {
		// Instuction #8 go2,tell all game start
		state = 0;
		Tid = -1;
		function = "go2";
		source = -1;
		dest = -1;
		type = -1;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = "@";
	}

	public void inst9(int atktid, double x, double y, int dir) {
		// Instuction #9 atk,tell other i swing my sword
		state = 1;
		Tid = -1;
		function = "atk";
		source = -1;
		dest = atktid;
		type = -1;
		X = x;
		Y = y;
		direction = dir;
		Stype = "@";
	}

	public void inst10(int atk2tid, double x, double y, int dir) {
		// Instuction #10 atk2,tell other i shoot shit
		state = 1;
		Tid = -1;
		function = "atk2";
		source = -1;
		dest = atk2tid;
		type = -1;
		X = x;
		Y = y;
		direction = dir;
		Stype = "@";
	}

	public void inst11(int atkedtid, double x, double y, int hpnow) {
		// Instuction #11 atked,tell other i got hurt
		state = 1;
		Tid = -1;
		function = "atked";
		source = -1;
		dest = atkedtid;
		type = hpnow;
		X = x;
		Y = y;
		direction = 0;
		Stype = "@";
	}

	public void inst12(int deathtid) {
		// Instuction #12 atked,tell other i got hurt
		state = 1;
		Tid = -1;
		function = "death";
		source = -1;
		dest = deathtid;
		type = -1;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = "@";
	}

	public void inst13(int wontid) {
		// Instuction #13 win,tell all who won
		state = 2;
		Tid = -1;
		function = "win";
		source = -1;
		dest = -1;
		type = wontid;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = "@";
	}

	public void inst14() {
		// Instuction #14 back1,tell all to count down (back)
		state = 2;
		Tid = -1;
		function = "back1";
		source = -1;
		dest = -1;
		type = time;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = "@";
	}

	public void inst15() {
		// Instuction #15 back2,tell all to back
		state = 2;
		Tid = -1;
		function = "back2";
		source = -1;
		dest = -1;
		type = -1;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = "@";
	}
	public void instUp(int moveid){
		state = 1;
		Tid = -1;
		function = "moveup";
		source = -1;
		dest = moveid;
		type = -1;
		X = -1;
		Y = -1;
		direction = 0; 
		Stype = "@";
	}
	public void instDown(int moveid){
		state = 1;
		Tid = -1;
		function = "movedown";
		source = -1;
		dest = moveid;
		type = -1;
		X = -1;
		Y = -1;
		direction = 0;
		Stype = "@";
	}
	public void instLeft(int moveid){
		state = 1;
		Tid = -1;
		function = "moveleft";
		source = -1;
		dest = moveid;
		type = -1;
		X = -1;
		Y = -1;
		direction = -1;
		Stype = "@";
	}
	public void instRight(int moveid){
		state = 1;
		Tid = -1;
		function = "moveright";
		source = -1;
		dest = moveid;
		type = -1;
		X = -1; 
		Y = -1;
		direction = 1;
		Stype = "@";
		
	}

}