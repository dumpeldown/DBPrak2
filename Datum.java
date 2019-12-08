public class Datum{
    private int tag;
    private int monat;
    private int jahr;

    Datum(int tag, int monat, int jahr){
        this.tag = tag;
        this.monat = monat;
        this.jahr = jahr;
    }

    public static Datum parseDate(String input){
        String[] out = input.split("\\.");
        return new Datum(Integer.parseInt(out[0]), Integer.parseInt(out[1]), Integer.parseInt(out[2]));
    }

    private String formatDat(){
        return this.jahr + "-" + this.monat + "-" + this.tag;
    }

    public static java.sql.Date toSQLDate(Datum dat){
        return java.sql.Date.valueOf(dat.formatDat());
    }
}
