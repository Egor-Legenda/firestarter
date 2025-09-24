package itmo.programming.service;

import itmo.programming.exception.FileValidationException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Сервис для валидации содержимого Excel файлов.
 * Проверяет, что на первом листе заполнены первые 3 колонки в первых 2 строках.
 */
@Service
public class ExcelValidationService {

    private static final int REQUIRED_ROWS = 2;
    private static final int REQUIRED_COLUMNS = 3;

    /**
     * Валидация содержимого Excel файла.
     * Проверяет, что на первом листе заполнены первые 3 колонки в первых 2 строках
     *
     * @param fileContent содержимое файла в виде массива байт
     * @param fileName имя файла для определения формата
     */
    public void validateExcelContent(byte[] fileContent, String fileName) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileContent);
             Workbook workbook = createWorkbook(bis, fileName)) {

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet == null) {
                throw new FileValidationException("Excel file does not contain any sheets");
            }

            validateSheetStructure(sheet);

        } catch (IOException e) {
            throw new FileValidationException("Error reading Excel file: " + e.getMessage(), e);
        }
    }

    /**
     * Создание Workbook в зависимости от формата файла.
     *
     * @param bis поток с содержимым файла
     * @param fileName имя файла для определения формата
     */
    private Workbook createWorkbook(ByteArrayInputStream bis, String fileName) throws IOException {
        if (fileName.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(bis);
        } else if (fileName.toLowerCase().endsWith(".xls")) {
            return new HSSFWorkbook(bis);
        } else {
            throw new FileValidationException("Unsupported file format: " + fileName);
        }
    }

    /**
     * Валидация структуры листа.
     *
     * @param sheet лист для проверки
     */
    private void validateSheetStructure(Sheet sheet) {
        for (int rowNum = 0; rowNum < REQUIRED_ROWS; rowNum++) {
            Row row = sheet.getRow(rowNum);

            if (row == null) {
                throw new FileValidationException(
                        String.format("Row %d is empty. First %d rows must have data.",
                                rowNum + 1, REQUIRED_ROWS));
            }

            for (int colNum = 0; colNum < REQUIRED_COLUMNS; colNum++) {
                Cell cell = row.getCell(colNum);

                if (cell == null || isCellEmpty(cell)) {
                    throw new FileValidationException(
                            String.format("Cell [%d,%d] is empty. First %d columns must be filled.",
                                    rowNum + 1, colNum + 1, REQUIRED_COLUMNS));
                }
            }
        }
    }

    /**
     * Проверка, что ячейка пуста или содержит только пробелы.
     */
    private boolean isCellEmpty(Cell cell) {
        if (cell.getCellType() == CellType.BLANK) {
            return true;
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim().isEmpty();
        }

        return false;
    }
}
