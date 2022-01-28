package team.project.Entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class ProjectCategory {

    @Id @GeneratedValue
    @Column(name = "project_category_id")
    private Long id;
    private String name;

}
