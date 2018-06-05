package dao;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Damir on 10/24/2016.
 *
 * Simple dao class that wraps calls to DB and returns generic list of hashmaps. If things get complex we can introduce
 * additonal DAO objects for different operations however for now (and most likely forever) this is more then enough
 */
public class DAO {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(DAO.class);

    private String _dbDriver;
    private String _dbConnection;
    private String _dbUser;
    private String _dbPassword;

    public DAO(String dbConnection,String dbUser, String dbPassword)
    {
        _dbDriver = "com.mysql.jdbc.Driver"; //only mysql is supported here;
        _dbConnection = dbConnection;
        _dbUser = dbUser;
        _dbPassword = dbPassword;
    }

    //close instance without parameters
    private DAO(){}

    public List<Map<String,Object>> executeQuery(String sql, Object[] parameters) throws Exception {
        Connection dbConnection = null;
        List<Map<String,Object>> resultData = new ArrayList<Map<String, Object>>();
        PreparedStatement queryStatement = null;

        try {
            dbConnection = getConnection();

            queryStatement = generatePreparedStatement(sql, parameters, dbConnection);

            // execute select SQL statement
            ResultSet rs = queryStatement.executeQuery();

            Map<String, Object> row = null;

            ResultSetMetaData metaData = rs.getMetaData();
            Integer columnCount = metaData.getColumnCount();

            while (rs.next()) {
                row = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {

                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(columnName);
                    row.put(columnName, columnValue);
                }

                resultData.add(row);
            }

        } catch (SQLException e) {

            LOG.error(e.getMessage(),e);
            throw e;
        } finally {
            try {
                if (queryStatement != null) {
                    queryStatement.close();
                }

                if (dbConnection != null) {
                    dbConnection.close();
                }
            } catch (SQLException e) {
                LOG.error("Error on connection hadling!",e);
                throw new Exception("Error in connection handling! Check local logs for details");
            }
        }

        return resultData;
    }

    public void executeUpdate(String sql, Object[] parameters)
    {
        Connection dbConnection = null;
        List<Map<String,Object>> resultData = new ArrayList<Map<String, Object>>();
        PreparedStatement insertStatement = null;

        try {
            dbConnection = getConnection();
            insertStatement = generatePreparedStatement(sql, parameters, dbConnection);
            // execute select SQL statement
            insertStatement.executeUpdate();

        } catch (SQLException e) {
            LOG.error(e.getMessage(),e);
        } finally {
            try {
                if (insertStatement != null) {
                    insertStatement.close();
                }
                if (dbConnection != null) {
                    dbConnection.close();
                }
            } catch (SQLException e) {
                LOG.error("Error on connection hadling!",e);
            }
        }

    }

    public int executeInsert(String sql, Object[] parameters) throws SQLException {
        Connection dbConnection = null;
        List<Map<String,Object>> resultData = new ArrayList<Map<String, Object>>();
        PreparedStatement insertStatement = null;

        try {
            dbConnection = getConnection();
            insertStatement = generatePreparedStatement(sql, parameters, dbConnection);
            // execute select SQL statement
            insertStatement.executeUpdate();
            ResultSet rs = insertStatement.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            rs.close();
            return id;

        } catch (SQLException e) {
            LOG.error(e.getMessage(),e);
            throw e;
        } finally {
            try {
                if (insertStatement != null) {
                    insertStatement.close();
                }
                if (dbConnection != null) {
                    dbConnection.close();
                }
            } catch (SQLException e) {
                LOG.error("Error on connection hadling!",e);
            }
        }

    }

    public void executeDelete(String sql, Object[] parameters)
    {
        Connection dbConnection = null;
        List<Map<String,Object>> resultData = new ArrayList<Map<String, Object>>();
        PreparedStatement insertStatement = null;

        try {
            dbConnection = getConnection();
            insertStatement = generatePreparedStatement(sql, parameters, dbConnection);
            // execute select SQL statement
            insertStatement.executeUpdate();

        } catch (SQLException e) {
            LOG.error(e.getMessage(),e);
        } finally {
            try {
                if (insertStatement != null) {
                    insertStatement.close();
                }
                if (dbConnection != null) {
                    dbConnection.close();
                }
            } catch (SQLException e) {
                LOG.error("Error on connection hadling!",e);
            }
        }

    }

    private PreparedStatement generatePreparedStatement(String sql, Object[] parameters, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);

        if(parameters!=null){
            for(int i = 0;i<parameters.length;i++)
            {
                statement.setObject(i+1,parameters[i]);
            }
        }

        return statement;
    }

    public void batchInsert(String sql, List<Object[]> parameters) throws SQLException {

        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            dbConnection = getConnection();
            preparedStatement = dbConnection.prepareStatement(sql);

            dbConnection.setAutoCommit(false);

            for (Object[] parameterGroup : parameters) {
                for(int i = 0;i<parameterGroup.length;i++)
                {
                    preparedStatement.setObject(i+1,parameterGroup[i]);
                }
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

            dbConnection.commit();

        } catch (SQLException e) {
            LOG.error(e.getMessage(),e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (dbConnection != null) {
                    dbConnection.close();
                }
            } catch (SQLException e) {
                LOG.error("Error on connection handling!",e);
            }
        }

    }

    private Connection getConnection(){
        Connection dbConnection = null;

        try {
            Class.forName(_dbDriver);

        } catch (ClassNotFoundException e) {
            LOG.error(e.getMessage(),e);
        }
        try {
            dbConnection = DriverManager.getConnection(_dbConnection, _dbUser, _dbPassword);
            return dbConnection;

        } catch (SQLException e) {
            LOG.error(e.getMessage(),e);
        }

        return dbConnection;
    }

}
