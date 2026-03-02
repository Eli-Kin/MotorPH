package com.motorph;
import java.io.*;
import java.security.Key;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("-".repeat(100));

        System.out.println("███╗   ███╗ ██████╗ ████████╗ ██████╗ ██████╗ ██████╗ ██╗  ██╗");
        System.out.println("████╗ ████║██╔═══██╗╚══██╔══╝██╔═══██╗██╔══██╗██╔══██╗██║  ██║");
        System.out.println("██╔████╔██║██║   ██║   ██║   ██║   ██║██████╔╝██████╔╝███████║");
        System.out.println("██║╚██╔╝██║██║   ██║   ██║   ██║   ██║██╔══██╗██╔═══╝ ██╔══██║");
        System.out.println("██║ ╚═╝ ██║╚██████╔╝   ██║   ╚██████╔╝██║  ██║██║     ██║  ██║");
        System.out.println("╚═╝     ╚═╝ ╚═════╝    ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝  ╚═╝");

        System.out.println("-".repeat(100));

        String attendanceFile = "src\\data_attendance.csv";
        String infoFile = "src\\data_info.csv";
        String attendanceLine;
        String infoLine;
        String[] infoRow = {};
        String[] attendanceRow = {};

        String input;
        boolean appRunning = true;
        boolean inEmployees = false;

        BufferedReader attendanceReader = null;
        BufferedReader infoReader = null;
        Scanner sc = new Scanner(System.in);

        List<Integer> ids = new ArrayList<>();
        List<String> infos = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> birthdays = new ArrayList<>();

        HashMap<Integer, List<String>> date = new HashMap<>();
        HashMap<Integer, List<String>> in = new HashMap<>();
        HashMap<Integer, List<String>> out = new HashMap<>();

        //Get the data
        try {
            attendanceReader = new BufferedReader(new FileReader(attendanceFile));
            infoReader = new BufferedReader(new FileReader(infoFile));

            infoReader.readLine(); // skip header

            while(((infoLine = infoReader.readLine()) != null)){
                infoRow = infoLine.split(",");

                //store ids, names, and birthdays in array
                ids.add(Integer.parseInt(infoRow[0])); //convert to int

                //store infos in array
                String name = infoRow[1] + " " + infoRow[2];
                String formatted = String.format("%-25s %-25s", name, infoRow[3]);
                infos.add(formatted);
                names.add(name);
                birthdays.add(infoRow[3]);
            }

            attendanceReader.readLine(); // skip header
            while((attendanceLine = attendanceReader.readLine()) != null){
                attendanceRow = attendanceLine.split(",");
                int id = Integer.parseInt(attendanceRow[0]);
                String attendanceDate = attendanceRow[3];
                String attendanceIn = attendanceRow[4];
                String attendanceOut = attendanceRow[5];

                //if id doesn't exist, create a new list, if it does exist then ignore
                date.putIfAbsent(id, new ArrayList<>());
                in.putIfAbsent(id, new ArrayList<>());
                out.putIfAbsent(id, new ArrayList<>());
                //the csv file is sorted with the id.
                //the csv file contain multiple identical id, so using putifabsent, the creation of a new list will be prevented if the id exist.
                //while the id is still the same in the loop, the same list will be used.
                //once a new id appear, a list will be replaced with a new one.

                //store the date to the list for that ID
                date.get(id).add(attendanceDate);
                in.get(id).add(attendanceIn);
                out.get(id).add(attendanceOut);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                attendanceReader.close();
                infoReader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //store ids as keys and infos as value in hashmap
        HashMap<Integer, String> employees = new HashMap<>();
        HashMap<Integer, String> employeesNames = new HashMap<>();
        HashMap<Integer, String> employeesBirthdays = new HashMap<>();
        for(int i = 0; i <= ids.size() - 1; i++){
            employees.put(ids.get(i), infos.get(i));
            employeesNames.put(ids.get(i), names.get(i));
            employeesBirthdays.put(ids.get(i), birthdays.get(i));
        }

        //display header
        System.out.printf("%-8s %-25s %-25s", "ID", "Name", "Birthday");
        System.out.println();
        //display hashmap data
        for (Map.Entry<Integer, String> entry : employees.entrySet()) {
            System.out.printf("%-8d %-20s%n", entry.getKey(), entry.getValue());
        }

        do {
            System.out.println("-".repeat(100));
            System.out.print("Enter Employee's ID: ");
            input = sc.next();

            if (isInteger(input)) {
                int id = Integer.parseInt(input); // convert input to int
                long totalSeconds = 0;

                // get the list for this key
                List<String> dateList = date.get(id);
                List<String> inList = in.get(id);
                List<String> outList = out.get(id);

                // Loop through each pair
                for (int i = 0; i < inList.size(); i++) {
                    //every iteration a new array is created
                    String[] inParts = inList.get(i).split(":"); //split the data within :
                    String[] outParts = outList.get(i).split(":");

                    int inHour = Integer.parseInt(inParts[0]);
                    int inMinute = Integer.parseInt(inParts[1]);
                    int outHour = Integer.parseInt(outParts[0]);
                    int outMinute = Integer.parseInt(outParts[1]);

                    //sum the seconds every loop
                    totalSeconds += hourBetweenLog(inHour, inMinute, outHour, outMinute);
                }
                if (employees.containsKey(id)) {
                    String alert = "";
                    do {
                        inEmployees = true;
                        if(employeesNames.containsKey(id) && employeesBirthdays.containsKey(id)){
                            System.out.println("-".repeat(100));
                            System.out.println("ID: " + id);
                            System.out.println("Name: " + employeesNames.get(id));
                            System.out.println("Birthday: " + employeesBirthdays.get(id));
                            System.out.println("Total Hours: " + secondsToTime(totalSeconds));
                        } else {
                            System.out.println("ID seems to not match with either the Name or the Birthday.");
                        }

                        System.out.println("-".repeat(100));
                        System.out.println("Enter a to show attendance.");
                        System.out.println("Enter e to go back.");
                        System.out.println(alert);
                        input = sc.next();

                        if (Objects.equals(input, "e")){
                            inEmployees = false;
                            alert = "";
                        }
                        if (Objects.equals(input, "a")){
                            if (date.containsKey(id)) {
                                //display header
                                System.out.printf("%-14s %-9s %-9s", "Date", "In", "Out");
                                System.out.println();
                                for(int i = 0; i <= dateList.toArray().length - 1; i++){
                                    System.out.printf("%-14s %-9s %-9s", dateList.get(i), inList.get(i), outList.get(i));
                                    System.out.println();
                                }

                            } else {
                                System.out.println("No data found for ID " + id);
                            }
                        } else {
                            alert = "Please input either \"e\" or \"a\".";
                            continue;
                        }
                    } while (inEmployees == true);

                } else {
                    System.out.println("ID not found in employees.");
                }
            } else {
                System.out.println("Please only input a number.");
            }

        } while (appRunning);
    }

    //return true if the value can be converted to an int, if not then return false
    static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Integer.parseInt(str); //convert string into an int
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //Calculate the hours between log in and log out
    static long hourBetweenLog(int inHour, int inMinute, int outHour, int outMinute) {
        LocalTime logIn = LocalTime.of(inHour, inMinute);
        LocalTime logOut = LocalTime.of(outHour, outMinute);

        // only count 8:00 AM to 5:00 PM
        if (logIn.isBefore(LocalTime.of(8, 0))) {
            logIn = LocalTime.of(8, 0);
        }
        if (logOut.isAfter(LocalTime.of(17, 0))) {
            logOut = LocalTime.of(17, 0);
        }

        return Duration.between(logIn, logOut).getSeconds();
    }

    static String secondsToTime(long totalSeconds) {
        long hours = totalSeconds / 3600; //convert second into hour
        long remainingSecondsAfterHours = totalSeconds % 3600; //get the remainder after hour
        long minutes = remainingSecondsAfterHours / 60; //convert the remaining seconds into minutes

        System.out.println();
        return hours + "h " + minutes + "m ";
    }
}