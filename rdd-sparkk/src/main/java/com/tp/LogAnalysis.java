package com.tp;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogAnalysis {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("LogAnalysis").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Chargement du fichier
        JavaRDD<String> logLines = sc.textFile("file:///tmp/access.log");

        // Regex pour extraire : IP, Date, Méthode, URL, Code, Taille
        // Groupe 1: IP, Groupe 3: Date, Groupe 4: Méthode, Groupe 5: URL, Groupe 6: Code
        String logRegex = "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S+) (\\S+) (\\S+)\" (\\d{3}) (\\d+|-)";
        Pattern pattern = Pattern.compile(logRegex);

        // Parsing et filtrage des lignes valides
        JavaRDD<String[]> parsedLogs = logLines.map(line -> {
            Matcher m = pattern.matcher(line);
            if (m.find()) {
                // Retourne [IP, Date, Méthode, URL, Code]
                return new String[]{m.group(1), m.group(4), m.group(5), m.group(6), m.group(8)};
            }
            return null;
        }).filter(x -> x != null);

        parsedLogs.cache(); // Optimisation

        // 1. Stats de base
        long total = parsedLogs.count();
        long errors = parsedLogs.filter(cols -> Integer.parseInt(cols[4]) >= 400).count();
        System.out.println("Total requêtes : " + total);
        System.out.println("Total erreurs (4xx, 5xx) : " + errors);

        // 2. Top 5 Adresses IP
        System.out.println("\n=== Top 5 IPs ===");
        parsedLogs.mapToPair(cols -> new Tuple2<>(cols[0], 1))
                .reduceByKey(Integer::sum)
                .mapToPair(Tuple2::swap) // Échange (IP, Count) -> (Count, IP) pour trier
                .sortByKey(false)        // Tri décroissant
                .take(5)
                .forEach(t -> System.out.println(t._2 + " : " + t._1 + " requêtes"));

        // 3. Top 5 Ressources
        System.out.println("\n=== Top 5 Ressources ===");
        parsedLogs.mapToPair(cols -> new Tuple2<>(cols[3], 1))
                .reduceByKey(Integer::sum)
                .mapToPair(Tuple2::swap)
                .sortByKey(false)
                .take(5)
                .forEach(t -> System.out.println(t._2 + " : " + t._1));

        // 4. Répartition par Code HTTP
        System.out.println("\n=== Répartition Codes HTTP ===");
        parsedLogs.mapToPair(cols -> new Tuple2<>(cols[4], 1))
                .reduceByKey(Integer::sum)
                .collect()
                .forEach(t -> System.out.println("Code " + t._1 + " : " + t._2));

        sc.close();
    }
}