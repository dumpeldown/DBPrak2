
import java.sql.*;

public class ConnectionManager{
    public static final Connection con = connect("xxxx", "xxxx");

    /**
     * Verbindung mit der Datenbank.
     *
     * @return Connection Object
     */
    private static Connection connect(String user, String pass){
        String url = "jdbc:postgresql://feuerbach.nt.fh-koeln.de/postgres?user=" + user + "&password=" + pass;
        Connection dbConnection = null;
        // Treiber laden
        try{
            Class.forName("org.postgresql.Driver");
        }catch(Exception e){
            System.out.println("Fehler beim Laden des Treibers: " + e.getMessage());
        }

        // Erstellung Datenbank-Verbindungsinstanz
        try{
            dbConnection = DriverManager.getConnection(url);
        }catch(SQLException e){
            System.out.println("Fehler beim Verbindungsaufbau zur Datenbank!");
            System.out.println(e.getMessage());
        }
        if(dbConnection == null){
            System.exit(-1);
        }
        return dbConnection;
    }

    /**
     * Schließt Connection zur Datenbank.
     */
    public static void closeConnection(){
        try{
            con.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
