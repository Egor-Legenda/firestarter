package service;


import itmo.programming.service.ExcelValidationService;
import org.apache.poi.EmptyFileException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExcelValidationServiceTest {

    @InjectMocks
    private ExcelValidationService validationService;

    private byte[] createValidExcelFile() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            var sheet = workbook.createSheet("Sheet1");
            var row1 = sheet.createRow(0);
            row1.createCell(0).setCellValue("A1");
            row1.createCell(1).setCellValue("B1");
            row1.createCell(2).setCellValue("C1");

            var row2 = sheet.createRow(1);
            row2.createCell(0).setCellValue("A2");
            row2.createCell(1).setCellValue("B2");
            row2.createCell(2).setCellValue("C2");

            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Test
    void shouldValidateValidExcelContent() throws Exception {
        byte[] excelContent = createValidExcelFile();

        assertDoesNotThrow(() ->
                validationService.validateExcelContent(excelContent, "test.xlsx"));
    }

    @Test
    void shouldThrowExceptionForEmptyExcel() {
        byte[] emptyContent = new byte[0];

        assertThrows(EmptyFileException.class, () ->
                validationService.validateExcelContent(emptyContent, "test.xlsx"));
    }
}
