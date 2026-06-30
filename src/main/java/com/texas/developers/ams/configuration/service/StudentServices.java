package com.texas.developers.ams.configuration.service;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import com.texas.developers.ams.converter.StudentConverter;
import com.texas.developers.ams.dto.studentDto.BatchUploadResult;
import com.texas.developers.ams.dto.studentDto.StudentExcelRow;
import com.texas.developers.ams.dto.studentDto.StudentRequestDto;
import com.texas.developers.ams.dto.studentDto.StudentUpdateDto;
import com.texas.developers.ams.entity.Student;
import com.texas.developers.ams.enums.Faculty;
import com.texas.developers.ams.repo.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentServices {
    private final StudentRepository studentRepository;
    private final StudentConverter studentConverter;

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".xlsx", ".xls", ".csv");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void addStudent(StudentRequestDto studentRequestDto) {
        Student student = studentConverter.toEntity(studentRequestDto);
        studentRepository.save(student);
    }

    public Student getStudentById(Integer id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void updateStudent(StudentUpdateDto student) {

    }

    public void deleteStudentById(Integer id) {
        studentRepository.deleteById(id);
    }

    public BatchUploadResult uploadStudents(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return BatchUploadResult.failure("Please choose a file to upload.");
        }

        String filename = Optional.ofNullable(file.getOriginalFilename()).orElse("");
        String normalizedFilename = filename.toLowerCase(Locale.ROOT);
        String extension = resolveExtension(normalizedFilename);

        if (extension == null) {
            return BatchUploadResult.failure("Invalid file type. Only .xlsx, .xls, or .csv files are accepted.");
        }

        List<ParsedStudentRow> rows = ".csv".equals(extension)
                ? parseCsvRows(file)
                : parseExcelRows(file, normalizedFilename);

        if (rows.isEmpty()) {
            return BatchUploadResult.failure("File contains no data rows.");
        }

        List<String> errors = new ArrayList<>();
        List<StudentRequestDto> validDtos = new ArrayList<>();
        Set<String> fileEmails = new HashSet<>();

        for (ParsedStudentRow row : rows) {
            List<String> rowErrors = new ArrayList<>();

            String fullName = normalize(row.fullName());
            String email = normalize(row.email());
            String mobileNumber = normalize(row.mobileNumber());
            String facultyRaw = normalize(row.faculty());
            String collegeJoinDateRaw = normalize(row.collegeJoinDate());

            if (isBlank(fullName)) rowErrors.add("full_name is required");
            if (isBlank(email)) rowErrors.add("email is required");
            if (isBlank(mobileNumber)) rowErrors.add("mobile_number is required");
            if (isBlank(facultyRaw)) rowErrors.add("faculty is required");
            if (isBlank(collegeJoinDateRaw)) rowErrors.add("college_join_date is required");

            if (!rowErrors.isEmpty()) {
                errors.add("Row " + row.lineNumber() + ": " + String.join(", ", rowErrors));
                continue;
            }

            Faculty faculty;
            try {
                faculty = Faculty.valueOf(facultyRaw.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                errors.add("Row " + row.lineNumber() + ": Invalid faculty '" + facultyRaw
                        + "'. Allowed values: " + Arrays.toString(Faculty.values()));
                continue;
            }

            LocalDate joinDate;
            try {
                joinDate = LocalDate.parse(collegeJoinDateRaw, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                errors.add("Row " + row.lineNumber() + ": Invalid date '" + collegeJoinDateRaw
                        + "'. Expected format: yyyy-MM-dd");
                continue;
            }

            String normalizedEmail = email.toLowerCase(Locale.ROOT);
            if (!fileEmails.add(normalizedEmail)) {
                errors.add("Row " + row.lineNumber() + ": Duplicate email '" + email + "' in uploaded file.");
                continue;
            }

            if (Boolean.TRUE.equals(studentRepository.existsByEmail(email))) {
                errors.add("Row " + row.lineNumber() + ": Email '" + email + "' already exists in the database.");
                continue;
            }

            StudentRequestDto dto = new StudentRequestDto();
            dto.setFullName(fullName);
            dto.setEmail(email);
            dto.setMobileNumber(mobileNumber);
            dto.setFaculty(faculty.name());
            dto.setCollegeJoinDate(joinDate);
            validDtos.add(dto);
        }

        if (validDtos.isEmpty()) {
            return new BatchUploadResult(false, 0, errors.size(), errors,
                    "No valid rows found. Please fix the file and try again.");
        }

        List<Student> students = validDtos.stream().map(studentConverter::toEntity).toList();
        studentRepository.saveAll(students);

        String msg = students.size() + " student(s) imported successfully."
                + (errors.isEmpty() ? "" : " " + errors.size() + " row(s) had errors and were skipped.");

        return new BatchUploadResult(true, students.size(), errors.size(), errors, msg);
    }

    // ── Generate Sample Excel ─────────────────────────────────────────────────

    public byte[] generateSampleExcel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Header row — order must match @ExcelCell indices in StudentExcelRow
            Row header = sheet.createRow(0);
            String[] columns = {
                    "full_name",
                    "email",
                    "mobile_number",
                    "faculty",
                    "college_join_date"
            };
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5500);
            }

            // Sample data rows — faculty values must match your Faculty enum exactly
            String facultyExample = Faculty.values()[0].name();
            Object[][] data = {
                    {"Ram Kumar",  "ramkumar@example.com",  "9800000001", facultyExample, "2024-09-01"},
                    {"Hari Bahadur",      "hari.bahadur@example.com",    "9800000002", facultyExample, "2023-09-01"},
                    {"Shri krishna",    "shreekrishna@example.com",  "9800000003", facultyExample, "2025-01-15"},
            };
            for (int r = 0; r < data.length; r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < data[r].length; c++) {
                    row.createCell(c).setCellValue(data[r][c].toString());
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private List<ParsedStudentRow> parseExcelRows(MultipartFile file, String normalizedFilename) throws IOException {
        PoijiExcelType excelType = normalizedFilename.endsWith(".xls") ? PoijiExcelType.XLS : PoijiExcelType.XLSX;
        PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().headerCount(1).build();

        try (InputStream is = file.getInputStream()) {
            List<StudentExcelRow> rows = Poiji.fromExcel(is, excelType, StudentExcelRow.class, options);
            List<ParsedStudentRow> parsedRows = new ArrayList<>();
            for (StudentExcelRow row : rows) {
                parsedRows.add(new ParsedStudentRow(
                        row.getRowNumber() + 1,
                        row.getFullName(),
                        row.getEmail(),
                        row.getMobileNumber(),
                        row.getFaculty(),
                        row.getCollegeJoinDate()
                ));
            }
            return parsedRows;
        } catch (Exception e) {
            throw new IOException("Could not parse Excel file: " + e.getMessage(), e);
        }
    }

    private List<ParsedStudentRow> parseCsvRows(MultipartFile file) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = format.parse(reader)) {
            List<ParsedStudentRow> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                rows.add(new ParsedStudentRow(
                        (int) record.getRecordNumber() + 1,
                        readCsvValue(record, "full_name"),
                        readCsvValue(record, "email"),
                        readCsvValue(record, "mobile_number"),
                        readCsvValue(record, "faculty"),
                        readCsvValue(record, "college_join_date")
                ));
            }
            return rows;
        } catch (IllegalArgumentException e) {
            throw new IOException("CSV header is invalid. Expected: full_name, email, mobile_number, faculty, college_join_date", e);
        }
    }

    private String readCsvValue(CSVRecord record, String column) {
        return record.isMapped(column) ? record.get(column) : null;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String resolveExtension(String filename) {
        return ALLOWED_EXTENSIONS.stream().filter(filename::endsWith).findFirst().orElse(null);
    }

    private record ParsedStudentRow(
            int lineNumber,
            String fullName,
            String email,
            String mobileNumber,
            String faculty,
            String collegeJoinDate
    ) {
    }
}
