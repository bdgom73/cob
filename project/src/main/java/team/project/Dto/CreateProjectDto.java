package team.project.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class CreateProjectDto {

    private String title;
    private String introduction;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long teamId;


}
