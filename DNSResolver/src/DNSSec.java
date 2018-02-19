import org.xbill.DNS.*;
import org.xbill.DNS.DNSSEC.DNSSECException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

public class DNSSec {
	public boolean performDNSVerification(RRset[] recs, List<String> rootServers) throws UnknownHostException, IOException {
		for (RRset rrSet: recs) {
			Iterator<RRSIGRecord> signatures =  rrSet.sigs();
			//Check existence of RRSIG
			if(!signatures.hasNext()){  
				return false;
			}
			while (signatures.hasNext()) {
				//Get the RRSIG record
				RRSIGRecord record = signatures.next();
				Name owner = record.getSigner();
				int keyID = record.getFootprint();
				SimpleResolver resolver;
				Message res = null;
				for (int i=0; i < rootServers.size();i++) {
					resolver = new SimpleResolver(rootServers.get(i));
					resolver.setEDNS(0, 0, ExtendedFlags.DO, null);
					Record r = Record.newRecord(owner, Type.DNSKEY, DClass.IN);
					Message m = Message.newQuery(r);
					try {
						//GET THE DNSKEY
						res = resolver.send(m);
						break;
					}catch(java.net.SocketTimeoutException e) {
						System.out.println("Timeout Root Server : "+rootServers.get(i));
					}
				}
				//System.out.println("response===="+res);
				boolean KSKVerfied = false;
				if (res != null) {
					DNSKEYRecord keyRec = null;
					RRset[] innerRRsets = res.getSectionRRsets(Section.ANSWER);
					for (RRset rec: innerRRsets) {
						Iterator<DNSKEYRecord> rrData = rec.rrs();
						//System.out.println("rrData===="+rrData.toString());
						while (rrData.hasNext()) {
							DNSKEYRecord currentRecord = rrData.next();
							if (currentRecord!=null && currentRecord.getFootprint() > 0 && currentRecord.getFootprint() == keyID) {
								keyRec = currentRecord;
							}
							
						}
						//VERIFICATION WITH KSK
						//System.out.println(keyRec.rdataToString());
						Iterator<RRSIGRecord> innerSigs = rec.sigs();
						while (innerSigs.hasNext()) {
							RRSIGRecord currentSigRecord = innerSigs.next();
							System.out.println("innerSigs :"+currentSigRecord.getFootprint());
							if (currentSigRecord.getFootprint() == keyID) {
								try {
									DNSSEC.verify(rec, currentSigRecord, keyRec);
									System.out.println("Verified witk KSK");
									KSKVerfied = true;
								} catch (DNSSEC.KeyMismatchException e) {
									System.out.println("DNSSEC is configured	but	the	digital	signature could	NOT	be	verified");
									System.out.println("Verification failed due to KSK.");
									return false;
									
								} catch(DNSSEC.DNSSECException e) {
									System.out.println("DNSSEC is configured	but	the	digital	signature could	NOT	be	verified");
									System.out.println("Verification failed due to KSK.");
									return false;
								}

							} 
						}
						
					}
					boolean ZSKVerified = false;
					//VERIFICATION WITH ZSK IF KSK PASSES
					if (KSKVerfied) {
						try {
							DNSSEC.verify(rrSet, record, keyRec);
							ZSKVerified = true;
							System.out.println("Verified witk ZSK");
							System.out.println("DNSSEC is configured	and	everything is verified");
							return true;
						} catch (DNSSEC.KeyMismatchException e) {
							System.out.println("DNSSEC is configured	but	the	digital	signature could	NOT	be	verified");
							System.out.println("Verification failed due to KSK.");
							return false;
						}catch (DNSSECException e) {
							System.out.println("DNSSEC is configured	but	the	digital	signature could	NOT	be	verified");
							System.out.println("Verification failed due to ZSK.");
							return false;
						}
					}
					
				} else {
					return false;
				}
			}
			
		}
		return false;
	}
}
