package com.blogspot.gambitgeoff.ggwol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class GGWOLActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final int PORT = 9;

		Button wakeButton = (Button) GGWOLActivity.this
				.findViewById(R.id.wake_button);
		wakeButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				System.out.println("Sending wake command!");
				String ipaddr = ((EditText) GGWOLActivity.this
						.findViewById(R.id.ipaddr_text)).getText().toString();
				String macaddr = ((EditText) GGWOLActivity.this
						.findViewById(R.id.macaddr_text)).getText().toString();

				try {
					byte[] macBytes = getMacBytes(macaddr);
					byte[] bytes = new byte[6 + 16 * macBytes.length];
					for (int i = 0; i < 6; i++) {
						bytes[i] = (byte) 0xff;
					}
					for (int i = 6; i < bytes.length; i += macBytes.length) {
						System
								.arraycopy(macBytes, 0, bytes, i,
										macBytes.length);
					}

					InetAddress address = InetAddress.getByName(ipaddr);
					DatagramPacket packet = new DatagramPacket(bytes,
							bytes.length, address, PORT);
					DatagramSocket socket = new DatagramSocket();
					socket.send(packet);
					socket.close();
				} catch (Exception e) {
					System.out.println("Failed to send WOL packet: + e");
					System.exit(1);
				}

			}

		});

	}

	private static byte[] getMacBytes(String macStr)
			throws IllegalArgumentException {
		byte[] bytes = new byte[6];
		String[] hex = macStr.split("(\\:|\\-)");
		if (hex.length != 6) {
			throw new IllegalArgumentException("Invalid MAC address.");
		}
		try {
			for (int i = 0; i < 6; i++) {
				bytes[i] = (byte) Integer.parseInt(hex[i], 16);
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Invalid hex digit in MAC address.");
		}
		return bytes;
	}

}