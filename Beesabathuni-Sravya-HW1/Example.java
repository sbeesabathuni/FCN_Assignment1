import java.io.IOException;

import org.xbill.DNS.*;

public class Example {
	public static void main(String[] args) {
		String hostNames[] = {  
		
		"google.com", "youtube.com", "facebook.com", "baidu.com", "wikipedia.org","reddit.com","yahoo.com",
		"google.co.in", "qq.com","taobao.com", "amazon.com", "tmall.com", "twitter.com", "google.co.jp", "instagram.com",
		"live.com", "vk.com",  "sohu.com", "sina.com.cn", "jd.com", "weibo.com", "360.cn",  "google.de", 
		"google.co.uk", "google.co.br"};
		
		DNSResolver dResolver = new DNSResolver();
		
		for (int i=0; i<hostNames.length;i++) {
			Long[] durationTimes = new Long[10];
			for(int count = 0; count <10;count++) {
				try {
					long startTime = System.nanoTime();
					Message response = dResolver.resolveDNS(hostNames[i], Type.A, false);
					long endTime = System.nanoTime();
					long duration = (endTime - startTime) / 1000000; 
					durationTimes[count] = duration;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
			}
			int sum = 0;
			for (long d : durationTimes) sum += d;
			long avg = sum/10;
			
			System.out.println("Average time     to resolve "+hostNames[i]+":"+avg);
		}
		
		
		
	}
}
