package fundamental;

import java.io.*;

public class OSCommander {
    public static void exec(String ... args) throws IOException, InterruptedException { 
        	String s = null;
        	// run the command using the Runtime exec method
            Process p = Runtime.getRuntime().exec(args);            
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream())); 
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream())); 
            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
             // read any errors from the command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            p.wait();
            p.destroy();
    }
}