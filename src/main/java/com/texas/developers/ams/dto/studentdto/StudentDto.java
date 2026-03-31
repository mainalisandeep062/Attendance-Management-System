package com.texas.developers.ams.dto.studentdto;

import com.texas.developers.ams.enums.Faculty;
import lombok.*;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class StudentDto {

        private String fullName;
        private String email;
        private String mobileNumber;
        private Faculty faculty;
    }

