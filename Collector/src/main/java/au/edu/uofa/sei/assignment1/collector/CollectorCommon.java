package au.edu.uofa.sei.assignment1.collector;

import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

public class CollectorCommon {
    static {
        // init the logger
        Properties prop = new Properties();
        prop.setProperty("log4j.rootLogger", "INFO");
        PropertyConfigurator.configure(prop);
    }
}
