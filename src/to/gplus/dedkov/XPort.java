package to.gplus.dedkov;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class XPort {
	static Enumeration portList;
	static CommPortIdentifier portId;
	static String messageString = "Hello, world!\n";
	static SerialPort serialPort;
	static OutputStream outputStream;

	static Display display;
	static Shell shell;
	static Rectangle clientArea;
	static GC gc;
	static Button greenLed;
	static Button redLed;
	static Button findPort;
	static Text COMNo;
	static Label COMLabel;

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

		COMLabel = new Label(shell, SWT.SINGLE);
		COMLabel.setText("COM");
		COMLabel.setBounds(clientArea.x + 200, clientArea.y + 20, 40, 30);

		COMNo = new Text(shell, SWT.SINGLE);
		COMNo.setText("4");
		COMNo.setBounds(clientArea.x + 250, clientArea.y + 20, 40, 30);
		COMNo.addListener(SWT.Verify, new TextValidatorListener());

		findPort = new Button(shell, SWT.PUSH);
		findPort.setBounds(clientArea.x + 300, clientArea.y + 20, 60, 30);
		findPort.setText("Find Port");
	}

	public static class TextValidatorListener implements Listener {
		public void handleEvent(Event e) {
			String string = e.text;
			char[] chars = new char[string.length()];
			string.getChars(0, chars.length, chars, 0);
			for (int i = 0; i < chars.length; i++) {
				if (!('0' <= chars[i] && chars[i] <= '9')) {
					e.doit = false;
					return;
				}
			}
		}
	}

	public static void main(String[] args) {
		prepareWidgets();

		greenLed.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				ledState ^= 0x10;
				try {
					outputStream.write(ledState);
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
					outputStream.write(ledState);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		findPort.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				portList = CommPortIdentifier.getPortIdentifiers();

				while (portList.hasMoreElements()) {
					portId = (CommPortIdentifier) portList.nextElement();
					if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
						if (portId.getName().equals("COM" + COMNo.getText())) {
							try {
								serialPort = (SerialPort) portId.open(
										"XPortApp", 2000);
							} catch (PortInUseException e) {
								e.printStackTrace();
							}
							try {
								serialPort.setSerialPortParams(9600,
										SerialPort.DATABITS_8,
										SerialPort.STOPBITS_1,
										SerialPort.PARITY_NONE);
							} catch (UnsupportedCommOperationException e) {
								e.printStackTrace();
							}
							try {
								outputStream = serialPort.getOutputStream();
							} catch (IOException e) {
								e.printStackTrace();
							}
							try {
								outputStream.write(0x00);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
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