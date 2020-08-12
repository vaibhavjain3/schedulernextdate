import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

enum days{
    SUN, MON, TUE, WED, THU, FRI, SAT
}
enum month{
    JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC
}
public class TEMP {
    public static void main(String[] args) {
        Date obj = new Date(2020 - 1900, 7, 19, 00, 50, 4);
        Date obj1 = new Date(2021 - 1900, 7, 9, 8, 50, 4);
        System.out.println(fetchNextRunDate(obj, obj1, FrequencyType.Weekly ,7, "MAR,AUG,NOV", "SUN,MON,WED", "13"));
    }
    private static Date fetchNextRunDate(Date startDate, Date endDate, FrequencyType frequencyType, Integer repeatEvery, String months, String runOnDaysOfWeek, String runOnDay) {
        Calendar start = Calendar.getInstance(), end = Calendar.getInstance(), current = Calendar.getInstance(), next = Calendar.getInstance();
        start.setTime(startDate);
        end.setTime(endDate);
        next.setTime(current.getTime());
        next.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY));
        next.set(Calendar.MINUTE, start.get(Calendar.MINUTE));
        next.set(Calendar.SECOND, start.get(Calendar.SECOND));
        switch (frequencyType) {
            case Once:
                return start.after(current) && start.before(end) ? startDate : null;
            case Daily:
                long time1 = start.get(Calendar.HOUR_OF_DAY) * 60 * 60 + start.get(Calendar.MINUTE) * 60 + start.get(Calendar.SECOND);
                long time2 = current.get(Calendar.HOUR_OF_DAY) * 60 * 60 + current.get(Calendar.MINUTE) * 60 + current.get(Calendar.SECOND);
                if (start.after(current))
                    return startDate;
                if (repeatEvery != 0) {
                    int diff = (int)(ChronoUnit.DAYS.between(start.toInstant(), current.toInstant()) + (time1 > time2 ? 1 : 0)) % repeatEvery;
                    System.out.println(diff);
                    if (diff != 0)
                        next.add(Calendar.DAY_OF_MONTH, repeatEvery - diff);
                    System.out.println(next.getTime());
                    if (next.before(end) && next.after(current))
                        return next.getTime();
                    else {
                        next.add(Calendar.DAY_OF_MONTH, repeatEvery);
                        if (next.before(end)) return next.getTime();
                    }
                }
            case Weekly:
                int diff_start = -1, diff_current = -1;
                List < String > day = Arrays.asList(days.values()).stream().map(item -> item.toString()).collect(Collectors.toList());
                List < String > weeks = Arrays.asList(runOnDaysOfWeek.split(","));
                LocalDate start_date = new java.sql.Date(startDate.getTime()).toLocalDate();
                LocalDate end_date = new java.sql.Date(new Date().getTime()).toLocalDate();
                int due_week = (int) ChronoUnit.WEEKS.between(start_date, end_date) % repeatEvery;
                int prefix = day.indexOf(weeks.get(0)) + 1;
                for (String week: weeks) {
                    if (day.indexOf(week) >= start.get(Calendar.DAY_OF_WEEK) - 1)
                        diff_start = day.indexOf(week) - start.get(Calendar.DAY_OF_WEEK) + 1;
                    if (day.indexOf(week) > current.get(Calendar.DAY_OF_WEEK) - 1)
                        diff_current = day.indexOf(week) - current.get(Calendar.DAY_OF_WEEK) + 1;
                    if (diff_current != -1 && diff_start != -1)
                        break;
                }
                if (start.before(current)) {
                    if (due_week == 0) {
                        if (diff_current != -1) {
                            next.add(Calendar.DAY_OF_MONTH, diff_current);
                            return next.before(end) ? next.getTime() : null;
                        } else {
                            next.add(Calendar.DAY_OF_MONTH, 6 - current.get(Calendar.DAY_OF_WEEK) + 1 + prefix);
                            return next.before(end) ? next.getTime() : null;
                        }
                    } else {
                        if (diff_start != -1)
                            next.add(Calendar.DAY_OF_MONTH, prefix - 1 + (repeatEvery - due_week) * 7 - current.get(Calendar.DAY_OF_WEEK) + 1);
                        else
                            next.add(Calendar.DAY_OF_MONTH, prefix - 1 + (repeatEvery - due_week + 1) * 7 - current.get(Calendar.DAY_OF_WEEK) + 1);
                        return next.getTime();
                    }
                }
                if (start.after(current)) {
                    if (weeks.contains(day.get(start.get(Calendar.DAY_OF_WEEK) - 1)) && start.before(end))
                        return startDate;
                    if (diff_start != -1) {
                        start.add(Calendar.DAY_OF_MONTH, diff_start);
                        return start.before(end) ? start.getTime() : null;
                    } else {
                        start.add(Calendar.DAY_OF_MONTH, 6 - startDate.getDay() + prefix);
                        return start.before(end) ? start.getTime() : null;
                    }
                }
                break;
            case Monthly:
                List<String> month_item = Arrays.asList(month.values()).stream().map(item -> item.toString()).collect(Collectors.toList());
                List<String> month_list = Arrays.asList(months.split(","));
                int index =-1;
                if(start.after(current))
                    next.setTime(startDate);
                else
                    next.setTime(current.getTime());
                Calendar base = Calendar.getInstance();
                base.setTime(next.getTime());
                base.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY));
                base.set(Calendar.MINUTE,start.get(Calendar.MINUTE));
                base.set(Calendar.SECOND, start.get(Calendar.SECOND));
                for (String item : month_list) {
                    if (month_item.indexOf(item) > next.get(Calendar.MONTH)) {
                        index = month_item.indexOf(item);
                    }
                    if (month_item.indexOf(item) ==  next.get(Calendar.MONTH)) {
                        if (runOnDay.equalsIgnoreCase("last"))
                            base.set(Calendar.DAY_OF_MONTH,next.getActualMaximum(Calendar.DAY_OF_MONTH));
                        else
                            base.set(Calendar.DAY_OF_MONTH,Integer.parseInt(runOnDay));
                        if(base.after(next)&&base.before(end))
                            return base.getTime();
                    }
                }
                if(index!=-1)
                {
                    if (runOnDay.equalsIgnoreCase("last")) {
                        base.set(Calendar.MONTH,index);
                        base.set(Calendar.DAY_OF_MONTH, base.getActualMaximum(Calendar.DAY_OF_MONTH));
                    }
                    else {
                        base.set(Calendar.DAY_OF_MONTH, Integer.parseInt(runOnDay));
                        base.set(Calendar.MONTH,index);
                    }
                    if(base.before(end))
                        return base.getTime();
                }
                base.set(Calendar.MONTH,month_item.indexOf(month_list.get(0)));
                base.set(Calendar.YEAR,current.get(Calendar.YEAR)+1);
                if(runOnDay.equalsIgnoreCase("last"))
                    base.set(Calendar.DAY_OF_MONTH,base.getActualMaximum(Calendar.DAY_OF_MONTH));
                else
                    base.set(Calendar.DAY_OF_MONTH,Integer.parseInt(runOnDay));
                if(base.before(end))
                    return  base.getTime();
        }
        return null;
    }
}