package artiano.uiTest;

import java.awt.Color;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class RegressionView extends ApplicationFrame {
	private static final long serialVersionUID = 1L;
	
	//Data set
	private XYDataset data1;	
	
	/**
	 * constructor
	 * @param title - title of the frame
	 * @param seriesKey - series key
	 * @param dataArr - input data of regression
	 */
	public RegressionView(String title, String seriesKey, double[][] dataArr) {
		super(title);
		this.data1 = createDataset(dataArr, seriesKey);	//Create data set.
	}
	
	/**  
     * Creates a scatter plot chart based on the first data set  
     *  
     * @return scatter plot chart.  
     */
	public JFreeChart createScatterPlotChart() {
		//create plot
		NumberAxis xAxis = new NumberAxis("X");
		xAxis.setAutoRangeIncludesZero(false);
		NumberAxis yAxis = new NumberAxis("Y");
		yAxis.setAutoRangeIncludesZero(false);
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
		XYPlot plot = new XYPlot(data1, xAxis, yAxis, renderer);
		
		//create and return the chart panel
		JFreeChart chart = 
			new JFreeChart("Scattered Plot",JFreeChart.DEFAULT_TITLE_FONT, plot, true);		
		return chart;
	}
	
	/**
	 * Creates a scatter plot chart based on the first data set
	 * @param title - title of plot chart
	 * @param color - color of scatter shape
	 * @return scatter plot chart with specified title and color of scatter shape
	 */
	public JFreeChart createScatterPlotChart(String title, Color color) {
		//create plot
		NumberAxis xAxis = new NumberAxis("X");
		xAxis.setAutoRangeIncludesZero(false);
		NumberAxis yAxis = new NumberAxis("Y");
		yAxis.setAutoRangeIncludesZero(false);
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
		renderer.setSeriesPaint(0, color);		
		
		XYPlot plot = new XYPlot(data1, xAxis, yAxis, renderer);
		
		//create and return the chart panel
		JFreeChart chart = 
			new JFreeChart(title,JFreeChart.DEFAULT_TITLE_FONT, plot, true);		
		
		return chart;
	}

	/**  
     * Creates a chart based on the first data set, with a fitted regression line.  
     * @param - fittedDataArr - data selected to fit regression(linear or power regression)
     * @return the chart  
     * @throws IllegalArgumentException - when fittedDataArr inputed is empty
     */
	public JFreeChart createRegressionChart(double[][] fittedDataArr) {
		if(fittedDataArr == null) {
			throw new IllegalArgumentException("fittedDataArr should not be empty!");
		}
		
		//Get relative scattered panel
		JFreeChart chart = createScatterPlotChart(); 
		XYPlot plot = chart.getXYPlot();
				
		XYDataset data = createDataset(fittedDataArr, "Series 1");  //filtered data
		plot.setDataset(1, data);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, Color.blue);		
		
		plot.setRenderer(1, renderer);
			
		return chart;
	}
	
	/**  
     * Creates a chart based on the first data set, with a fitted regression line.  
     * @param - fittedDataArr - data selected to fit regression(linear or power regression)
     * @param - title - title of the chart 
     * @param - color - color of lines that connect points
     * @return the chart  
     * @throws IllegalArgumentException - when fittedDataArr inputed is empty
     */
	public JFreeChart createRegressionChart(double[][] fittedDataArr, String title, Color lineColor) {
		if(fittedDataArr == null) {
			throw new IllegalArgumentException("fittedDataArr should not be empty!");
		}
		
		//Get relative scattered panel
		JFreeChart chart = createScatterPlotChart();
		chart.setTitle(title);
		XYPlot plot = chart.getXYPlot();
				
		XYDataset data = createDataset(fittedDataArr, "Series 1");  //filtered data
		plot.setDataset(1, data);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, lineColor);				
		plot.setRenderer(1, renderer);
			
		return chart;
	}
	
	/**  
     * Creates a fitted regression chart.  
     * @param - data - data selected to fit regression(linear or power regression)
     * @return the chart  
     */
	public JFreeChart createRegressionChart(XYDataset data) {				
		JFreeChart chart = createScatterPlotChart(); //Get relative scattered panel
		XYPlot plot = chart.getXYPlot();
				
		plot.setDataset(1, data);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, Color.RED);				
		plot.setRenderer(1, renderer);				

		return chart;
	}	

	/**  
     * Creates a fitted regression chart.  
     * @param - data - data selected to fit regression(linear or power regression)
     * @param - title - title of the chart 
     * @param - color - color of lines that connect points
     * @return the chart  
     */
	public JFreeChart createRegressionChart(XYDataset data, String title, Color linesColor) {				
		JFreeChart chart = createScatterPlotChart(); //Get relative scattered panel
		chart.setTitle(title);
		XYPlot plot = chart.getXYPlot();
				
		plot.setDataset(1, data);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, linesColor);				
		plot.setRenderer(1, renderer);				

		return chart;
	}	

	
	/**
	 * create data set using array inputed 
	 * @param dataArr - input data array
	 * @param seriesTitle - series key 
	 * @return - data set
	 */
	public static XYDataset createDataset(double[][] dataArr, String seriesTitle) {
		if(dataArr[0].length != 2) {
			throw new IllegalArgumentException("Each row length of array \"dataArr\"" +
					" should be 2!");
		}
		
		XYSeries series = new XYSeries(seriesTitle);  //Series of points
		for(int i=0; i<dataArr.length; i++) {
			series.add(dataArr[i][0], dataArr[i][1]);  //Add a point
		}
		
		XYDataset result = new XYSeriesCollection(series);  //data set
		return result;
	}
}
