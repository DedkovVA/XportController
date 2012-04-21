import java.applet.Applet;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class XPortApplet extends Applet implements ActionListener {
	static DataOutputStream out;

	static int ledState = 0;
	static Button findPort;
	static Button greenLed;
	static Button redLed;

	int serverPort = 10001;
	String address = "169.254.87.110";
	Socket socket;

	public void init() {
		setLayout(new FlowLayout());
		findPort = new Button("Find port");
		greenLed = new Button("Green");
		redLed = new Button("Red");
		add(findPort);
		add(greenLed);
		add(redLed);

		findPort.addActionListener(this);
		greenLed.addActionListener(this);
		redLed.addActionListener(this);
	}

	public void paint(Graphics g) {
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource().equals(findPort)) {
			if (socket == null) {
				try {
					InetAddress ipAddress = InetAddress.getByName(address);
					socket = new Socket(ipAddress, serverPort);
					OutputStream sout = socket.getOutputStream();
					out = new DataOutputStream(sout);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					out.write(0x00);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			repaint();
		} else if (evt.getSource().equals(greenLed)) {
			ledState ^= 0x10;
			try {
				out.write(ledState);
			} catch (IOException e) {
				e.printStackTrace();
			}
			repaint();
		} else if (evt.getSource().equals(redLed)) {
			ledState ^= 0x01;
			try {
				out.write(ledState);
			} catch (IOException e) {
				e.printStackTrace();
			}
			repaint();
		}
	}
}