package io.system.counter;

import io.system.counter.manager.FileProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.system.counter.util.PropertiesUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if(args.length == 0){
            log.info("Need path config.properties");
            log.info("Or config.properties rulePathFile dataPathFile");
            log.info("Or config.properties rulePathFile dataPathFile outPathFile");
            return;
        }
        final Path path = Paths.get(args[0]);
        if(!Files.exists(path)){
            log.error("Not find config file {}", args[0]);
            return;
        }
        PropertiesUtil.load(args[0]);
        String ruleFileName = PropertiesUtil.get("main.ruleFile");
        String dataFileName = PropertiesUtil.get("main.dataFile");
        String outFileName = PropertiesUtil.get("main.outFile");
        if(args.length == 3){
            ruleFileName = args[1];
            dataFileName = args[2];
        }
        if(args.length == 4){
            outFileName = args[3];
        }
        try {
            FileProcessor.checkRulesAndWrite(ruleFileName, dataFileName, outFileName);
        } catch (Exception e) {
            log.error("RuleFileName {} DataFileName {}  OutFileName {} {}", ruleFileName, dataFileName, outFileName, e);
        }
    }
}
