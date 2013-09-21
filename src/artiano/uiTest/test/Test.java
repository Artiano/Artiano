package artiano.uiTest.test;

import java.awt.Color;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.function.Function2D;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.function.PowerFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;

import artiano.uiTest.RegressionView;

public class Test {

	// sample points
	final static double[][] samples = { { 2.0, 56.27 }, { 3.0, 41.32 },
			{ 4.0, 31.45 }, { 5.0, 30.05 }, { 6.0, 24.69 }, { 7.0, 19.78 },
			{ 8.0, 20.94 }, { 9.0, 16.73 }, { 10.0, 14.21 }, { 11.0, 12.44 } };

	public static void main(String[] args) {
		// testScatteredPoint();
		// testLinearRegression();
		// testPowerRegression();
	}

	
	public static void testScatteredPoint() {
		RegressionView view = new RegressionView("Scatter plot", "Points",
				samples);
		JFreeChart chart = view.createScatterPlotChart("Scatter plot",
				Color.BLUE); // Show scatter plot chart
		ChartPanel panel = new ChartPanel(chart);
		view.setContentPane(panel);
		view.pack();
		view.setLocationRelativeTo(null);
		view.setVisible(true);
	}

	public static void testLinearRegression() {
		// calculate the regression and create subplot 2...
		XYDataset originData = RegressionView
				.createDataset(samples, "Points 1");
		double[] coefficients = Regression.getOLSRegression(originData, 0);
		System.out.println(coefficients[0] + " " + coefficients[1]);
		Function2D curve = new LineFunction2D(coefficients[0], coefficients[1]);
		XYDataset regressionData = DatasetUtilities.sampleFunction2D(curve,
				2.0, 11.0, 100, "Fitted Regression Line");

		RegressionView view = new RegressionView("Linear Regression",
				"Points 1", samples);
		JFreeChart chart = view.createRegressionChart(regressionData,
				"Line Regression", Color.BLUE); // Show regression chart
		ChartPanel panel = new ChartPanel(chart);
		view.setContentPane(panel);
		view.pack();
		view.setLocationRelativeTo(null);
		view.setVisible(true);
	}

	public static void testPowerRegression() {
		// calculate the regression and create subplot 2...
		XYDataset originData = RegressionView.createDataset(samples, "Points");

		// calculate the regression and create subplot 2...
		double[] coefficients = Regression.getPowerRegression(originData, 0);
		Function2D curve = new PowerFunction2D(coefficients[0], coefficients[1]);
		XYDataset regressionData = DatasetUtilities.sampleFunction2D(curve,
				2.0, 11.0, 100, "Fitted Regression Line");
		RegressionView view = new RegressionView("Power Regression", "Points",
				samples);
		JFreeChart chart = view.createRegressionChart(regressionData); // Show
																		// regression
																		// chart
		ChartPanel panel = new ChartPanel(chart);
		view.setContentPane(panel);
		view.pack();
		view.setLocationRelativeTo(null);
		view.setVisible(true);
	}
}
