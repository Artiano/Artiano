package artiano.uiTest;

import java.awt.Color;

import org.jfree.chart.ChartPanel;
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
	 */
	public RegressionView(String title) {
		super(title);		
	}
	
	/**
	 * constructor
	 * @param title - title of the frame
	 * @param dataArr - input data of regression
	 */
	public RegressionView(String title, double[][] dataArr) {
		this(title);
		this.data1 = createDataset(dataArr);	//Create data set.
	}
	
	/**  
     * Creates a scatter plot chart based on the first data set  
     *  
     * @return the chart panel.  
     */
	public ChartPanel createScatterPlotPanel() {
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
		ChartPanel panel = new ChartPanel(chart);
		return panel;
	}
	
	/**  
     * Creates a chart based on the first data set, with a fitted regression line.  
     * @param - fittedDataArr - data selected to fit regression(linear or power regression)
     * @return the chart panel.  
     * @throws IllegalArgumentException - when fittedDataArr inputed is empty
     */
	public ChartPanel createChartPanel(double[][] fittedDataArr) {
		if(fittedDataArr == null) {
			throw new IllegalArgumentException("fittedDataArr should not be empty!");
		}
		
		//Get relative scattered panel
		ChartPanel scatteredPanel = createScatterPlotPanel(); 
		XYPlot plot = scatteredPanel.getChart().getXYPlot();
				
		XYDataset data = createDataset(fittedDataArr);  //filtered data
		plot.setDataset(1, data);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, Color.blue);				
		plot.setRenderer(1, renderer);
		
		//create and return the chart panel
		JFreeChart chart = 
			new JFreeChart("Regression",JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		ChartPanel panel = new ChartPanel(chart);
		return panel;
	}		
	
	/**  
     * Creates a fitted regression chart.  
     * @param - data - data selected to fit regression(linear or power regression)
     * @return the chart panel.  
     */
	public ChartPanel createChartPanel(XYDataset data) {				
		ChartPanel scatteredPanel = createScatterPlotPanel(); //Get relative scattered panel
		XYPlot plot = scatteredPanel.getChart().getXYPlot();
				
		plot.setDataset(1, data);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, Color.blue);				
		plot.setRenderer(1, renderer);
		
		//create and return the chart panel
		JFreeChart chart = 
			new JFreeChart("Regression",JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		ChartPanel panel = new ChartPanel(chart);
		return panel;
	}	
	
	/**
	 * create data set using array inputed 
	 * @param dataArr - input data array
	 * @return - data set
	 */
	public static XYDataset createDataset(double[][] dataArr) {
		if(dataArr[0].length != 2) {
			throw new IllegalArgumentException("Each row length of array \"dataArr\"" +
					" should be 2!");
		}
		
		XYSeries series = new XYSeries("Series 1");  //Series of points
		for(int i=0; i<dataArr.length; i++) {
			series.add(dataArr[i][0], dataArr[i][1]);  //Add a point
		}
		
		XYDataset result = new XYSeriesCollection(series);  //data set
		return result;
	}
}
