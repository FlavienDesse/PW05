package fr.isen.java2.db.daos;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

public class GenreDao {


    public List<Genre> listGenres() {
        List<Genre> listOfBooks = new ArrayList<>();
        try (Connection connection = DataSourceFactory.getDataSource()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre")) {
                try (ResultSet results = statement.executeQuery()) {
                    while (results.next()) {
                        Genre book = new Genre(results.getInt("idgenre"), results.getString("name"));
                        listOfBooks.add(book);
                    }
                    results.close();
                    statement.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listOfBooks;
    }


    public Genre getGenre(String name) {
        try (Connection connection = DataSourceFactory.getDataSource()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name = ?")) {
                statement.setString(1, name);
                try (ResultSet results = statement.executeQuery()) {
                    if (results.next()) {
                        return new Genre(results.getInt("idgenre"),results.getString("name"));
                    }
                    results.close();
                    statement.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addGenre(String name) {

		try (Connection connection = DataSourceFactory.getDataSource()) {
			String sqlQuery = "insert into genre(name) "+"VALUES(?)";
			try (PreparedStatement statement = connection.prepareStatement(
					sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, name);
				statement.executeUpdate();
            }
		}catch (SQLException e) {
			// Manage Exception
			e.printStackTrace();
		}

    }


}
