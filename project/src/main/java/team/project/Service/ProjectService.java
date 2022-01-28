package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.project.Entity.Project;
import team.project.Repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public void createProject(){

    }

}
