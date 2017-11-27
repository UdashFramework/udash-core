/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config

import scala.scalajs.js
import scala.scalajs.js.|

@js.annotation.ScalaJSDefined
trait Data extends js.Object {

  /**
    * A two-dimensional array representing the input data on tabular form. This input can be used when the data is already parsed,
    * for example from a grid view component. Each cell can be a string or number. If not switchRowsAndColumns is set, the columns are interpreted as series.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/columns/" target="_blank">Columns</a>
    */
  val columns: js.UndefOr[Data.DateType] = js.undefined

  /**
    * The callback that is evaluated when the data is finished loading, optionally from an external source, and parsed.
    * The first argument passed is a finished chart options object, containing the series. These options can be extended
    * with additional options and passed directly to the chart constructor.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/complete/" target="_blank">Modify data on complete</a>
    */
  val complete: js.UndefOr[js.Function1[HighchartsConfig, Any]] = js.undefined

  /**
    * <p>A comma delimited string to be parsed. Related options are <a href="#data.startRow">startRow</a>,
    * <a href="#data.endRow">endRow</a>, <a href="#data.startColumn">startColumn</a> and <a href="#data.endColumn">endColumn</a>
    * to delimit what part of the table is used. The <a href="#data.lineDelimiter">lineDelimiter</a> and <a href="#data.itemDelimiter">itemDelimiter</a>
    * options define the CSV delimiter formats.</p>
    * 
    * <p>The built-in CSV parser doesn't support all flavours of CSV, so in some cases it may be necessary to use an external CSV parser.
    * See <a href="http://jsfiddle.net/highcharts/u59176h4/">this example</a> of parsing CSV through the MIT licensed <a href="http://papaparse.com/">Papa Parse</a> library.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/csv/" target="_blank">Data from CSV</a>
    */
  val csv: js.UndefOr[String] = js.undefined

  /**
    * <p>Which of the predefined date formats in Date.prototype.dateFormats to use to parse date values.
    * Defaults to a best guess based on what format gives valid and ordered dates.</p>
    * 
    * <p>Valid options include:
    * <ul>
    * <li><code>YYYY-mm-dd</code></li>
    * <li><code>dd/mm/YYYY</code></li>
    * <li><code>mm/dd/YYYY</code></li>
    * <li><code>dd/mm/YY</code></li>
    * <li><code>mm/dd/YY</code></li>
    * </ul>
    * </p>
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/dateformat-auto/" target="_blank">Best guess date format</a>
    */
  val dateFormat: js.UndefOr[String] = js.undefined

  /**
    * The decimal point used for parsing numbers in the CSV.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/delimiters/" target="_blank">Comma as decimal point</a>
    */
  val decimalPoint: js.UndefOr[String] = js.undefined

  /**
    * In tabular input data, the last column (indexed by 0) to use. Defaults to the last column containing data.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/start-end/" target="_blank">Limited data</a>
    */
  val endColumn: js.UndefOr[Double] = js.undefined

  /**
    * In tabular input data, the last row (indexed by 0) to use. Defaults to the last row containing data.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/start-end/" target="_blank">Limited data</a>
    */
  val endRow: js.UndefOr[Double] = js.undefined

  /**
    * Whether to use the first row in the data set as series names. 
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/start-end/" target="_blank">Don't get series names from the CSV</a>
    */
  val firstRowAsNames: js.UndefOr[Boolean] = js.undefined

  /**
    * The key for a Google Spreadsheet to load. See <a href="https://developers.google.com/gdata/samples/spreadsheet_sample">general information on GS</a>.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/google-spreadsheet/" target="_blank">Load a Google Spreadsheet</a>
    */
  val googleSpreadsheetKey: js.UndefOr[String] = js.undefined

  /**
    * The Google Spreadsheet worksheet to use in combination with <a href="#data.googleSpreadsheetKey">googleSpreadsheetKey</a>.
    * The available id's from your sheet can be read from <code>https://spreadsheets.google.com/feeds/worksheets/{key}/public/basic</code>
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/google-spreadsheet/" target="_blank">Load a Google Spreadsheet</a>
    */
  val googleSpreadsheetWorksheet: js.UndefOr[String] = js.undefined

  /**
    * Item or cell delimiter for parsing CSV. Defaults to the tab character <code>\t</code> if a tab character
    * is found in the CSV string, if not it defaults to <code>,</code>.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/delimiters/" target="_blank">Delimiters</a>
    */
  val itemDelimiter: js.UndefOr[String] = js.undefined

  /**
    * Line delimiter for parsing CSV.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/delimiters/" target="_blank">Delimiters</a>
    */
  val lineDelimiter: js.UndefOr[String] = js.undefined

