# FCN_Assignment1 
External Libraries Used:
------------------------
1. java.io.IOException;
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
5. Third argument to enable DNSSEC validation - values are true of false
6. Supported Types are A, NS and MX
7. Outputs can found in mydig_output.txt

DNS SEC Validation
-------------------------
Pass the third argument as DNSSEC as true for DNSSEC validation.

Local DNS Experiment
-------------------------
1. Complie Example.java using javac
2. Run the following program to obtain the average query time for top 25 websites in Alexa.
	java Example
3. Outputs can be found in output_2.txt

Graph
--------------------------
1. DNS_Values_Final.xlxs contains the average query time of all the resolvers.
2. CDF_Graph_Values.xlxs contains the CDF graph along with the values.

