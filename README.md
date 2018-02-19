# FCN_Assignment1 
External Libraries Used:
------------------------
1.java.io.IOException;
2. java.net.UnknownHostException;
3. java.util.*;
4. org.xbill.DNS.*;

Run the program:
------------------------
1. Compile the files using javac
2. To run the program:
	java MyDig www.google.com A true
3. First argument should be hostname
4. Second argument should be the Type	
5. To enable DNSSEC validation

DNS SEC Validation
-------------------------
Pass the third argument as DNSSEC as true for DNSSEC validation.

Local DNS Expirement
-------------------------
1. Complile Example.java using javac
2. Run the following program to obtain the average query time for top 25 websites in Alexa.
	java Example

