package com.texas.developers.ams.dto.sessionstudentdto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SessionStudentBulkRequestDto {

    @NotEmpty(message = "Select at least one student.")
    private List<Integer> studentIds;
}