  /**
    * A callback function to parse string representations of dates into JavaScript timestamps. Should return an integer timestamp on success.
    */
  val parseDate: js.UndefOr[js.Function1[String, Int]] = js.undefined

  /**
    * A callback function to access the parsed columns, the two-dimentional input data array directly, before they are interpreted
    * into series data and categories. Return <code>false</code> to stop completion, or call <code>this.complete()</code> to continue async.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/parsed/" target="_blank">Modify data after parse</a>
    */
  val parsed: js.UndefOr[js.Function1[Data.DateType, Boolean]] = js.undefined

  /**
    * The same as the columns input option, but defining rows intead of columns.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/rows/" target="_blank">Data in rows</a>
    */
  val rows: js.UndefOr[Data.DateType] = js.undefined

  /**
    * An array containing object with Point property names along with what column id the property should be taken from.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/seriesmapping-label/" target="_blank">Label from data set</a>
    */
  val seriesMapping: js.UndefOr[js.Array[js.Object]] = js.undefined

  /**
    * In tabular input data, the first column (indexed by 0) to use.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/start-end/" target="_blank">Limited data</a>
    */
  val startColumn: js.UndefOr[Double] = js.undefined

  /**
    * In tabular input data, the first row (indexed by 0) to use.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/start-end/" target="_blank">Limited data</a>
    */
  val startRow: js.UndefOr[Double] = js.undefined

  /**
    * Switch rows and columns of the input data, so that <code>this.columns</code> effectively becomes the rows of the data set, and the rows are interpreted as series.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/switchrowsandcolumns/" target="_blank">Switch rows and columns</a>
    */
  val switchRowsAndColumns: js.UndefOr[Boolean] = js.undefined

  /**
    * A HTML table or the id of such to be parsed as input data. Related options are <code>startRow</code>, <code>endRow</code>, <code>startColumn</code> and <code>endColumn</code> to delimit what part of the table is used.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/column-parsed/" target="_blank">Parsed table</a>
    */
  val table: js.UndefOr[String] = js.undefined
}

object Data {
  import scalajs.js.JSConverters._

  type DateType = js.Array[js.Array[String | Double]]
  type WrapperDateType = Seq[Seq[String | Double]]

