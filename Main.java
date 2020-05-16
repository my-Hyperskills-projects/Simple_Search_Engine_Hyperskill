package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;

public class Main {
    public static Scanner scanner = new Scanner(System.in);
    private static ArrayList<String> database = new ArrayList<>();
    public static boolean exit;
    public static HashMap<String, ArrayList<String>> mapOfResults = new HashMap<>();

    public static void main(String[] args) {
        String fileName = "";
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--data")) {
                fileName = args[i + 1];
                break;
            }
        }

        peopleDataEntering(fileName);
        findRelations();

        while (!exit) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. Find a person");
            System.out.println("2. Print all people");
            System.out.println("0. Exit");
            int option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1:
                    selectStrategy();
                    break;
                case 2:
                    printAllPeople();
                    break;
                case 0:
                    System.out.println("\nBye!");
                    exit = true;
                    break;
                default:
                    System.out.println("\nIncorrect option! Try again.");
            }
        }
    }

    public static void selectStrategy() {

        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        String strategy = scanner.nextLine();

        System.out.println("\nEnter a name or email to search all suitable people.");
        String[] keyWords = scanner.nextLine().toLowerCase().split(" ");

        HashSet<ArrayList<String>> setOfValues = new HashSet<>();

        for (String keyWord : keyWords) {
            if (mapOfResults.containsKey(keyWord)) {
                setOfValues.add(mapOfResults.get(keyWord));
            }
        }

        SearchContext context;

        switch (strategy) {
            case "ALL":
                context = new SearchContext(new ALL());
                break;
            case "ANY":
                context = new SearchContext(new ANY());
                break;
            case "NONE":
                context = new SearchContext(new NONE(database));
                break;
            default:
                System.out.println("ERROR");
                context = new SearchContext(new ANY());
        }

        context.search(setOfValues);
    }

    public static void peopleDataEntering(String fileName) {
        try {
            Scanner fileScanner = new Scanner(new File(fileName));
            database = new ArrayList<>();
            while (fileScanner.hasNext()) {
                database.add(fileScanner.nextLine());
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Exception");
        }
    }

    public static void printAllPeople() {
        System.out.println("=== List of people ===");
        for (String peopleData : database) {
            System.out.println(peopleData);
        }
    }

    public static void findRelations() {
        for (String people : database) {
            String[] data = people.toLowerCase().split(" ");   //toLowerCase
            for (String d : data) {
                ArrayList<String> value = mapOfResults.getOrDefault(d, new ArrayList<>());
                value.add(people);
                mapOfResults.put(d, value);
            }
        }
    }
}

class SearchContext {
    private SearchStrategy strategy;

    public SearchContext(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    public void search(HashSet<ArrayList<String>> setOfValues) {
        strategy.search(setOfValues);
    }
}

interface SearchStrategy {

    void search(HashSet<ArrayList<String>> setOfValues);
}

class ALL implements SearchStrategy {

    @Override
    public void search(HashSet<ArrayList<String>> setOfValues) {
        if (setOfValues.isEmpty()) return;
        HashSet<String> intersectSet = new HashSet<>((ArrayList<String>)setOfValues.toArray()[0]);
        for (ArrayList<String> values : setOfValues) {
            intersectSet.retainAll(values);
        }

        System.out.println(intersectSet.size() + " persons found:");

        for (String in : intersectSet) {
            System.out.println(in);
        }
    }
}

class ANY implements SearchStrategy {

    @Override
    public void search(HashSet<ArrayList<String>> setOfValues) {
        HashSet<String> intersectSet = new HashSet<>();
        for (ArrayList<String> values : setOfValues) {
            intersectSet.addAll(values);
        }

        System.out.println(intersectSet.size() + " persons found:");

        for (String in : intersectSet) {
            System.out.println(in);
        }
    }
}

class NONE implements SearchStrategy {
    ArrayList<String> database;

    public NONE(ArrayList<String> database) {
        this.database = database;
    }

    @Override
    public void search(HashSet<ArrayList<String>> setOfValues) {
        HashSet<String> intersectSet = new HashSet<>(database);
        for (ArrayList<String> values : setOfValues) {
            intersectSet.removeAll(values);
        }

        System.out.println(intersectSet.size() + " persons found:");

        for (String in : intersectSet) {
            System.out.println(in);
        }
    }
}