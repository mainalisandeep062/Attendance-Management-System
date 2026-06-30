package com.texas.developers.ams.dto.studentDto;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelRow;
import lombok.Data;

@Data
public class StudentExcelRow {

    @ExcelRow
    private int rowNumber;

    @ExcelCell(0)
    private String fullName;

    @ExcelCell(1)
    private String email;

    @ExcelCell(2)
    private String mobileNumber;

    @ExcelCell(3)
    private String faculty;

    @ExcelCell(4)
    private String collegeJoinDate;
}