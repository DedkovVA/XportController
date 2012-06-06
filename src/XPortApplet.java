import java.applet.Applet;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.TextArea;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.GregorianCalendar;

public class XPortApplet extends Applet implements Runnable {
	static DataOutputStream out;
	static DataInputStream inp;
	private int doorData;
	private int doorData2;

	private TextArea output;

	static int ledState = 0;

	int serverPort = 10001;
	String address = "169.254.87.110";
	Socket socket;

	Thread thread = new Thread(this);

	public void start() {
		thread.start();
	}

	public void init() {
		setLayout(new FlowLayout());

		if (socket == null) {
			try {
				InetAddress ipAddress = InetAddress.getByName(address);
				socket = new Socket(ipAddress, serverPort);

				OutputStream sout = socket.getOutputStream();
				out = new DataOutputStream(sout);

				InputStream sinp = socket.getInputStream();
				inp = new DataInputStream(sinp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		repaint();

		this.output = new TextArea(25, 70);
		this.output.setEditable(false);
		this.add(output);
	}

	public void paint(Graphics g) {
	}

	@Override
	public void run() {
		if (socket != null) {
			while (true) {
				try {
					out.write(0);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					doorData = doorData2;
					doorData2 = inp.read();
				} catch (IOException e) {
					e.printStackTrace();
				}
				String mes;
				if (doorData == 254 && doorData2 == 255) {
					mes = "The door was closed at ";
					output.append(mes + new GregorianCalendar().getTime()
							+ "\n");
				} else if (doorData == 255 && doorData2 == 254) {
					mes = "The door was opened at ";
					output.append(mes + new GregorianCalendar().getTime()
							+ "\n");
				}
				System.out.println(doorData);
			}
		}
	}
}