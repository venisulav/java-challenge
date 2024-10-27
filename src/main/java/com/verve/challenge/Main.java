package com.verve.challenge;

import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger( Main.class.getName());
    private static void validateArgs(String[] args){
        if (args.length != 3){
            logger.severe("Please provide the required arguments.");
            logger.severe("Usage: java jarfile.jar port_number number_of_threads server|stress_generator");
            System.exit(-1);
        }
        if (!(args[2].equals("server") || args[2].equals("stress_generator"))){
            logger.severe("argument 3 must be server|stress_generator");
            System.exit(-1);
        }

    }
    public static void main(String[] args) throws Exception {
        validateArgs(args);
        int port = Integer.parseInt(args[0]);
        int nThreads = Integer.parseInt(args[1]);
        if (args[2].equals("server")){
            Server.runServer(port, nThreads);
        }else{
            LoadGenerator.runGenerator(port,nThreads);
        }
    }
}
