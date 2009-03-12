/*
 * Copyright 2008 EGEE Collaboration
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.pep.client;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.glite.authz.common.config.ConfigurationException;
import org.glite.authz.common.model.Request;
import org.glite.authz.common.model.Response;
import org.glite.authz.common.util.Files;
import org.glite.authz.pep.client.config.PEPClientConfiguration;
import org.glite.authz.pep.client.config.PEPClientIniConfigurationParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  */
public final class PEPCLI {

    /** Class logger. */
    private static final Logger LOG = LoggerFactory.getLogger(PEPCLI.class);

    /** Constructor. */
    private PEPCLI() {
    }

    /**
     * Entry point for starting the daemon.
     * 
     * @param args command line arguments
     * 
     * @throws Exception thrown if there is a problem starting the daemon
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 1) {
            printHelp(System.out);
        }

        PEPClientConfiguration clientConfing = parseConfiguration(args[0]);
        BlockingPolicyEnforcementPoint pepClient = new BlockingPolicyEnforcementPoint(clientConfing);

        Request request = new Request();
        Response response = pepClient.authorize(request);

        System.out.println("Made Request:\n" + request);
        System.out.println("Recieved Response:\n" + response);
    }

    /**
     * Reads the configuration file and creates a configuration from it.
     * 
     * @param configFilePath path to configuration file
     * 
     * @return configuration file and creates a configuration from it
     */
    private static PEPClientConfiguration parseConfiguration(String configFilePath) {
        File configFile = null;

        try {
            configFile = Files.getReadableFile(configFilePath);
        } catch (IOException e) {
            errorAndExit(e.getMessage(), null);
        }

        try {
            PEPClientIniConfigurationParser configParser = new PEPClientIniConfigurationParser();
            return configParser.parse(new FileReader(configFile));
        } catch (IOException e) {
            errorAndExit("Unable to read configuration file " + configFilePath, e);
        } catch (ConfigurationException e) {
            errorAndExit("Error parsing configuration file " + configFilePath, e);
        }
        return null;
    }

    /**
     * Logs, as an error, the error message and exits the program.
     * 
     * @param errorMessage error message
     * @param e exception that caused it
     */
    private static void errorAndExit(String errorMessage, Exception e) {
        System.err.println(errorMessage);
        if (e != null) {
            System.err.println("This error was caused by the exception:");
            e.printStackTrace(System.err);
        }

        System.out.flush();
        System.exit(1);
    }

    /**
     * Prints a help message to the given output stream.
     * 
     * @param out output to print the help message to
     */
    private static void printHelp(PrintStream out) {
        out.println("Policy Enforcement Point (PEP) Client");
        out.println();

        out.println("Usage pepclient <configFile>");
        System.out.flush();
        System.exit(0);
    }
}