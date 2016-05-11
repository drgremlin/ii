package org.ayfaar.app.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ayfaar.app.model.Topic;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@Slf4j
public class ParserExcel {

    public static Map<String, Topic> parse() {
        Map<String,Topic> topicKeys = new HashMap<String, Topic>();
        Topic topic;
        InputStream in = null;
        XSSFWorkbook wb = null;
        int emptyCount = 0;

        try {
            in = ParserExcel.class.getResourceAsStream("/m.xlsx"); //from resource dir
            wb = new XSSFWorkbook(in);
        } catch (IOException e) {
            log.error("File is not find", e);
        }

        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> it = sheet.iterator();
        List<String> list = null;
        while (it.hasNext()) {
            int i = 0;
            Row row = it.next();
            Iterator<Cell> cells = row.iterator();
            list = new ArrayList<String>();
            while (cells.hasNext()) {

                Cell cell = cells.next();
                int cellType = cell.getCellType();
                switch (cellType) {
                    case Cell.CELL_TYPE_STRING:
                        if( i<2 )
                            list.add(cell.getStringCellValue());
                        i++;
                        break;
                    default:
                        if(i == 1){
                            list.add("EMPTY" + emptyCount); //if key is empty, add "EMPTY", если не нужно заменить " "
                            emptyCount++;
                            i++;
                        }
                        break;
                }
            }
            if (list.size() >= 1){
                topic = new Topic(list.get(0));
                topicKeys.put(list.get(1),topic);
            }
        }   return topicKeys;
    }
}
