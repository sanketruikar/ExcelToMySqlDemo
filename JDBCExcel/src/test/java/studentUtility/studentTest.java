package studentUtility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class studentTest {

	public static void main(String[] args) throws IOException, SQLException {
		String jdbcURL="jdbc:mysql://localhost:3308/sa";
		String userName="root";
		String password="";
		String exceFilePath="./data/StudentData.xlsx";
		int batchSize = 20;
		
		Connection con=null;
		
		long start = System.currentTimeMillis();
		
		FileInputStream file=new FileInputStream(exceFilePath);
		XSSFWorkbook workbook=new XSSFWorkbook(file);
		XSSFSheet sheet=workbook.getSheetAt(0);
		Iterator<Row> rowIterator=sheet.iterator();
		con=DriverManager.getConnection(jdbcURL,userName,password);
		con.setAutoCommit(false);
		
		String insertQuery="INSERT INTO students(name,enrolled,progress) VALUES (?,?,?)";
		PreparedStatement statement=con.prepareStatement(insertQuery);
		int count =0;
		rowIterator.next();
		
		while(rowIterator.hasNext())
		{
			Row nextRow=rowIterator.next();
			Iterator<Cell> cellIterator=nextRow.iterator();
			while(cellIterator.hasNext())
			{
				Cell nextCell=cellIterator.next();
				int columnIndex=nextCell.getColumnIndex();
				switch(columnIndex)
				{
				case 0:
					String name=nextCell.getStringCellValue();
					statement.setString(1, name);
					break;
				case 1:
					Date enrollDate=nextCell.getDateCellValue();
					statement.setTimestamp(2,new Timestamp(enrollDate.getTime()));
					break;
				case 2:
					int progress=(int)nextCell.getNumericCellValue();
					statement.setInt(3,progress);
					break;
				}
			}
			statement.addBatch();
			if(count%batchSize==0)
			{
				statement.executeBatch();
			}
			
		}
		workbook.close();
        statement.executeBatch();
        con.commit();
        con.close();
         
        long end = System.currentTimeMillis();
        System.out.printf("Import done in %d ms\n", (end - start));
		
		
		
		
		
		
		

	}

}
