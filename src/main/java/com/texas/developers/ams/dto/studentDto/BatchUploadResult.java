package com.texas.developers.ams.dto.studentDto;

import java.util.List;

public record BatchUploadResult(
        boolean success,
        int savedCount,
        int errorCount,
        List<String> errors,
        String message
) {
    public static BatchUploadResult failure(String msg) {
        return new BatchUploadResult(false, 0, 0, List.of(), msg);
    }
}