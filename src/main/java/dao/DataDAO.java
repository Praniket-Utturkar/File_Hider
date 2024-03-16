package dao;


import db.MyConnection;
import model.Data;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataDAO {
public static List<Data> getAllFiles(String email) throws SQLException {
    Connection connection = MyConnection.getConnection();
    PreparedStatement ps = connection.prepareStatement("select * from data where email = ?");
    ps.setString(1,email);
    ResultSet rs = ps.executeQuery();
    List<Data> files = new ArrayList<>();
    while (rs.next()){
        int id = rs.getInt(1);
        String name = rs.getString(2);
        String path = rs.getString(3);
        files.add(new Data(id,name,path));


    }
    return files;
}
    public static int hideFile(Data file)throws SQLException, IOException , FileNotFoundException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "insert into data(name, path, email, bin_data) values(?,?,?,?)");
        ps.setString(1, file.getFileName());
        ps.setString(2, file.getPath());
        ps.setString(3, file.getEmail());
        File f = new File(file.getPath());
        FileReader fr = new FileReader(f);
        ps.setCharacterStream(4, fr, f.length());
        int ans = ps.executeUpdate();
        System.out.println("Successfully hidden");

        fr.close();
        f.delete();
        return ans;
    }

    public static void unhide(int id) throws SQLException, IOException, FileNotFoundException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT path, bin_data FROM data WHERE id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String path = rs.getString("path");
            InputStream inputStream = rs.getBinaryStream("bin_data");

            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            fos.close();
            inputStream.close();

            ps = connection.prepareStatement("DELETE FROM data WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Successfully Unhidden");
        } else {
            System.out.println("File with ID " + id + " not found.");
        }
    }
//    public static void unhide(int id) throws SQLException, IOException ,FileNotFoundException {
//        Connection connection = MyConnection.getConnection();
//        PreparedStatement ps = connection.prepareStatement("select path, bin_data from data where id = ?");
//        ps.setInt(1, id);
//        ResultSet rs = ps.executeQuery();
//        rs.next();
//        String path = rs.getString("path");
//        Clob c = rs.getClob("bin_data");
//
//        Reader r = c.getCharacterStream();
//        FileWriter fw = new FileWriter(path);
//        int i;
//        while ((i = r.read()) != -1) {
//            fw.write((char) i);
//        }
//        fw.close();
//        ps = connection.prepareStatement("delete from data where id = ?");
//        ps.setInt(1, id);
//        ps.executeUpdate();
//        System.out.println("Successfully Unhidden");
//    }
//
}
