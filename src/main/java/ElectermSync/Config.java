package main.java.ElectermSync;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private Dotenv dotenv;

    public Config() {
        dotenv = Dotenv.load();
    }

    public Config(String path) {
        dotenv = Dotenv.configure().directory(path).load();
    }

    public String getValue(String name) {
        String sysProp = System.getProperty(name);
        if (sysProp != null) {
            return sysProp;
        }
        return dotenv.get(name);
    }
}
