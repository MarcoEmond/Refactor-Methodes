package main.service;

public class ScheduleService {
    private final int[] departuresByDay = {4, 7, 3, 8, 12, 6, 2};

    //replaced switch case to formula
    public double createMaskByDayOfWeek(int dayOfTheWeek) {
        double mask = Math.pow(2, dayOfTheWeek - 1);
        return mask;
    }

    public boolean runsOnFriday(int mask) {
        return (mask & 16) != 0;
    }

    public int getDepartures(int day) {
        try {
            return departuresByDay[day];
        } catch (ArrayIndexOutOfBoundsException exception) {
            return 0;
        }
    }
}
