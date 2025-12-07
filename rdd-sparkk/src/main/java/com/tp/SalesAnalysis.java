package com.tp;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class SalesAnalysis {
    public static void main(String[] args) {
        // Configuration Spark
        SparkConf conf = new SparkConf().setAppName("SalesAnalysis").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Lecture du fichier depuis le dossier temporaire du conteneur
        JavaRDD<String> lines = sc.textFile("file:///tmp/ventes.txt");

        // --- Question 1 : Total ventes par ville ---
        System.out.println("=== Total des ventes par ville ===");
        lines.mapToPair(line -> {
                    String[] parts = line.split(" ");
                    return new Tuple2<>(parts[1], Double.parseDouble(parts[3])); // (Ville, Prix)
                })
                .reduceByKey(Double::sum)
                .collect()
                .forEach(t -> System.out.println(t._1 + " : " + t._2));

        // --- Question 2 : Total ventes par ville et année ---
        System.out.println("\n=== Total des ventes par ville et année ===");
        lines.mapToPair(line -> {
                    String[] parts = line.split(" ");
                    String year = parts[0].substring(0, 4);
                    String city = parts[1];
                    // Clé composite : "Ville-Année"
                    return new Tuple2<>(city + " (" + year + ")", Double.parseDouble(parts[3]));
                })
                .reduceByKey(Double::sum)
                .collect()
                .forEach(t -> System.out.println(t._1 + " : " + t._2));

        sc.close();
    }
}