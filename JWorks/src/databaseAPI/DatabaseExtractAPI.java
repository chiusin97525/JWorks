package databaseAPI;

import database.DatabaseSelector;
import exceptions.DatabaseInsertException;
import exceptions.DatabaseSelectException;
import io.OutputGenerator;
import models.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DatabaseExtractAPI extends DatabaseSelector implements DatabaseAPI{
    @Override
    public void actOnDatabase(int actObj, String[] args) {
        //to send message to UI
        OutputGenerator outTo = new OutputGenerator();
        try {
            Connection connection = DatabaseDriverAPI.connectOrCreateDataBase();
            // the query result will always be parsed into an Array list
            List<String[]> problems = new ArrayList<String[]>();
            // stores value returned from respective table
            ResultSet results;
            // store metadata for corresponding ResultSet
            ResultSetMetaData rsmd;
            switch (actObj){
                // when a request is made to return all problems
                case 1:
                    results = DatabaseSelector.getAllProblems(connection);
                    rsmd = results.getMetaData();
                    formatResult(results, rsmd, problems);
                    outTo.problemSetOutput(problems);
                    break;
                case 2:
                    results = DatabaseSelector.getProblemSet(Integer.parseInt(args[0]), connection);
                    rsmd = results.getMetaData();
                    formatResult(results, rsmd, problems);
                    outTo.problemSetOutput(problems);
                    break;
                case 3:
                    results = DatabaseSelector.getStudent(Integer.parseInt(args[0]), connection);
                    rsmd = results.getMetaData();
                    if (results.isClosed()){
                        throw new DatabaseSelectException("student with student number: "+args[0]+" wasn't be found");
                    }
                    else{
                        String[] studentRow = formatStudent(results, rsmd);
                        outTo.output("User has student number: "+studentRow[0]+" name: "+studentRow[1]+" and email: "+studentRow[2]);
                    }
                    break;
            }
            connection.close();
        }catch (SQLException e){
            outTo.output(e.getMessage());
        } catch (DatabaseSelectException e) {
            outTo.output(e.getMessage());
        }
    }

    /**
     * assigns values to problem with the corresponding problem key
     * @param pKey primary key to identify the problem in database
     * @param searchProblem problem that will be populated and returned
     * @return problem with attributes corresponding to its primary key
     * @throws DatabaseSelectException
     * @throws SQLException
     */
    public Problem actOnDatabase(int pKey, Problem searchProblem)throws DatabaseSelectException, SQLException{
        Connection connection = DatabaseDriverAPI.connectOrCreateDataBase();
        // stores value returned from respective table
        ResultSet results;
        // store metadata for corresponding ResultSet
        ResultSetMetaData rsmd;
        results = DatabaseSelector.getProblemSet(pKey, connection);
        rsmd = results.getMetaData();
        String[] resultRow = new String[rsmd.getColumnCount()];
        while(results.next()){
            for (int col = 1; col <= rsmd.getColumnCount(); col++){
                resultRow[col] = results.getString(col);
            }
        }
        searchProblem = populateProblem(resultRow);
        return searchProblem;
    }

    /**
     * Gets all the problems in the database are organizes them into a list of problems
     * @param pList list of problem objects that will populated using info from database
     * @return list with all the problems in currently being stored in the database
     * @throws DatabaseSelectException
     * @throws SQLException
     */
    public List<Problem> actOnDatabase(List<Problem> pList) throws DatabaseSelectException, SQLException{
        Connection connection = DatabaseDriverAPI.connectOrCreateDataBase();
        // the query result will always be parsed into an Array list
        List<String[]> problems = new ArrayList<String[]>();
        // stores value returned from respective table
        ResultSet results;
        // store metadata for corresponding ResultSet
        ResultSetMetaData rsmd;
        results = DatabaseSelector.getAllProblems(connection);
        rsmd = results.getMetaData();
        formatResult(results, rsmd, problems);
        for (int problemIndex = 0; problemIndex < problems.size(); problemIndex++){
            pList.add(populateProblem(problems.get(problemIndex)));
        }
        return pList;
    }

    /**
     * Use information from array to assign attributes to Problem
     */
    private Problem populateProblem(String[] aRow){
        Problem newProblem;
        int pid = Integer.parseInt(aRow[0]);
        String question = aRow[1];
        String answer = aRow[2];
        newProblem = new SingleAnswerProblem(question, answer);
        newProblem.setId(pid);
        return newProblem;
    }

    /**
     * populates problems list with info stored in rs
     * @param rs the result set from requesting all problems
     * @param rm metadata for rs
     * @param problems list of string arrays each representing a row on problems table
     * @throws DatabaseSelectException
     * @throws SQLException
     */
    private void formatResult(ResultSet rs, ResultSetMetaData rm, List<String[]> problems)
            throws DatabaseSelectException, SQLException{
        // each problem will be an array, they will be stored into the list of problems
        // parse each result into problem attribute and add it to the list of problems
        String[] row;
        while (rs.next()){
            row = new String[rm.getColumnCount()];
            for(int col = 1; col <= rm.getColumnCount();col++){
                //System.out.print(rs.getString(col)+"   ");
                row[col - 1] = rs.getString(col);
            }
            //System.out.print("\n");
            problems.add(row);
        }
    }

    /**
     * parses info stored in rs into attributes for a student user
     * @param rs result set from requesting student with specific student number
     * @param rm metatata for rs
     * @return String array which has parsed attributes of a student user
     * @throws DatabaseSelectException
     * @throws SQLException
     */
    private String[] formatStudent(ResultSet rs, ResultSetMetaData rm)
            throws DatabaseSelectException, SQLException{
        // parse student into a string array
        String[] row = new String[rm.getColumnCount()];
        for(int col = 1; col <= rm.getColumnCount();col++){
            //System.out.print(rs.getString(col)+"   ");
            row[col - 1] = rs.getString(col);
        }
        //System.out.print("\n");
        return row;
    }
}