package vn.gpay.gsmart.core.api.timesheet_lunch;

public final class ExcelRiceDTO {
    private String date;
    private int numberOfMeals;

    public ExcelRiceDTO(String date, int numberOfMeals) {
        this.date = date;
        this.numberOfMeals = numberOfMeals;
    }

    public String getDate() {
        return date;
    }

    public int getNumberOfMeals() {
        return numberOfMeals;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNumberOfMeals(int numberOfMeals) {
        this.numberOfMeals = numberOfMeals;
    }
}
