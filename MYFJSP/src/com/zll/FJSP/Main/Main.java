package com.zll.FJSP.Main;

/**
 * Description:遗传算法求解FJSP 主函数
 *
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年5月28日 下午2:18:10
 */

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.zll.FJSP.Data.Input;
import com.zll.FJSP.Data.Problem;
import com.zll.FJSP.Data.Solution;
import com.zll.FJSP.GA.MyHybridAlgorithm;

public class Main {

    public static String[] MKinstances = new String[]{"Mk01.txt", "Mk02.txt", "Mk03.txt", "Mk04.txt", "Mk05.txt",
            "Mk06.txt", "Mk07.txt", "Mk08.txt", "Mk09.txt", "Mk10.txt"};
    public static String[] bestCost_HA = new String[]{"40", "26", "204", "60", "172",
            "57", "139", "523", "307", "197"};

    public static String[] instances = MKinstances; // new String[]{"Mk02.txt"};

    public static void main(String[] args) {
        int times = 1;
        double[][][] resultGA = new double[times][instances.length][2];

        for (int i = 0; i < times; i++) {
            for (int j = 0; j < instances.length; j++) {
                Input input = new Input(new File("./input/" + instances[j]));// 输入算例
                Problem p = input.getProblemDesFromFile();

                MyHybridAlgorithm GA = new MyHybridAlgorithm(p);
                Solution GAbest = GA.solve();
//                Solution TSbest = NeighbourAlgorithms.neighbourSearch(GAbest);

                System.out.println();
                System.out.println("HAbest:" + GAbest.cost);
                GAbest.checkSolution();

//                System.out.println();
//                GAbest.printSchedPicInConsole();
//                TSbest2.printSchedPicInConsole();
//                TSbest.checkSolution();

                resultGA[i][j][0] = GAbest.cost;
                resultGA[i][j][1] = GAbest.algrithmTimeCost;
            }
        }

        printToCSV("HA FJSP test" + new Date().getTime() + ".csv", resultGA);
    }

    public static void printToCSV(String FILE_NAME, double[][][] result) {
        final String[] FILE_HEADER = {"Instances", "bestCost_HA", "myBestCost"};

        FileWriter fileWriter = null;
        CSVPrinter csvPrinter = null;
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER);

        try {
            fileWriter = new FileWriter(FILE_NAME);
            csvPrinter = new CSVPrinter(fileWriter, csvFormat);
            for (int i = 0; i < instances.length; i++) {
                List<String> record = new ArrayList<>();
                record.add(instances[i]);
                record.add(bestCost_HA[i]);
                for (int j = 0; j < result.length; j++) {
                    record.add(String.valueOf(result[j][i][0]));
                    record.add(String.valueOf(result[j][i][1]));
                }
                csvPrinter.printRecord(record);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvPrinter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
