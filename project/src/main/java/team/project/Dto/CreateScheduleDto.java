package team.project.Dto;

import lombok.Data;
import team.project.Entity.Member;
import team.project.Entity.TeamEntity.DateSaveType;
import team.project.Entity.TeamEntity.Team;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
public class CreateScheduleDto {

    private Long groupId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private DateSaveType dateType;
    private String memo;

}
