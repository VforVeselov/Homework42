import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class WorkJDBC {
    private static String url = "jdbc:postgresql://localhost:5432/postgres";
    private static String user = "postgres";
    private static String password = "postgres";


    public static void CreateDB() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
        ) {
            statement.execute(readSqlFile("create_tables.sql"));
        }
    }
    public static void insert() throws SQLException  {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
        ) {
            //users 1-10
            for (int i = 0; i < 10; i++) {
                String sql = "insert into users (name, password) values ('user"+i+"', 'password"+i+"');";
                statement.execute(sql);
            }
            //
            ResultSet resultSet = statement.executeQuery("select id from users;");
            List<Integer> ids = new ArrayList<Integer>();
            while (resultSet.next()) {
                ids.add(resultSet.getInt(1));
            }

            // posts
            for (int i = 0; i < 15; i++) {
                Integer userId = ids.get(new Random().nextInt(ids.size()));
                String sql = "insert into post (text, user_id) values ('randomtext', '"+userId+"');";
                statement.execute(sql);
            }
            resultSet = statement.executeQuery("select id from post;");
            List<Integer> postiIds = new ArrayList<Integer>();
            while (resultSet.next()) {
                postiIds.add(resultSet.getInt(1));
            }
           // comments
            for (int i = 0; i < 30; i++) {
                Integer userId = ids.get(new Random().nextInt(ids.size()));
                String sql = "insert into comment (text, user_id) values ('randomtext', '"+userId+"');";
                statement.execute(sql);
            }
            resultSet = statement.executeQuery("select id from comment;");
            List<Integer> commentsIds = new ArrayList<Integer>();
            while (resultSet.next()) {
                commentsIds.add(resultSet.getInt(1));
            }
            //likes
            for (int i = 0; i < 45; i++) {
                Integer userId = ids.get(new Random().nextInt(ids.size()));
                Integer postID = postiIds.get(new Random().nextInt(postiIds.size()));
                Integer commentID = commentsIds.get(new Random().nextInt(commentsIds.size()));
                String sql = "";
                if (new Random().nextBoolean()) {
                    sql = "insert into likes (user_id, post_id) values ('" + userId+ "', '" + postID + "');";
                } else {
                    sql = "insert into likes (user_id, post_id) values ('" + userId+ "', '" + commentID + "');";
                }
                statement.execute(sql);
            }

        }
    }
    public static void showCounts() {
        String sql = "select (select count(*) from users) as users_count,"+
        " (select count(*) from likes) as likes_count,"+
        " (select count(*) from post) as post_count,"+
        " (select count(*) from comment) as comments_count;";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
        ) {
            ResultSet resultSet = statement.executeQuery(sql);
            printAllDataFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public static void showUser(Integer id) {
        String sql = "select name, created_at from users where id = "+id;
        String sql2 = "select * from post where user_id = "+id+" order by created_at limit 1;";
        String sql3 = "select COUNT(*) as comments_count from comment where user_id ="+id;
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
        ) {
            ResultSet resultSet = statement.executeQuery(sql);
            printAllDataFromResultSet(resultSet);
            resultSet = statement.executeQuery(sql2);
            printAllDataFromResultSet(resultSet);
            resultSet = statement.executeQuery(sql3);
            printAllDataFromResultSet(resultSet);
        } catch (SQLException e) {
            System.out.println("usernotfound");
            throw new RuntimeException(e);
        }
    }

    public static void printAllDataFromResultSet(ResultSet resultSet) throws SQLException {
        printColumnNames(resultSet);
        while (resultSet.next()) {
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount ; i++) {
                System.out.printf("%s | ", resultSet.getString(i));
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void printColumnNames(ResultSet resultSet) throws SQLException {
        int columnCount = resultSet.getMetaData().getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            System.out.printf("%s | ", resultSet.getMetaData().getColumnName(i));
        }
        System.out.println("\n--------------------------");
    }

    public static String readSqlFile(String filename) {
        InputStream resource = WorkJDBC.class.getClassLoader().getResourceAsStream(filename);
        return new BufferedReader(new InputStreamReader(resource)).lines().collect(Collectors.joining(""));
    }
}
