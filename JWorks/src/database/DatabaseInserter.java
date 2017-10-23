package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import exceptions.DatabaseInsertException;

public class DatabaseInserter {

  /**
   * Inserts a problem into the database.
   * @param type An integer used to represent the questions type.
   * @param question The text that would be prompted to the student.
   * @param answer The answer to the question.
   * @param connection The connection to the database.
   * @return The unique key of the problem.
   * @throws DatabaseInsertException Thrown if the question could not be added to the database.
   */
  protected static int insertProblem(int type, String question, String answer, Connection connection)
      throws DatabaseInsertException {
    String sql = "INSERT INTO PROBLEMS(TYPE, QUESTION, ANSWER) VALUES(?,?,?)";
    int result = -1;
    
      PreparedStatement preparedStatement = null;
      try {
          preparedStatement = connection.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS);
          preparedStatement.setInt(1, type);
          preparedStatement.setString(2, question);
          preparedStatement.setString(3, answer);
        
        int id = 0;
        id = preparedStatement.executeUpdate();
        
        if (id > 0) {
          ResultSet uniqueKey = null;
          uniqueKey = preparedStatement.getGeneratedKeys();
          if (uniqueKey.next()) {
            result = uniqueKey.getInt(1);
          }
        }
      } catch (SQLException e) {
        String errorMessage = "Failed to insert question into the database.";
        throw new DatabaseInsertException(errorMessage);
      }
    
    return result;
  }
}