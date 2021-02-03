package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.isen.java2.db.entities.Film;
import java.time.*;

import fr.isen.java2.db.entities.Genre;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.tuple;

public class FilmDaoTestCase {


    private FilmDao filmDao = new FilmDao();
    private GenreDao genreDao = new GenreDao();

    @Before
    public void initDb() throws Exception {
        Connection connection = DataSourceFactory.getDataSource();
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS film (\r\n"
                        + "  idfilm INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
                        + "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
                        + "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
                        + "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
        stmt.executeUpdate("DELETE FROM film");
        stmt.executeUpdate("DELETE FROM genre");
        stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
        stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
        stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
                + "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first film')");
        stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
                + "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second film')");
        stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
                + "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third film')");
        stmt.close();
        connection.close();
    }

    @Test
    public void shouldListFilms() {
        List<Film> films = filmDao.listFilms();
        List <Genre> genres = new ArrayList<>();


        for (Film f: films) {
            genres.add(f.getGenre());
        }

        assertThat(genres).hasSize(3);
        assertThat(genres).extracting("id", "name").containsOnly(tuple(1, "Drama"), tuple(2, "Comedy"), tuple(2, "Comedy"));



        // THEN
        assertThat(films).hasSize(3);

        assertThat(films).extracting("id", "title", "releaseDate", "duration", "director", "summary").containsOnly(
                tuple(1, "Title 1", LocalDate.of(2015,11,26), 120, "director 1", "summary of the first film"),
                tuple(2, "My Title 2", LocalDate.of(2015,11,14),  114, "director 2", "summary of the second film"),
                tuple(3, "Third title", LocalDate.of(2015,12,12), 176, "director 3", "summary of the third film"));
    }

    @Test
    public void shouldListFilmsByGenre() {
        List<Film> films = filmDao.listFilmsByGenre("Comedy");

        assertThat(films).hasSize(2);


        assertThat(films).extracting("id", "title", "releaseDate", "duration", "director", "summary").containsOnly(
                tuple(2, "My Title 2", LocalDate.of(2015,11,14),  114, "director 2", "summary of the second film"),
                tuple(3, "Third title", LocalDate.of(2015,12,12), 176, "director 3", "summary of the third film"));


    }

    @Test
    public void shouldAddFilm() throws Exception {

       

        Genre genre = new Genre(3,"Horror");
        filmDao.addFilm(new Film( 5,"Moi moche", LocalDate.of(2015,11,26), genre, 120, "director 1", "summary of the first film"));
        Connection connection = DataSourceFactory.getDataSource();
        Statement statement = connection.createStatement();
        ResultSet resultSetFilm = statement.executeQuery("SELECT * FROM film WHERE title='Moi moche'");


        assertThat(resultSetFilm.next()).isTrue();
        assertThat(resultSetFilm.getInt("idfilm")).isNotNull();
        assertThat(resultSetFilm.getString("title")).isEqualTo("Moi moche");
        assertThat(resultSetFilm.getInt("duration")).isEqualTo(120);
        assertThat(resultSetFilm.getString("director")).isEqualTo("director 1");
        assertThat(resultSetFilm.getString("summary")).isEqualTo("summary of the first film");
        assertThat(resultSetFilm.getDate("release_date")).isEqualTo(  java.sql.Date.valueOf((LocalDate.of(2015,11,26))));
        assertThat(resultSetFilm.getInt("genre_id")).isEqualTo(3);
        assertThat(resultSetFilm.next()).isFalse();

        resultSetFilm.close();
        statement.close();
        connection.close();



    }
}
