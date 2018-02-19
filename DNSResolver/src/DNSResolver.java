import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.xbill.DNS.*;
import java.util.Arrays;
import java.util.*;

public class DNSResolver {
	
	List<String> rootServersList = new ArrayList<String>(
			Arrays.asList("198.41.0.4", "199.9.14.201", "192.33.4.12", "199.7.91.13", "192.203.230.10", 
					"192.5.5.241", "192.112.36.4", "198.97.190.5", "192.36.148.17", "192.58.128.30",
					"193.0.14.129", "199.7.83.4", "202.12.27.33"));
	
	List<String> rootServers = new ArrayList<String>(
			Arrays.asList("198.41.0.4", "199.9.14.201", "192.33.4.12", "199.7.91.13", "192.203.230.10", 
					"192.5.5.241", "192.112.36.4", "198.97.190.5", "192.36.148.17", "192.58.128.30",
					"193.0.14.129", "199.7.83.4", "202.12.27.33"));
	
	
	
	private Message lookUpAddress(Message message, List<String> servers) throws UnknownHostException, IOException {
		Message res = null;
		for (int i=0; i < servers.size();i++) {
			SimpleResolver resolver =  new SimpleResolver(rootServers.get(i));
			try {
				res = resolver.send(message);
				System.out.println(res);
				break;
			}catch(java.net.SocketTimeoutException e) {
				System.out.println("Timeout Root Server : "+rootServers.get(i));
			}
		}
		return res;
	}
	
	private String getAddressFromAdditional (Name name, Record[] additionalRecs) {
		String ipAddress = null;
		for(Record rec: additionalRecs) {
			if (rec.getType() == Type.A && name.equals(rec.getName())) {
				ipAddress = ((ARecord) rec).getAddress().getHostAddress();
				break;
			}
		}
		return ipAddress;
	}


	public Message resolveDNS(String hostName, int dnsType, boolean dnssec_required) throws IOException {
		
		Message response = null;
		String[] labels = hostName.split("\\.");
		labels[labels.length - 1] = labels[labels.length - 1] + ".";
		String dnsString = null;
		int dType = Type.ANY;
		for (int i=labels.length-1;i>=0;i--) {
			if (dnsString == null) {
				dnsString = labels[i];
			} else {
				dnsString = labels[i] + "."+ dnsString;
			}
			
			//System.out.println(dnsString); 
			
			Name n = Name.fromString(dnsString);
			Record r = Record.newRecord(n, dType, DClass.IN);
			Message m = Message.newQuery(r);
			response = lookUpAddress(m, rootServers);
//			System.out.println("===============");
			//System.out.println(response);
			if (response != null) {
				Record[] answerRecords = response.getSectionArray(Section.ANSWER);
				Record[] authorityRecs = response.getSectionArray(Section.AUTHORITY);
				Record[] additionalRecs = response.getSectionArray(Section.ADDITIONAL);
				
				if (authorityRecs != null) {
					rootServers.clear();
					for (Record authRec: authorityRecs) {
						if (authRec.getType() == Type.NS) {
							Name answerName = ((NSRecord) authRec).getTarget();
							String ipAddress =  getAddressFromAdditional(answerName, additionalRecs);
							if (ipAddress != null) {
								rootServers.add(ipAddress);
							}
						}
					}
				} 
				 
				for (Record answer: answerRecords) {
					if (answer.getType() == Type.A && dnsType == Type.A) {
						return response;
					} else if (answer.getType() == Type.CNAME) {
						rootServers.clear();
						rootServers = rootServersList;
						response = resolveDNS((((CNAMERecord) answer).getTarget().toString()), dnsType, false);
						if (response != null) {
							response.addRecord(((CNAMERecord) answer), Section.ANSWER);
						}
					}
					
				}
//				
			
				
				
			}
			
			//break;
		}
		
		
		return setRecordsForFinalMessage(response);
	}
	
	private Message setRecordsForFinalMessage(Message response) {
		if (response != null) {
			Record[] answerRecords = response.getSectionArray(Section.ANSWER);
			Record[] authorityRecs = response.getSectionArray(Section.AUTHORITY);
			Record[] additionalRecs = response.getSectionArray(Section.ADDITIONAL);
			if (answerRecords == null || answerRecords.length == 0) {
				for (Record authRec: authorityRecs) {
					if (authRec.getType() == Type.NS) {
						response.addRecord(((NSRecord) authRec), Section.ANSWER);
					} else if (authRec.getType() == Type.A) {
						response.addRecord(((ARecord) authRec), Section.ANSWER);
					} else if (authRec.getType() == Type.MX) {
						response.addRecord(((MXRecord) authRec), Section.ANSWER);
					} else if (authRec.getType() == Type.CNAME) {
						response.addRecord(((CNAMERecord) authRec), Section.ANSWER);
					}
				}
			}
		}
		
		return response;
	}

}
