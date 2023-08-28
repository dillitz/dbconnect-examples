import com.databricks.connect.DatabricksSession
import org.apache.spark.sql.SparkSession
import org.jfree.chart.{ChartFactory, ChartFrame, ChartUtils}
import org.jfree.data.category.DefaultCategoryDataset
import java.io.File

object Taxi {
  def createSession(): SparkSession = {
    DatabricksSession.builder.remote().getOrCreate()
  }

  def main(args: Array[String]): Unit = {
    val spark = createSession()

    val data = spark.sql("""
        SELECT
          date_format(tpep_pickup_datetime, "HH") AS `Pickup Hour`,
          count(*) AS `Number of Rides`
        FROM
          samples.nyctaxi.trips
        GROUP BY 1
        ORDER BY 1
        """).collect()

    val dataset = new DefaultCategoryDataset();
    data.foreach(row => dataset.addValue(row(1).asInstanceOf[Long], "Trips", row(0).toString))

    val chart = ChartFactory.createBarChart("Number of trips per hour",
      "trips",
      "hour",
      dataset)

    ChartUtils.saveChartAsPNG(new File("plot.png"), chart, 600, 500);
    val frame = new ChartFrame("Results", chart)
    frame.setSize(600, 500)
    frame.setVisible(true)
  }
}
