import java.time.*;
import java.util.Date;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;

class LocalExample2 {
    public enum FrequencyType {
        Once, Daily, Weekly, Monthly
    }

    public static void main(String args[]) {

        LocalExample2 test = new LocalExample2();
        String runOnDaysOfWeek = "WED";
        String months = "FEB,SEP";

        ZoneId currentZone = ZoneId.systemDefault();
        LocalDateTime startDate = LocalDateTime.of(2020, 8, 4, 16, 0, 0);

        Date sdate = Date.from(startDate.atZone(currentZone).toInstant());
        startDate = startDate.plusMonths(11);
        Date end = Date.from(startDate.atZone(currentZone).toInstant());
        System.out.println("Start Date: " + sdate);
        System.out.println("End date: " + end);

        System.out.println(
                "Next Date: " + test.nextRunDate(sdate, end, FrequencyType.Daily, 5, months, runOnDaysOfWeek, "31"));

    }

    private Date nextRunDate(Date startDate, Date endDate, FrequencyType frequencyType, Integer repeatEvery,
            String months, String runOnDaysOfWeek, String runOnDay) {

        LocalDateTime systemTime = LocalDateTime.now();
        System.out.println("System Time:" + systemTime);
        Instant start = startDate.toInstant();
        Instant end = endDate.toInstant();
        ZoneId currentZone = ZoneId.systemDefault();
        LocalDateTime LstartDate = LocalDateTime.ofInstant(start, currentZone);
        LocalDateTime LendDate = LocalDateTime.ofInstant(end, currentZone);
        LocalDateTime nextDate = LstartDate;
        String[] dayList = runOnDaysOfWeek.split(",");
        String[] monthList = months.split(",");

        switch (frequencyType) {
            case Once: {
                if (LstartDate.isAfter(systemTime))
                    return Date.from(nextDate.atZone(currentZone).toInstant());
                else
                    return null;
            }

            case Daily: {
                if (LstartDate.isAfter(systemTime))
                    return Date.from(LstartDate.atZone(currentZone).toInstant());

                long dif = ChronoUnit.DAYS.between(LstartDate, systemTime);
                long dueDays = repeatEvery - (dif % repeatEvery);

                nextDate = LstartDate.plusDays(dif + dueDays);
                if (systemTime.isAfter(nextDate))
                    nextDate = nextDate.plusDays(repeatEvery);
                if (LendDate.isAfter(nextDate))
                    return Date.from(nextDate.atZone(currentZone).toInstant());
                else
                    return null;
            }

            case Weekly: {
                int[] order = new int[dayList.length];
                for (int i = 0; i < dayList.length; i++) {
                    switch (dayList[i]) {
                        case "MON": {
                            order[i] = 1;
                            break;
                        }
                        case "TUE": {
                            order[i] = 2;
                            break;
                        }
                        case "WED": {
                            order[i] = 3;
                            break;
                        }
                        case "THU": {
                            order[i] = 4;
                            break;
                        }
                        case "FRI": {
                            order[i] = 5;
                            break;
                        }
                        case "SAT": {
                            order[i] = 6;
                            break;
                        }
                        case "SUN": {
                            order[i] = 7;
                            break;
                        }
                    }
                }

                if (LstartDate.isAfter(systemTime)) {
                    nextDate = LstartDate;
                } else {
                    LocalDateTime temp = LstartDate;
                    if (!(temp.getDayOfWeek().getValue() <= order[dayList.length - 1]))
                        temp = temp.plusWeeks(1);

                    nextDate = systemTime.withHour(LstartDate.getHour()).withMinute(LstartDate.getMinute())
                            .withSecond(LstartDate.getSecond());
                    long noOfWeeksElapsed = ChronoUnit.WEEKS.between(temp.with(DayOfWeek.MONDAY),
                            systemTime.with(DayOfWeek.SUNDAY));
                    long dueWeeks = noOfWeeksElapsed % repeatEvery;

                    if (dueWeeks == 0) {
                        LocalDateTime temp1 = systemTime.withHour(LstartDate.getHour())
                                .withMinute(LstartDate.getMinute()).withSecond(LstartDate.getSecond());
                        if ((temp1.getDayOfWeek().getValue() <= order[dayList.length - 1]))
                            nextDate = temp1;
                        else
                            nextDate = nextDate.with(DayOfWeek.MONDAY).plusWeeks(repeatEvery);
                    } else {
                        nextDate = nextDate.plusWeeks(repeatEvery - dueWeeks);
                        nextDate = nextDate.with(DayOfWeek.MONDAY);
                        nextDate = nextDate.plusDays(order[0] - 1);
                    }
                }
                
                
                while(true)
                {
                    if(Arrays.stream(dayList).anyMatch(nextDate.getDayOfWeek().name().substring(0, 3)::equals) && nextDate.isAfter(systemTime))
                    {
                        if(LendDate.isAfter(nextDate))
                            return Date.from(nextDate.atZone(currentZone).toInstant());
                        else
                            return null;
                    }

                    nextDate=nextDate.plusDays(1);
                }
            }


            case Monthly:
            {
                int j=(runOnDay=="last")?0:Integer.parseInt(runOnDay);
                if(LstartDate.isAfter(systemTime))
                    nextDate=LstartDate;
                else
                    nextDate=systemTime.withHour(LstartDate.getHour()).withMinute(LstartDate.getMinute()).withSecond(LstartDate.getSecond());
                    
                LocalDateTime temp=nextDate;

                while(true)
                {   
                    //nextDate= (j==0)?nextDate.with(TemporalAdjusters.lastDayOfMonth()) : nextDate.withDayOfMonth(Integer.parseInt(runOnDay));
                    if(Arrays.stream(monthList).anyMatch(nextDate.getMonth().name().substring(0,3)::equals) && nextDate.isAfter(systemTime) && nextDate.toLocalDate().lengthOfMonth()>=j)
                    {
                        nextDate= (j==0)?nextDate.with(TemporalAdjusters.lastDayOfMonth()) : nextDate.withDayOfMonth(Integer.parseInt(runOnDay));
                    
                        if(nextDate.isAfter(LstartDate) || nextDate==LstartDate)
                        {
                            if(LendDate.isAfter(nextDate))
                                return Date.from(nextDate.atZone(currentZone).toInstant());
                            else
                                return null;
                        }
                    }

                    nextDate=nextDate.plusMonths(1);
                    if(ChronoUnit.MONTHS.between(temp, nextDate)>12)
                        return null;
                }
            }

            default:
            {
                return null;
            }

        }
    }
}