  /**
    * @param columns A two-dimensional array representing the input data on tabular form. This input can be used when the data is already parsed, for example from a grid view component. Each cell can be a string or number. If not switchRowsAndColumns is set, the columns are interpreted as series.
    * @param complete The callback that is evaluated when the data is finished loading, optionally from an external source, and parsed. The first argument passed is a finished chart options object, containing the series. These options can be extended with additional options and passed directly to the chart constructor.
    * @param csv <p>A comma delimited string to be parsed. Related options are <a href="#data.startRow">startRow</a>, <a href="#data.endRow">endRow</a>, <a href="#data.startColumn">startColumn</a> and <a href="#data.endColumn">endColumn</a> to delimit what part of the table is used. The <a href="#data.lineDelimiter">lineDelimiter</a> and <a href="#data.itemDelimiter">itemDelimiter</a> options define the CSV delimiter formats.</p>. . <p>The built-in CSV parser doesn't support all flavours of CSV, so in some cases it may be necessary to use an external CSV parser. See <a href="http://jsfiddle.net/highcharts/u59176h4/">this example</a> of parsing CSV through the MIT licensed <a href="http://papaparse.com/">Papa Parse</a> library.</p>
    * @param dateFormat <p>Which of the predefined date formats in Date.prototype.dateFormats to use to parse date values. Defaults to a best guess based on what format gives valid and ordered dates.</p>. . <p>Valid options include:. <ul>. <li><code>YYYY-mm-dd</code></li>. <li><code>dd/mm/YYYY</code></li>. <li><code>mm/dd/YYYY</code></li>. <li><code>dd/mm/YY</code></li>. <li><code>mm/dd/YY</code></li>. </ul>. </p>
    * @param decimalPoint The decimal point used for parsing numbers in the CSV.
    * @param endColumn In tabular input data, the last column (indexed by 0) to use. Defaults to the last column containing data.
    * @param endRow In tabular input data, the last row (indexed by 0) to use. Defaults to the last row containing data.
    * @param firstRowAsNames Whether to use the first row in the data set as series names. 
    * @param googleSpreadsheetKey The key for a Google Spreadsheet to load. See <a href="https://developers.google.com/gdata/samples/spreadsheet_sample">general information on GS</a>.
    * @param googleSpreadsheetWorksheet The Google Spreadsheet worksheet to use in combination with <a href="#data.googleSpreadsheetKey">googleSpreadsheetKey</a>. The available id's from your sheet can be read from <code>https://spreadsheets.google.com/feeds/worksheets/{key}/public/basic</code>
    * @param itemDelimiter Item or cell delimiter for parsing CSV. Defaults to the tab character <code>\t</code> if a tab character is found in the CSV string, if not it defaults to <code>,</code>.
    * @param lineDelimiter Line delimiter for parsing CSV.
    * @param parseDate A callback function to parse string representations of dates into JavaScript timestamps. Should return an integer timestamp on success.
    * @param parsed A callback function to access the parsed columns, the two-dimentional input data array directly, before they are interpreted into series data and categories. Return <code>false</code> to stop completion, or call <code>this.complete()</code> to continue async.
    * @param rows The same as the columns input option, but defining rows intead of columns.
    * @param seriesMapping An array containing object with Point property names along with what column id the property should be taken from.
    * @param startColumn In tabular input data, the first column (indexed by 0) to use.
    * @param startRow In tabular input data, the first row (indexed by 0) to use.
    * @param switchRowsAndColumns Switch rows and columns of the input data, so that <code>this.columns</code> effectively becomes the rows of the data set, and the rows are interpreted as series.
    * @param table A HTML table or the id of such to be parsed as input data. Related options are <code>startRow</code>, <code>endRow</code>, <code>startColumn</code> and <code>endColumn</code> to delimit what part of the table is used.
    */
  def apply(columns: js.UndefOr[WrapperDateType] = js.undefined,
            complete: js.UndefOr[(HighchartsConfig) => Any] = js.undefined,
            csv: js.UndefOr[String] = js.undefined,
            dateFormat: js.UndefOr[String] = js.undefined,
            decimalPoint: js.UndefOr[String] = js.undefined,
            endColumn: js.UndefOr[Double] = js.undefined,
            endRow: js.UndefOr[Double] = js.undefined,
            firstRowAsNames: js.UndefOr[Boolean] = js.undefined,
            googleSpreadsheetKey: js.UndefOr[String] = js.undefined,
            googleSpreadsheetWorksheet: js.UndefOr[String] = js.undefined,
            itemDelimiter: js.UndefOr[String] = js.undefined,
            lineDelimiter: js.UndefOr[String] = js.undefined,
            parseDate: js.UndefOr[(String) => Int] = js.undefined,
            parsed: js.UndefOr[(DateType) => Boolean] = js.undefined,
            rows: js.UndefOr[WrapperDateType] = js.undefined,
            seriesMapping: js.UndefOr[Seq[js.Object]] = js.undefined,
            startColumn: js.UndefOr[Double] = js.undefined,
            startRow: js.UndefOr[Double] = js.undefined,
            switchRowsAndColumns: js.UndefOr[Boolean] = js.undefined,
            table: js.UndefOr[String] = js.undefined): Data = {
    val columnsOuter = columns.map(_.map(_.toJSArray).toJSArray)
    val completeOuter = complete.map(f => js.Any.fromFunction1(f))
    val csvOuter = csv
    val dateFormatOuter = dateFormat
    val decimalPointOuter = decimalPoint
    val endColumnOuter = endColumn
    val endRowOuter = endRow
    val firstRowAsNamesOuter = firstRowAsNames
    val googleSpreadsheetKeyOuter = googleSpreadsheetKey
    val googleSpreadsheetWorksheetOuter = googleSpreadsheetWorksheet
    val itemDelimiterOuter = itemDelimiter
    val lineDelimiterOuter = lineDelimiter
    val parseDateOuter = parseDate.map(f => js.Any.fromFunction1(f))
    val parsedOuter = parsed.map(f => js.Any.fromFunction1(f))
    val rowsOuter = rows.map(_.map(_.toJSArray).toJSArray)
    val seriesMappingOuter = seriesMapping.map(_.toJSArray)
    val startColumnOuter = startColumn
    val startRowOuter = startRow
    val switchRowsAndColumnsOuter = switchRowsAndColumns
    val tableOuter = table
    new Data {
      override val columns = columnsOuter
      override val complete = completeOuter
      override val csv = csvOuter
      override val dateFormat = dateFormatOuter
      override val decimalPoint = decimalPointOuter
      override val endColumn = endColumnOuter
      override val endRow = endRowOuter
      override val firstRowAsNames = firstRowAsNamesOuter
      override val googleSpreadsheetKey = googleSpreadsheetKeyOuter
      override val googleSpreadsheetWorksheet = googleSpreadsheetWorksheetOuter
      override val itemDelimiter = itemDelimiterOuter
      override val lineDelimiter = lineDelimiterOuter
      override val parseDate = parseDateOuter
      override val parsed = parsedOuter
      override val rows = rowsOuter
      override val seriesMapping = seriesMappingOuter
      override val startColumn = startColumnOuter
      override val startRow = startRowOuter
      override val switchRowsAndColumns = switchRowsAndColumnsOuter
      override val table = tableOuter
    }
  }
}
