package dependencies

object TablePrinter {

  def format(table: Seq[Seq[String]]) = table match {
    case Seq() => ""
    case _ =>
      val sizes = table map {
        _.map { cell =>
          Option(cell) map (_.length) getOrElse 0
        }
      }
      val colSizes = sizes.transpose map (_.max)
      val rows = table.zipWithIndex.map {
        case (row, index) => formatRow(row, colSizes, index == 0)
      }
      formatRows(rowSeparator(colSizes), rows)
  }

  def formatRows(rowSeparator: String, rows: Seq[String]): String =
    (rowSeparator ::
      rows.head ::
        rowSeparator ::
          rows.tail.toList :::
            rowSeparator ::
              List()).mkString("\n")

  def formatRow(row: Seq[String], colSizes: Seq[Int], header: Boolean) = {
    val cells = row.zip(colSizes) map {
      case (item, size) if size == 0 => ""
      case (item, size)              => ("%" + size + "s").format(item)
    }
    cells.mkString("|", "|", "|")
  }

  def rowSeparator(colSizes: Seq[Int]) = colSizes map { "-" * _ } mkString ("+", "+", "+")

}
