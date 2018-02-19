
import org.xbill.DNS.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;

public class MyDig {
	
	public static void main(String[] args) throws RelativeNameException, IOException {
		String hostname = args[0];
		String type = args[1];
		boolean enableDNSSEC = Boolean.parseBoolean(args[2]);
		int dnsType = Type.A;
		if (type.equals("A")) {
			dnsType = Type.A;
		} else if (type.equals("NS")) {
			dnsType = Type.NS;
		} else if (type.equals("MX")) {
			dnsType = Type.MX;
		}	
		
		DNSResolver dResolver = new DNSResolver();
		
		long startTime = System.nanoTime();
		Message response = dResolver.resolveDNS(hostname, dnsType, enableDNSSEC);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000; 
		
		Name n = Name.fromString(hostname+".");
		Record r = Record.newRecord(n, dnsType, DClass.IN);
		Message finalMessage = Message.newQuery(r);
		
		if (response != null) {
			Record[] finalAnswer = response.getSectionArray(Section.ANSWER);
			
			for (int i=0;i<finalAnswer.length;i++) {
				if (dnsType == Type.A && (finalAnswer[i].getType() == Type.A || finalAnswer[i].getType() == Type.CNAME)) {
					finalMessage.addRecord(finalAnswer[i], Section.ANSWER);
				} else if (dnsType == Type.NS &&  finalAnswer[i].getType() == Type.NS){
					finalMessage.addRecord(finalAnswer[i], Section.ANSWER);
				} else if (dnsType == Type.MX &&  finalAnswer[i].getType() == Type.MX){
					finalMessage.addRecord(finalAnswer[i], Section.ANSWER);
				} 
			}
			if(finalMessage.getSectionArray(Section.ANSWER).length == 0) {
				for (int i=0;i<finalAnswer.length;i++) {
					finalMessage.addRecord(finalAnswer[i], Section.ANSWER);
					
				}
			}
			
		}
		System.out.println(finalMessage);
		
		System.out.println("Query time : "+duration+"ms");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMMM d yyyy hh:mm:ss");
	    System.out.println("WHEN : " + dateFormatter.format(new Date()));
	}
}
