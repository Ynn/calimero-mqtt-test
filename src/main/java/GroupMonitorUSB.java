/*
    Calimero 2 - A library for KNX network access
    Copyright (c) 2015, 2017 B. Malinowsky

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.concurrent.TimeoutException;


import tuwien.auto.calimero.DataUnitBuilder;
import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.KNXException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkUsb;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListener;

/**
 * Example code showing how to use KNX process communication for group monitoring on a KNX Twisted Pair 1 (TP1) network.
 * On receiving group notifications, the KNX source and destination address are printed to System.out, as well as any
 * data part of the application service data unit (ASDU) in hexadecimal format.
 * <p>
 * Note that this example does not exit, i.e., it monitors forever or until the KNX network link connection got
 * closed. Hence, with KNX servers that have a limit on active tunneling connections (usually 1 or 4), if the group
 * monitor in connected state is terminated by the client (you), the pending state of the open tunnel on the KNX server
 * might temporarily cause an error on subsequent connection attempts.
 *
 * @author B. Malinowsky
 */
public class GroupMonitorUSB implements ProcessListener
{
	/**
	 * Address of your KNXnet/IP server. Replace the host or IP address as necessary.
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */

	public static void main(final String[] args) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, TimeoutException
	{
		new GroupMonitorUSB().run();
	}

	public void run() throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, TimeoutException
	{

		try (KNXNetworkLink knxLink = new KNXNetworkLinkUsb(0x135e, 0x0025,TPSettings.TP1);
				ProcessCommunicator pc = new ProcessCommunicatorImpl(knxLink)) 
		{

			// start listening to group notifications using a process listener
			pc.addProcessListener(this);

			while (knxLink.isOpen()) Thread.sleep(1000);
		}
		catch (final KNXException | InterruptedException | RuntimeException e) {
			System.err.println(e);
		}
	}

	public void groupWrite(final ProcessEvent e) { try {
		print("write.ind", e);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}}
	public void groupReadRequest(final ProcessEvent e) { try {
		print("read.req", e);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} }
	public void groupReadResponse(final ProcessEvent e) { try {
		print("read.res", e);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} }
	public void detached(final DetachEvent e) {}

	// Called on every group notification issued by a datapoint on the KNX network. It prints the service primitive,
	// KNX source and destination address, and Application Service Data Unit (ASDU) to System.out.
	private  void print(final String svc, final ProcessEvent e) throws IOException
	{
		try {
			System.out.println(LocalTime.now() + " " + e.getSourceAddr() + "->" + e.getDestination() + " " + svc
					+ ": " + DataUnitBuilder.toHex(e.getASDU(), ""));
		}
		catch (final RuntimeException ex) {
			ex.printStackTrace();
		}
	}
}
