package org.azodi.prj;

import net.rcarz.jiraclient.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class JiraIssueCrawler {

    private static final String DB_NAME = "db.csv";
    private static String[] HEADER_NAMES = new String[] {
            "Type",
            "Assignee",
            "Created",
            "Created_Epoch",
            "Description",
            "Comments"
    };

    public static void main(String[] args) throws JiraException, IOException {

        System.out.println("!!!Jira Issue CSV export!!!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println();
        System.out.println();


        String jiraUrl;
        if (args == null || args.length < 1) {

            System.out.println("Please specify JIRA backend URL");
            System.out.println("Using default configured URL");
            System.out.println("https://issues.apache.org/jira");

            jiraUrl = "https://issues.apache.org/jira";
        } else {
            jiraUrl = args[0];
        }

        JiraClient client = new JiraClient(
                jiraUrl
        );
        System.out.println("Jira Client Ready");

        Scanner scanner = new Scanner(System.in);
        CSVFormat csvFormat;
        if (!Files.exists(Paths.get(DB_NAME))) {
            Files.createFile(Paths.get(DB_NAME));
            csvFormat = CSVFormat.DEFAULT.withHeader(HEADER_NAMES);
        } else {
            csvFormat = CSVFormat.DEFAULT;
        }
        try (
                FileWriter writer = new FileWriter(DB_NAME, true);
                BufferedWriter bw = new BufferedWriter(writer);
                CSVPrinter csvPrinter = new CSVPrinter(bw, csvFormat)
             ) {

            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    System.out.println();
                    System.out.print("Enter next issue key to convert: ");
                    String issueKey = scanner.next();
                    Issue issue = client.getIssue(issueKey);

                    save(issue, csvPrinter);

                    System.out.println(issue);
                } catch (Throwable ignored) {
                    System.out.println("Error happened fetching issue");
                    ignored.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void save(Issue issue, CSVPrinter csvPrinter) throws IOException, ParseException {
        /*
            "Type",
            "Assignee",
            "Created",
            "Created_Epoch",
            "Description",
            "Comments"
        */
        String issueType = issue.getIssueType().getName();
        String assignee = issue.getAssignee().getDisplayName();
        String created = String.valueOf(issue.getField("created"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String createdEpoch = String.valueOf(dateFormat.parse(String.valueOf(issue.getField("created")).substring(0,String.valueOf(issue.getField("created")).length() - 9)).getTime());
        String description = issue.getDescription();
        if (description != null && !description.isEmpty()) {
            description = description.replaceAll("\n", " ").replaceAll("\r", " ");
        }
        StringBuilder comments = new StringBuilder();
        for (Comment comment : issue.getComments()) {
            comments.append(comment.getAuthor().getDisplayName())
                    .append(" : ")
                    .append(comment.getBody().replaceAll("\n", " ").replaceAll("\r", " "))
                    .append(" #### ");
        }
        csvPrinter.printRecord(
                issueType,
                assignee,
                created,
                createdEpoch,
                description,
                comments.toString()
        );
        System.out.println("Saved Issue: ");
        System.out.println(
                issueType + "," +
                assignee + "," +
                created + "," +
                createdEpoch + "," +
                description + "," +
                comments.toString());
        csvPrinter.flush();

    }
}
