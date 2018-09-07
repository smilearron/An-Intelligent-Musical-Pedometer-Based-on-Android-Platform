package com.example.huiyicao.music_pedometer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by huiyicao on 11/27/17.
 */

public class CsvFiles {
    private int walkStep=0;
    private int runStep=0;
    private double walkDist=0;
    private double runDist=0;
    private double walkSpeed = 0;
    private double runSpeed = 0;
    private double dist=0;//total distance
    private double walkDuration = 0;
    private double runDuration = 0;
    private String time;
    private File csvFile;
    private String CSVDIR;

    public CsvFiles(int ws,int rs,double wd,double rd,double dst, double wdu, double rdu, double wsp, double rsp){
        walkStep = ws;
        runStep = rs;
        walkDist = wd;
        runDist = rd;
        walkDuration = wdu;
        runDuration = rdu;
        walkSpeed = wsp;
        runSpeed = rsp;
        dist = dst;
        time=getCurrentTimeStamp();
        save();
    }

    public static String getCurrentTimeStamp(){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date
            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void save() {
        FileOutputStream outputStream = null;
        try {

            CSVDIR = android.os.Environment.getExternalStorageDirectory() + "/DailyReport";
            String baseDir = CSVDIR;
            String fileCsvName = time + "_report.csv";
            String filePath = baseDir + File.separator + fileCsvName;
            csvFile = new File(filePath);
            csvFile.createNewFile();
            outputStream = new FileOutputStream(csvFile, false);
            StringBuilder sb;
            sb = new StringBuilder();
            sb.append("TimeStamp,");
            sb.append("Walking Step,");
            sb.append("Walking Distance,");
            sb.append("Walking Duration,");
            sb.append("Walking Speed,");
            sb.append("Running Step,");
            sb.append("Running Distance,");
            sb.append("Running Duration,");
            sb.append("Running Speed,");
            sb.append("Total Distance,");
            sb.append("\n");
            sb.append(getCurrentTimeStamp()).append(",");
            sb.append(walkStep).append(",");
            sb.append(walkDist*1000).append(",");
            sb.append(walkDuration/1000).append(",");
            sb.append(walkSpeed*1000000).append(",");
            sb.append(runStep).append(",");
            sb.append(runDist*1000).append(",");
            sb.append(runDuration/1000).append(",");
            sb.append(runSpeed*1000000).append(",");
            sb.append(dist*1000).append(",");
            sb.append("\n");
            outputStream.write(sb.toString().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
