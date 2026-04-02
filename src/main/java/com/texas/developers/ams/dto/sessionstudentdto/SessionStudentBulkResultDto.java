package com.texas.developers.ams.dto.sessionstudentdto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SessionStudentBulkResultDto {
    private boolean success;
    private String message;
    private int addedCount;
    private int skippedCount;
    private List<String> conflicts;
}

