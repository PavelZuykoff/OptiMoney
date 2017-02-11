package pavelzuykoff.optimoney;

import java.util.Calendar;


/**
 * Created by novem on 13.08.2016.
 */
public class CalendarValuesHandler {

    public Calendar today = Calendar.getInstance();
    public int currentDay = today.get(Calendar.DATE);
    public int currentMonth = today.get(Calendar.MONTH);
    public int currentYear = today.get(Calendar.YEAR);

    String monthString = "";
    String dateToStringValue = "";

    protected String getCurrentDateStringFormat() {



        switch (currentMonth) {
            case 0:
                monthString = "Января";
                break;
            case 1:
                monthString = "Февраля";
                break;
            case 2:
                monthString = "Марта";
                break;
            case 3:
                monthString = "Апреля";
                break;
            case 4:
                monthString = "Мая";
                break;
            case 5:
                monthString = "Июня";
                break;
            case 6:
                monthString = "Июля";
                break;
            case 7:
                monthString = "Августа";
                break;
            case 8:
                monthString = "Сентября";
                break;
            case 9:
                monthString = "Октября";
                break;
            case 10:
                monthString = "Ноября";
                break;
            case 11:
                monthString = "Декабря";
                break;
            default:
                monthString = "ОШИБКА";

        }

        dateToStringValue = currentDay + " " + monthString + " " + currentYear;

        return dateToStringValue;

    }


    protected String getChosenDateStringFormat(int day, int month, int year) {

        switch (month) {
            case 0:
                monthString = "Января";
                break;
            case 1:
                monthString = "Февраля";
                break;
            case 2:
                monthString = "Марта";
                break;
            case 3:
                monthString = "Апреля";
                break;
            case 4:
                monthString = "Мая";
                break;
            case 5:
                monthString = "Июня";
                break;
            case 6:
                monthString = "Июля";
                break;
            case 7:
                monthString = "Августа";
                break;
            case 8:
                monthString = "Сентября";
                break;
            case 9:
                monthString = "Октября";
                break;
            case 10:
                monthString = "Ноября";
                break;
            case 11:
                monthString = "Декабря";
                break;
            default:
                monthString = "ОШИБКА";

        }

        dateToStringValue = day + " " + monthString + " " + year;

        return dateToStringValue;

    }


}
