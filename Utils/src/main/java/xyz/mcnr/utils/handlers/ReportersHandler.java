package xyz.mcnr.utils.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ReportersHandler {
    Map<String, Long> reporters = new HashMap<>();
    public File reportersFile = new File("reporters.txt");
    public File reports = new File("reports.txt");

    public void load() throws IOException {
        if (!reportersFile.exists()) Files.createFile(reportersFile.toPath());
        if (!reports.exists()) Files.createFile(reports.toPath());

        for (String line : Files.readAllLines(reportersFile.toPath())) {
            String[] data = line.split(" ");
            long time = Long.parseLong(data[1]);
            if ((System.currentTimeMillis() - time) < 86400000) reporters.put(data[0], time);
        }
    }

    public void save() throws IOException {
        String data = "";
        for (Map.Entry<String, Long> entry : reporters.entrySet()) {
            data += entry.getKey() + " " + entry.getValue() + "\n";
        }
        Files.writeString(reportersFile.toPath(), data);
    }

    public void add(String name) {
        reporters.put(name, System.currentTimeMillis());
    }

    public boolean check(String name) {
        for (Map.Entry<String, Long> entry : reporters.entrySet()) {
            if (entry.getKey().equals(name) && (System.currentTimeMillis() - entry.getValue()) < 86400000) return false;
        }
        return true;
    }
}
