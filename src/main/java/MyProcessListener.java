import java.io.IOException;
import java.time.LocalTime;

import tuwien.auto.calimero.DataUnitBuilder;
import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListener;

public class MyProcessListener implements ProcessListener {
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
