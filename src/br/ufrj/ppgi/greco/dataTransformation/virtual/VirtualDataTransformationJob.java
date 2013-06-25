package br.ufrj.ppgi.greco.dataTransformation.virtual;

import java.util.ArrayList;
import java.util.HashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.ufrj.ppgi.greco.util.config.ConfigVirtualTransformation;
import br.ufrj.ppgi.greco.util.pkg.PackageDataTransformed;
import br.ufrj.ppgi.greco.util.queue.QueueDataTransformed;
import br.ufrj.ppgi.greco.util.rdf.TripleStoreDriver;
import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;


/**
 *JOB de transformação e geração de dados virtuais
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class VirtualDataTransformationJob implements Job
{

	/**
	 *Job da tarefa de criação de dados virtuais
	 *@param context Contexto de execução do Job.
	 *@throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{		
		JobDataMap dm = context.getJobDetail().getJobDataMap();
		ConfigVirtualTransformation cvt = (ConfigVirtualTransformation) dm.get("config");
		ArrayList<PackageDataTransformed> queueDTF = QueueDataTransformed.getQueue();

		
		//getting values
		HashMap<String,String> params = new HashMap<String, String>();
		for(String key : cvt.getFormulaParameters().keySet() )
		{
			String res =TripleStoreDriver.sparqlExecGetLiteral(cvt.getFormulaParameters().get(key));
			params.put(key, res);
		}
		
		if(params.size() <= 0)
		{
			return ;
		}
		
		String resultFormula = this.calc(cvt.getFormula(), params);
			
		if(cvt.isConditional())
		{
			
			HashMap<String,String> conditions =  cvt.getConditions();
			String result = "";
			
			for(String condition: conditions.keySet())
			{
				System.out.println("CONDITIONAL: "+ condition);
				System.out.println("RESULT FORMULA: "+ resultFormula);
				
				if(this.evaluateConditional(condition,resultFormula) )
				{					
					result = conditions.get(condition);
					break;
				}	
			}

			//if(cvt.getConditionalType().equals("literal"))
			//{
				PackageDataTransformed resource = new PackageDataTransformed();
				resource.setUnique(true);
				resource.insertSPL(cvt.getParentUri(), cvt.getParentProperty(), result);
				System.out.println(resource);
				
				queueDTF.add(resource);
			/*}	
			else //object
			{
				PackageDataTransformed resource = new PackageDataTransformed();
				resource.setUnique(true);
				resource.insertSPO(cvt.getParentUri(), cvt.getParentProperty(), result);
				queueDTF.add(resource);
			}*/	
		}
		else
		{
			PackageDataTransformed resource = new PackageDataTransformed();
			resource.setUnique(true);
			resource.insertSPL(cvt.getParentUri(), cvt.getParentProperty(), resultFormula);
			queueDTF.add(resource);
		}
			
	}

	/**
	 *Executa o cálculo para geração de dados virtuais
	 *@param formula formula de geração de dados propriamente dita
	 *@param params parâmetros e valores associados para cálculo da fórmula
	 */
	private String calc(String formula , HashMap<String, String> params)
	{
		String result = "";
	
		// build formula
		ExpressionBuilder exp = new ExpressionBuilder(formula);

		// setting parameters
		for (String key : params.keySet())
		{
			exp.withVariable(key, Double.parseDouble(params.get(key)));
		}

		try
		{
			// making calculus
			Calculable calc = exp.build();
			result = Double.toString(calc.calculate());
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	/**
	 *Avalia as condições de acordo com os valores da gerados pela execução da fórmula
	 *@param expression expressão a ser avaliada 
	 *@param value valor que será aplicado a expressão
	 */
	private boolean evaluateConditional(String expression , String value)
	{
		boolean eval = false;
		
		try
		{
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
			String condition = "("+expression.replaceAll("result", value)+")";
			System.out.println("CONDITION: "+ condition);
			eval = (Boolean)  engine.eval(condition);
		} 
		catch (ScriptException e)
		{
			e.printStackTrace();
		}

		return eval;
	}

}
