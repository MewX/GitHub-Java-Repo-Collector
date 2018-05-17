package au.edu.uofa.sei.assignment2;

import java.util.*;

public class WeekCount {
    int weekIndex;
    Set<Integer> activeDays;
    Set<String> projects;
    Map<String, Integer> projectCommits;
    Map<Integer, Set<String>> projectPerDay;
    int numberOfCommits;
    int numberOfFiles;
    int numberOfAdding;
    int numberOfDeleting;

    WeekCount(int weekIndex) {
        this.weekIndex = weekIndex;

        activeDays = new HashSet<>();
        projects = new HashSet<>();
        projectCommits = new HashMap<>();
        projectPerDay = new HashMap<>();

        numberOfCommits = 0;
        numberOfFiles = 0;
        numberOfAdding = 0;
        numberOfDeleting = 0;
    }

    public float getAverageProjectPerDay() {
        // count total project in a week
        int count = 0;
        for (Map.Entry<Integer, Set<String>> entry : projectPerDay.entrySet()) {
            count += entry.getValue().size();
        }

        return (float) count / activeDays.size();
    }

    public float getsFocus() {
        List<String> projectList = new ArrayList<>(projects);

        float result = 0;

        for (String tempProject : projectList) {
            int commits = projectCommits.get(tempProject);

            float pi = (float) commits / numberOfCommits;
            result += pi * log2(pi);
        }

        return result == 0 ? 0 : result * -1;
    }

    private double log2(double num) {
        return Math.log(num) / Math.log(2);
    }
}
