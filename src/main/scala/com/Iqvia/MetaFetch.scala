package com.Iqvia

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.sql.SQLException
import java.io.FileOutputStream
import java.sql.DatabaseMetaData
import java.sql.{Connection, DriverManager}

import scala.collection.mutable.ListBuffer

case class Processing ( attr : String, value : String)

case class Table ( tableName : String, columns : List[Column])

case class Column ( column : String, dataType : String , fieldLength : Option[Int],values : String)

object MetaFetch {

  val FILE_NAME = "C:\\Users\\vyalla\\Desktop\\MetaData.xlsx"

  def main(args: Array[String]): Unit = {

    val url = "jdbc:oracle:thin:system/B2440586_EWDI_DEV@cdtsrac710p.rxcorp.com:1521/PAA"
    val driver = "oracle.jdbc.driver.OracleDriver"
    val username = "B2440586_EWDI_DEV"
    val password = "B2440586"
    var connection: Connection = null
    val rsmd = null
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)

    val columns = new ListBuffer[String]()
    var tableName: String = null
    val tablesList = new ListBuffer[Table]()
    //val colList = new ListBuffer[Column]()
    var tablesM = scala.collection.mutable.Map[String, List[String]]()
    val tableSheets = tablesList.drop(1).toList
    val workbookOut = new XSSFWorkbook()
    val paramSheet = workbookOut.createSheet("ProcessingParameters")

    val procRow = paramSheet.createRow(0)
    procRow.createCell(0).setCellValue("Attribute")
    procRow.createCell(1).setCellValue("Value")

    val processingParms = Map(
      "NPI" -> "Number",
      "Source" -> "GILEAD",
      "Physician" -> "Name",
      "Specialist" -> "HEMATOLOGY, ONCOLOGY",
      "Address" -> "Varchar2",
      "City" -> "Varchar2",
      "Zipcode" -> "Number"
    )
    val l = processingParms.keysIterator.toList
    processingParms.foreach { x =>
      val row = paramSheet.createRow(l.indexOf(x._1) + 1)
      row.createCell(0).setCellValue(x._1)
      row.createCell(1).setCellValue(x._2)
    }

    val sheet = workbookOut.createSheet("DataFeeds")
    val ftpRow = sheet.createRow(0)
    ftpRow.createCell(0).setCellValue("Attribute")
    ftpRow.createCell(1).setCellValue("Value")

    val hdfsRow = sheet.createRow(1)
    hdfsRow.createCell(0).setCellValue("HDFSSuccess")
    hdfsRow.createCell(1).setCellValue("")

    val hdfsFailure = sheet.createRow(2)
    hdfsFailure.createCell(0).setCellValue("HDFSFailure")
    hdfsFailure.createCell(1).setCellValue("")

    val treashold = sheet.createRow(3)
    treashold.createCell(0).setCellValue("Threshold")
    treashold.createCell(1).setCellValue("LessThan")
    treashold.createCell(2).setCellValue(0.5)
    treashold.createCell(3).setCellValue("GraterThan")
    treashold.createCell(4).setCellValue(1)

      val statement = connection.createStatement
      val resultSet = statement.executeQuery("SELECT * FROM B2440586_KITE_DLBCL.statistics_final@PAA")
      val rs = resultSet.getMetaData()
      if (rs != null) {
        val col = rs.getColumnCount()

        val row1 = sheet.createRow(4)
        row1.createCell(0).setCellValue("Column Count")
        row1.createCell(1).setCellValue(rs.getColumnCount())
        for (i <- 1 to col by 1) {
          val row2 = sheet.createRow(5)
          row2.createCell(0).setCellValue("Column Name")
          for (j <- 1 to col by 1)
            row2.createCell(j).setCellValue(rs.getColumnName(j))
        }
        for (i <- 1 to col by 1) {
          val row3 = sheet.createRow(6)
          row3.createCell(0).setCellValue("Column Type Name")
          for (k <- 1 to col by 1)
            row3.createCell(k).setCellValue(rs.getColumnTypeName(k))
        }
      }
     try {
        val outputStream = new FileOutputStream(FILE_NAME)
        workbookOut.write(outputStream)
      }
      catch {
        case e: SQLException =>
          e.printStackTrace
      }
      println("Excel Created");
    }
}
