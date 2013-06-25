package br.ufrj.ppgi.greco.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Classe para comunicação com o banco de dados de configuração
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class UtilDatabase
{

	public static Connection conn;

	
	/**
	 *Retorna um objeto de conexão com o banco de dados
	 *@return Connection conexão com o banco de dados
	 */
	public static Connection getConnection()
	{
		if (conn == null)
		{
			try
			{
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/semantichub", "root", "root");
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		return conn;
	}
	
	/**
	 *Retorna um conjunto de objetos após a execução de uma consulta
	 *@param query consulta a ser realizada sobre o banco de dados
	 *@return ResultSet conjunto de resultados
	 */
	public static ResultSet executeQuery(String query)
	{
		ResultSet result = null;
		
		try
		{
			Statement stmt = UtilDatabase.getConnection().createStatement();
			result = stmt.executeQuery(query);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 *Retorna um conjunto de objetos após a execução de uma série de consultas
	 *@param query template da consulta
	 *@param params parâmetro da consulta
	 *@return ResultSet conjunto de resultados
	 */
	public static ResultSet executeQuery(String query, Object[] params) // param ordered by insertion order
	{
		ResultSet result = null;
		
		try
		{
			Statement stmt = UtilDatabase.getConnection().createStatement();
			String sql =  String.format(query, params);
		
			result = stmt.executeQuery(sql);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
}
