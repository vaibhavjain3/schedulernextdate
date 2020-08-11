import java.text.DateFormatSymbols;
import java.time.*;
import java.util.Date;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;

class LocalExample4
 {
    public enum FrequencyType 
    {
        Once, Daily, Weekly, Monthly
    }

    public enum ShortDays
    {
        SUN(1),MON(2),TUE(3),WED(4),THU(5),FRI(6),SAT(7);
        int value;
        private ShortDays(int n)
        {
            value=n;
        }
        int Value(){
            return value;
        }
    }

    public enum ShortMonths
    {
        JAN(1),FEB(2),MAR(3),APR(4),MAY(5),JUN(6),JUL(7),AUG(8),SEP(9),OCT(10),NOV(11),DEC(12);
        int value;
        private ShortMonths(int n)
        {
            value =n;
        }
        int Value(){
            return value;
        }
    }
    public static void main(String args[])
    {

        LocalExample4 test = new LocalExample4();
        String runOnDaysOfWeek = "SUN,MON,WED,";
        String months = "FEB,AUG,DEC";

        ZoneId currentZone = ZoneId.systemDefault();
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 6, 23, 0, 0);

        Date sdate = Date.from(startDate.atZone(currentZone).toInstant());
        startDate = startDate.plusMonths(11);
        Date end = Date.from(startDate.atZone(currentZone).toInstant());
        System.out.println("Start Date: " + sdate);
        System.out.println("End date: " + end);

        System.out.println("Next Date: " + test.nextRunDate(sdate, end, FrequencyType.Monthly, 1, months, runOnDaysOfWeek, "last"));
    }

    private Date nextRunDate(Date startDate, Date endDate, FrequencyType frequencyType, int repeatEvery,String months, String runOnDaysOfWeek, String runOnDay) 
    {

        Calendar systemTime = Calendar.getInstance();
        Calendar startDateC = Calendar.getInstance();
        startDateC.setTime(startDate);
        Calendar endDateC = Calendar.getInstance();
        endDateC.setTime(endDate);
        Calendar nextDate = Calendar.getInstance();
        String[] dayList = runOnDaysOfWeek.split(",");
        String[] monthList = months.split(",");
        
        switch (frequencyType)
         {
            case Once: 
            {
                if (startDateC.after(systemTime))
                    return startDateC.getTime();
                else
                    return null;
            }

            case Daily: 
            {
                if (startDateC.after(systemTime))
                    return startDateC.getTime();

                long diff = ChronoUnit.DAYS.between(startDateC.toInstant(), systemTime.toInstant());
                long dueDays = repeatEvery - (diff % repeatEvery);

                startDateC.add(Calendar.DAY_OF_WEEK, (int)diff + (int)dueDays);
                nextDate=startDateC;
                if (systemTime.after(nextDate))
                    nextDate.add(Calendar.DAY_OF_WEEK,repeatEvery);
                if (endDateC.after(nextDate))
                    return nextDate.getTime();
                else
                    return null;
            }

            case Weekly:
             {
                int[] order = new int[dayList.length];
                int index=0;
                // Creating a order array based on runOnDaysOfWeek
                for(ShortDays days:ShortDays.values())
                {   
                    if(index<dayList.length && days.name().contains(dayList[index]))
                    {   
                        order[index]=days.Value();
                        index+=1;
                    }
                }

                if (startDateC.after(systemTime)) 
                {   
                    Date assign = startDateC.getTime();
                    nextDate.setTime(assign);

                    // Now to 2 cases arise
                    // If startDate is on Day which is after the last day on dayList
                    if(nextDate.get(Calendar.DAY_OF_WEEK) > order[dayList.length - 1])
                    {
                        nextDate.add(Calendar.WEEK_OF_MONTH,1);
                        nextDate.set(Calendar.DAY_OF_WEEK,order[0]);
                    }
                    // If startDate is before or on the last day of dayList
                    else
                    {
                        Calendar localInstanceOfnextDate = nextDate;
                        int dayValue = Arrays.stream(order).filter(x -> x >= localInstanceOfnextDate.get(Calendar.DAY_OF_WEEK)).findFirst().getAsInt();
                        nextDate.set(Calendar.DAY_OF_WEEK,dayValue);
                    }      
                } 
                else
                {
                    Calendar tempStartDate = Calendar.getInstance();
                    Date assign = startDateC.getTime();
                    tempStartDate.setTime(assign);

                    // Finding the week from which task stated executing.
                    if (!(tempStartDate.get(Calendar.DAY_OF_WEEK) <= order[dayList.length - 1]))
                        tempStartDate.add(Calendar.WEEK_OF_MONTH,1);

                    nextDate.set(Calendar.HOUR_OF_DAY,startDateC.get(Calendar.HOUR_OF_DAY));
                    nextDate.set(Calendar.MINUTE,startDateC.get(Calendar.MINUTE));
                    nextDate.set(Calendar.SECOND,startDateC.get(Calendar.SECOND));

                    Calendar tempSystemTime= Calendar.getInstance();
                    
                    tempStartDate.set(Calendar.DAY_OF_WEEK,1);
                    tempSystemTime.set(Calendar.DAY_OF_WEEK,7);
        
                    long milliseconds1 = tempStartDate.getTimeInMillis();
                    long milliseconds2 = tempSystemTime.getTimeInMillis();
                    long diff = milliseconds2 - milliseconds1;
                    int noOfWeeksElapsed = (int)diff / (7*24 * 60 * 60 * 1000);
                    int dueWeeks = noOfWeeksElapsed % repeatEvery;
                    
                    // Now 2 Cases arise
                    // If dueWeeks = 0, i.e currentWeek is eligible for task execution . 
                    if (dueWeeks == 0) 
                    {    
                        tempSystemTime=Calendar.getInstance();
                        tempSystemTime.set(Calendar.HOUR_OF_DAY,startDateC.get(Calendar.HOUR_OF_DAY));
                        tempSystemTime.set(Calendar.MINUTE,startDateC.get(Calendar.MINUTE));
                        tempSystemTime.set(Calendar.SECOND,startDateC.get(Calendar.SECOND));

                        // Check if there still days left in the currentWeek that are in the daysList
                        if ((tempSystemTime.get(Calendar.DAY_OF_WEEK) <= order[dayList.length - 1]))
                        {   
                            if(tempSystemTime.after(systemTime))
                            {
                                Date assignLocal = tempSystemTime.getTime();  
                                nextDate.setTime(assignLocal);
                            }
                            // If current day is before system and current day is the last day in the daysList. So we need to skip to the eligible week   
                            else if(tempSystemTime.get(Calendar.DAY_OF_WEEK)==order[dayList.length-1])
                            {
                                tempSystemTime.add(Calendar.WEEK_OF_MONTH,repeatEvery);
                                tempSystemTime.set(Calendar.DAY_OF_WEEK,order[0]);
                                Date assignLocal = tempSystemTime.getTime();
                                nextDate.setTime(assignLocal); 
                            }
                            // If there are still days left in current week that are in the daysList. 
                            else
                            {
                                tempSystemTime.add(Calendar.DAY_OF_WEEK,1);
                                Calendar localInstanceOfnextDate= tempSystemTime;
                                int dayValue = Arrays.stream(order).filter(x -> x >= localInstanceOfnextDate.get(Calendar.DAY_OF_WEEK)).findFirst().getAsInt();
                                tempSystemTime.set(Calendar.DAY_OF_WEEK,dayValue);
                                Date assignLocal = tempSystemTime.getTime();
                                nextDate.setTime(assignLocal);                   
                            }                                               
                        }
                        // If no days are left in the current eligible week so we need to skip to the next eligible week. 
                        else
                        {
                            nextDate.set(Calendar.DAY_OF_WEEK,order[0]);
                            nextDate.add(Calendar.WEEK_OF_MONTH, repeatEvery);
                        }        
                    } 
                    // Current Week is not eligible for Execution. So go to the eligible week by adding the remaining weeks.
                    else 
                    {
                        nextDate.add(Calendar.WEEK_OF_MONTH, (int)repeatEvery - (int)dueWeeks);
                        nextDate.set(Calendar.DAY_OF_WEEK, order[0]);       
                    }
                }
                
                if(endDateC.after(nextDate))
                    return nextDate.getTime();
                else
                    return null;
            }

            case Monthly:
            {   

                if(startDateC.after(systemTime))
                {
                    Date assign = startDateC.getTime();
                    nextDate.setTime(assign);
                }
                else
                {
                    Date assign = systemTime.getTime();
                    nextDate.setTime(assign);
                    nextDate.set(Calendar.HOUR_OF_DAY,startDateC.get(Calendar.HOUR_OF_DAY));
                    nextDate.set(Calendar.MINUTE,startDateC.get(Calendar.MINUTE));
                    nextDate.set(Calendar.SECOND,startDateC.get(Calendar.SECOND));         
                }
                
                int iter=0;

                // Initiate dayOfMonth Value based on runOnday: if "last" dayOfMonth=0; if "any other Integer" dayOfMonth= that value
                int dayOfMonth = (runOnDay=="last")?0:Integer.parseInt(runOnDay);

                // Generally below loop won't iterate more than 12 times except on one edge case.
                // This edge case happens when runOnDay is "31" and there is no month is Months List that has "31" days. 
                // In this case loop wont break, so we need to break after 12 iterations.
                while(iter<13)
                {   
                    // Checking 3 conditons; 1: current month is the list, 2: current time is after system time 3: if current month can take runOnDay value 
                    // i.e if runOnDay is 31 but February can never take a 31st day. 
                    String month = new DateFormatSymbols().getShortMonths()[nextDate.get(Calendar.MONTH)];
        
                    if(Arrays.stream(monthList).anyMatch(month.toUpperCase()::equals) && nextDate.after(systemTime) && nextDate.getActualMaximum(Calendar.DAY_OF_MONTH)>=dayOfMonth)
                        {   
                            // Setting the day of current month to the runOnDay value 
                            if(dayOfMonth==0)
                                nextDate.set(Calendar.DAY_OF_MONTH,nextDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                            else
                                nextDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);   
                        
                         // Since we are using commom loop for both cases i.e startDate after systemTine and startDate before systemTime we need to check 
                         // if current Date is afer startDate. We have already checked the startDate after systemTime condition above.
                         if(nextDate.after(startDateC) || nextDate.equals(startDateC))
                         {
                            if(endDateC.after(nextDate))
                                return nextDate.getTime();
                            else
                                return null;
                         }
                     }
                    // Increase the month by 1 and check 
                    nextDate.add(Calendar.MONTH,1);
                    iter+=1;
                } 
                return null;          
            }

            default:
            {
                return null;
            }

        }
    }
}
