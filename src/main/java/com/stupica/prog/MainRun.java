package com.stupica.prog;


import com.stupica.ConstGlobal;
import com.stupica.GlobalVar;

import com.stupica.core.UtilString;
import com.stupica.mainRunner.MainRunBase;
import jargs.gnu.CmdLineParser;

import java.net.HttpURLConnection;
import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by bostjans on 07/09/16.
 */
public class MainRun extends MainRunBase {
    // Variables
    //
    //boolean bIsModeTest = true;

    String  sUrl = "http://localhost:8080/";

    /**
     * Main object instance variable;
     */
    private static MainRun objInstance;

    CmdLineParser.Option obj_op_url;

    private static Logger logger = Logger.getLogger(MainRun.class.getName());


    /**
     * @param a_args    ..
     */
    public static void main(String[] a_args) {
        // Initialization
        GlobalVar.getInstance().sProgName = "javaProbe";
        GlobalVar.getInstance().sVersionBuild = "011";

        // Generate main program class
        objInstance = new MainRun();

        iReturnCode = objInstance.invokeApp(a_args);

        // Return
        if (iReturnCode != ConstGlobal.PROCESS_EXIT_SUCCESS)
            System.exit(iReturnCode);
    }


    protected void printUsage() {
        super.printUsage();
        System.err.println("            [-u,--url]URL(https://..)");
    }


    protected void initialize() {
        super.initialize();
        bShouldReadConfig = false;
        //bIsProcessInLoop = false;
    }


    /**
     * Method: defineArguments
     *
     * ..
     *
     * @return int iResult	1 = AllOK;
     */
    protected int defineArguments() {
        // Local variables
        int         iResult;

        // Initialization
        iResult = super.defineArguments();

        obj_op_url = obj_parser.addStringOption('u', "url");
        return iResult;
    }

    /**
     * Method: readArguments
     *
     * ..
     *
     * @return int iResult	1 = AllOK;
     */
    protected int readArguments() {
        // Local variables
        int         iResult;

        // Initialization
        iResult = super.readArguments();

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            // Set program parameter
            objInstance.sUrl = (String)obj_parser.getOptionValue(obj_op_url, "");
        }
        return iResult;
    }


    /**
     * Method: run
     *
     * Run ..
     *
     * @return int	1 = AllOK;
     */
    public int run() {
        // Local variables
        int         iResult;
        URL         objUrl = null;

        // Initialization
        iResult = super.run();

        if (UtilString.isEmptyTrim(sUrl)) {
            System.out.println("URL not provided!");
            iResult = ConstGlobal.RETURN_NA;
            return iResult;
        } else {
            System.out.println("Checking URL: " + sUrl);
        }

        try {
            objUrl = new URL(sUrl);
        } catch (MalformedURLException e) {
            iResult = ConstGlobal.RETURN_INVALID;
            System.err.println("URL is not correct: " + sUrl);
            e.printStackTrace();
            return iResult;
        }
        System.out.print("\tProtocol: " + objUrl.getProtocol());
        System.out.println("\tHost: " + objUrl.getHost());
        System.out.println("\tPath: " + objUrl.getPath());
        System.out.println("\tPort(def): " + objUrl.getDefaultPort()
                + "\tPort: " + objUrl.getPort());

        // Do ..
        //
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            try {
                // Run ..
                iResult = testHttp(objUrl);
                // Error
                if (iResult != ConstGlobal.RETURN_OK) {
                    logger.severe("run(): Error at testHttp() operation! Result: " + iResult + ";");
                }
            } catch(Exception ex) {
                iResult = ConstGlobal.RETURN_ERROR;
                logger.severe("run(): Error at testHttp() operation! Result: " + iResult + ";");
            }
        }
        return iResult;
    }


    private int testHttp(URL aobjUrl) {
        // Local variables
        int                 iResult;
        StringBuilder       sMsg = new StringBuilder();
        HttpsURLConnection  conHttps = null;
        HttpURLConnection   conHttp = null;

        // Initialization
        iResult = ConstGlobal.RETURN_SUCCESS;
        sMsg.append("\t.. test HTTP -> .. ");

        try {
            if (aobjUrl.getDefaultPort() == 443) {
                conHttps = (HttpsURLConnection)aobjUrl.openConnection();
                conHttp = conHttps;
            } else
                conHttp = (HttpURLConnection)aobjUrl.openConnection();

            if (conHttp != null) {
                sMsg.append("Response: ").append(conHttp.getResponseCode());
                if ((conHttp.getResponseCode() >= 100) && (conHttp.getResponseCode() < 300)) {
                    // .. all OK;
                } else {
                    iResult = ConstGlobal.RETURN_ERROR;
                }
                // .. dump all the content
                if (GlobalVar.bIsModeVerbose)
                    print_content(conHttp);
                conHttp.disconnect();
            }
        } catch (MalformedURLException e) {
            iResult = ConstGlobal.RETURN_ERROR;
            e.printStackTrace();
        } catch (IOException e) {
            iResult = ConstGlobal.RETURN_ERROR;
            e.printStackTrace();
        }
        System.out.println(sMsg.toString());
        return iResult;
    }


    private void print_content(HttpURLConnection con) {
        String input;

        if (con != null) {
            try {
                System.out.println("****** Content of the URL ********");
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));

                while ((input = br.readLine()) != null) {
                    System.out.println(input);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
