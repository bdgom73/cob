package team.project.Entity.TeamEntity;

public enum JoinState {

    OK("가입") ,

    WAITING("대기"),

    BAN("밴");


    String state;

    JoinState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
