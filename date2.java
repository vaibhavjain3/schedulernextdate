

import java.time.*;
import java.util.Date;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;

class LocalExample1 {
    public enum FrequencyType {
        Once,
        Daily,
        Weekly,
        Monthly
    }



    public static void main(String args[]) {

        LocalExample1 test = new LocalExample1();
        String runOnDaysOfWeek = "THU,FRI";
        String  months="FEB,MAR,SEP";

        ZoneId currentZone = ZoneId.systemDefault();
        LocalDateTime startDate=LocalDateTime.of(2020, 8, 1, 12, 0, 0);

        Date sdate= Date.from(startDate.atZone(currentZone).toInstant());
        startDate=startDate.plusMonths(11);
        Date end=Date.from(startDate.atZone(currentZone).toInstant());
        System.out.println("Start Date: "+sdate);
        System.out.println("End date: "+end);

        System.out.println("Next Date: "+test.nextRunDate(sdate, end, FrequencyType.Weekly,2, months, runOnDaysOfWeek, "last"));

    }

    private Date nextRunDate(Date startDate, Date endDate, FrequencyType frequencyType, Integer repeatEvery, String months, String runOnDaysOfWeek, String runOnDay) {

        LocalDateTime systemTime= LocalDateTime.now();
        System.out.println("System Time:"+systemTime);
        Instant start = startDate.toInstant();
        Instant end= endDate.toInstant();
        ZoneId currentZone = ZoneId.systemDefault();
        LocalDateTime LstartDate=LocalDateTime.ofInstant(start, currentZone);
        LocalDateTime LendDate=LocalDateTime.ofInstant(end, currentZone);
        LocalDateTime nextDate=LstartDate;
        String[] dayList=runOnDaysOfWeek.split(",");
        String[] monthList=months.split(",");

        switch(frequencyType)
        {
            case Once:
            {
                if(LstartDate.isAfter(systemTime))
                    return Date.from(nextDate.atZone(currentZone).toInstant());
                else
                    return null;
            }

            case Daily:
            {
                if(LstartDate.isAfter(systemTime))
                    return Date.from(LstartDate.atZone(currentZone).toInstant());

                long dif=ChronoUnit.DAYS.between(LstartDate, systemTime);
                long  dueDays=repeatEvery- (dif % repeatEvery);

                nextDate=LstartDate.plusDays(dif+dueDays);
                if(systemTime.isAfter(nextDate))
                    nextDate=nextDate.plusDays(repeatEvery);
                if(LendDate.isAfter(nextDate))
                    return Date.from(nextDate.atZone(currentZone).toInstant());
                else
                    return null;
            }

            case Weekly:
            {
                if(LstartDate.isAfter(systemTime))
                {
                    nextDate=LstartDate;
                }
                else
                {
                    LocalDateTime temp=LstartDate;
                    while(true){
                        if(Arrays.stream(dayList).anyMatch(temp.getDayOfWeek().name().substring(0,3)::equals))
                            break;
                        if(temp.getDayOfWeek().getValue()==7){
                            temp=temp.plusDays(1);
                            break;
                        }    
                        temp=temp.plusDays(1);
                    }
                    System.out.println("temp:"+temp);
                    nextDate=systemTime.withHour(LstartDate.getHour()).withMinute(LstartDate.getMinute()).withSecond(LstartDate.getSecond());
                    long noOfWeeksElapsed=ChronoUnit.WEEKS.between(temp.with(DayOfWeek.MONDAY), systemTime.with(DayOfWeek.SUNDAY));
                    long dueWeeks=noOfWeeksElapsed % repeatEvery;
                    if(dueWeeks==0)
                    {   
                        LocalDateTime temp1 = systemTime.withHour(LstartDate.getHour()).withMinute(LstartDate.getMinute()).withSecond(LstartDate.getSecond());
                        Boolean flag=true;
                        while(temp1.getDayOfWeek().getValue()<=7 && flag)
                        {
                            //System.out.println("temp:1 "+temp1);
                            if(Arrays.stream(dayList).anyMatch(temp1.getDayOfWeek().name().substring(0,3)::equals))
                            {   System.out.println("here");
                                nextDate=temp1;
                                flag=false;
                                break;
                            }

                            if(temp1.getDayOfWeek().getValue()==7 && flag)
                            {
                                nextDate=nextDate.with(DayOfWeek.MONDAY).plusWeeks(repeatEvery);
                                flag=false;
                            }
                            temp1=temp1.plusDays(1);
                        }

                    }
                    else
                    {
                        nextDate=nextDate.plusWeeks(repeatEvery-dueWeeks);
                        nextDate=nextDate.with(DayOfWeek.MONDAY);
                    }
                }
                //System.out.println(nextDate);
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
                    //nextDate=systemTime.with(LocalTime.of(LstartDate.getHour(),LstartDate.getMinute(),LstartDate.getSecond());

                LocalDateTime temp=nextDate;

                while(true)
                {   
                    //nextDate= (j==0)?nextDate.with(TemporalAdjusters.lastDayOfMonth()) : nextDate.withDayOfMonth(Integer.parseInt(runOnDay));
                    if(Arrays.stream(monthList).anyMatch(nextDate.getMonth().name().substring(0,3)::equals) && nextDate.isAfter(systemTime) && nextDate.toLocalDate().lengthOfMonth()>=j)
                    {
                        nextDate= (j==0)?nextDate.with(TemporalAdjusters.lastDayOfMonth()) : nextDate.withDayOfMonth(Integer.parseInt(runOnDay));
                        //System.out.println(nextDate);
                        //System.out.println(LstartDate);
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
