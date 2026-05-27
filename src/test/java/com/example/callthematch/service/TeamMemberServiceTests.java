package com.example.callthematch.service;

import com.example.callthematch.model.Competition;
import com.example.callthematch.model.MyUser;
import com.example.callthematch.model.Prediction;
import com.example.callthematch.model.Team;
import com.example.callthematch.model.TeamMember;
import com.example.callthematch.repository.PredictionRepository;
import com.example.callthematch.repository.TeamMemberRepository;
import com.example.callthematch.repository.TeamRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TeamMemberServiceTests {

   /* private final TeamMemberRepository teamMemberRepository = mock(TeamMemberRepository.class);
    private final TeamRepository teamRepository = mock(TeamRepository.class);
    private final PredictionRepository predictionRepository = mock(PredictionRepository.class);
    private final TeamMemberService teamMemberService = new TeamMemberService(
            teamMemberRepository,
            teamRepository,
            predictionRepository,
            new ScoringService());

    @Test
    void recalculatesPredictionBasePointsMemberScoresAndTeamTotalAfterResult() {
        Competition competition = Competition.builder()
                .scoreA(2)
                .scoreB(1)
                .build();
        MyUser exactUser = MyUser.builder().id(1L).email("exact@example.com").build();
        MyUser outcomeUser = MyUser.builder().id(2L).email("outcome@example.com").build();
        Prediction exactPrediction = prediction(exactUser, competition, 2, 1);
        Prediction outcomePrediction = prediction(outcomeUser, competition, 3, 1);
        Team team = Team.builder().id(10L).build();
        TeamMember exactMember = teamMember(exactUser, team);
        TeamMember outcomeMember = teamMember(outcomeUser, team);
        List<TeamMember> teamMembers = List.of(exactMember, outcomeMember);

        when(predictionRepository.findAllByCompetition(competition))
                .thenReturn(List.of(exactPrediction, outcomePrediction));
        when(teamMemberRepository.findAllByUserId(1L)).thenReturn(List.of(exactMember));
        when(teamMemberRepository.findAllByUserId(2L)).thenReturn(List.of(outcomeMember));
        when(teamMemberRepository.findAllByTeam(team)).thenReturn(teamMembers);
        when(predictionRepository.findAllByUser(exactUser)).thenReturn(List.of(exactPrediction));
        when(predictionRepository.findAllByUser(outcomeUser)).thenReturn(List.of(outcomePrediction));
        when(predictionRepository.findByUserAndCompetition(exactUser, competition))
                .thenReturn(Optional.of(exactPrediction));
        when(predictionRepository.findByUserAndCompetition(outcomeUser, competition))
                .thenReturn(Optional.of(outcomePrediction));

        teamMemberService.recalculateScoresAfterResult(competition);

        assertThat(exactPrediction.getPointsEarned()).isEqualTo(5);
        assertThat(outcomePrediction.getPointsEarned()).isEqualTo(2);
        assertThat(exactMember.getScore()).isEqualTo(8);
        assertThat(outcomeMember.getScore()).isEqualTo(2);
        assertThat(team.getScore()).isEqualTo(10);
        verify(predictionRepository).saveAll(List.of(exactPrediction, outcomePrediction));
        verify(teamMemberRepository).saveAll(teamMembers);
        verify(teamRepository).save(team);
    }

    private Prediction prediction(MyUser user, Competition competition, int predictedScoreA, int predictedScoreB) {
        return Prediction.builder()
                .user(user)
                .competition(competition)
                .predictedScoreA(predictedScoreA)
                .predictedScoreB(predictedScoreB)
                .build();
    }

    private TeamMember teamMember(MyUser user, Team team) {
        return TeamMember.builder()
                .user(user)
                .team(team)
                .score(0)
                .build();
    }*/
}
