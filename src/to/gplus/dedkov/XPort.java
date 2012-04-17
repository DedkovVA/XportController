package to.gplus.dedkov;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class XPort {
	static DataOutputStream out;

	static Display display;
	static Shell shell;
	static Rectangle clientArea;
	static GC gc;
	static Button greenLed;
	static Button redLed;
	static Button findPort;

	static int ledState = 0;

	static void prepareWidgets() {
		display = new Display();
		shell = new Shell(display);
		shell.setSize(400, 200);
		shell.setLocation(10, 10);
		clientArea = shell.getClientArea();
		gc = new GC(shell);

		greenLed = new Button(shell, SWT.PUSH);
		greenLed.setBounds(clientArea.x + 20, clientArea.y + 20, 60, 30);
		greenLed.setText("GREEN");

		redLed = new Button(shell, SWT.PUSH);
		redLed.setBounds(clientArea.x + 100, clientArea.y + 20, 60, 30);
		redLed.setText("RED");

		findPort = new Button(shell, SWT.PUSH);
		findPort.setBounds(clientArea.x + 300, clientArea.y + 20, 60, 30);
		findPort.setText("Find Port");
	}

	static int serverPort = 10001;
	static String address = "169.254.87.110";
	static Socket socket;

	public static void main(String[] args) {
		prepareWidgets();

		greenLed.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				ledState ^= 0x10;
				try {
					out.write(ledState);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		redLed.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				ledState ^= 0x01;
				try {
					out.write(ledState);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		findPort.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
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
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}